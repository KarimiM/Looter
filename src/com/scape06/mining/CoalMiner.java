package com.scape06.mining;

import static org.rev317.min.api.methods.Players.getMyPlayer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.api.events.MessageEvent;
import org.rev317.min.api.events.listeners.MessageListener;
import org.rev317.min.api.methods.Bank;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Npcs;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.methods.Skill;
import org.rev317.min.api.wrappers.Npc;
import org.rev317.min.api.wrappers.SceneObject;
import org.rev317.min.api.wrappers.Tile;

import com.scape06.misc.Click;
import com.scape06.misc.ScriptTimer;
import com.scape06.misc.Utils;
import com.scape06.misc.Zone;

/**
 * A coal mining script.
 * @author Masood
 *
 */
@ScriptManifest(author = "Empathy", category = Category.MINING, description = "Mines coal at mining guild", name = "06CoalMiner", servers = { "2006Scape" }, version = 2.2)
public class CoalMiner extends Script implements Paintable, MessageListener {
	public static final Zone FALLY_BANK = new Zone(new Tile(3009, 3358), new Tile(3018, 3354));
	/**
	 * The coal rock ids.
	 */
	private static final int COAL_ROCK[] = { 2096, 2097 };

	/**
	 * The climb-down ladder object id.
	 */
	private static final int LADDER = 2113;

	/**
	 * The climb-up ladder object id.
	 */
	private static final int CLIMB = 1755;

	/**
	 * The pickaxe id.
	 */
	private static final int PICKAXE = 1276;

	/**
	 * The coal item id.
	 */
	private static final int COAL = 454;

	/**
	 * The script timer used to time mining data.
	 */
	private final ScriptTimer timer = new ScriptTimer(Skill.MINING, 1500); // 1500xp/coal

	/**
	 * Represents if we're mining or not.
	 */

	@Override
	public boolean onExecute() {		
		provide(Arrays.asList(new Click(), new AntiRandom(), new Banking(), new Walk(), new Mine()));
		return true;
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D gr = (Graphics2D) g1;
		gr.setColor(Color.WHITE);
		gr.setFont(new Font("Verdana", 0, 12));
		gr.drawString("By: Empathy", 333, 120);
		gr.drawString("Runtime: " + timer.toString(), 333, 100);
		gr.drawString("Coal Mined: " + Utils.formatNumb(timer.getProductAmount()) + "(" + Utils.formatNumb(timer.getPerHour((timer.getProductAmount()))) + "/hr)", 333, 80);
		gr.drawString("XP Gained: " + Utils.formatNumb(timer.getXpGained()) + " ( " + Utils.formatNumb(timer.getPerHour((timer.getXpGained()))) + " /hr)", 333, 60);
		gr.drawRect(330, 6, 183, 130);
		Graphics2D rect = (Graphics2D) g1;
		rect.setColor(new Color(0, 0, 0, 120));
		rect.fillRect(330, 6, 183, 130);
	}

	/**
	 * A strategy class used to bank coal.
	 * @author Masood
	 *
	 */
	public class Banking implements Strategy {

		@Override
		public boolean activate() {
			return Inventory.isFull();
		}

		@Override
		public void execute() {
			SceneObject l = SceneObjects.getClosest(CLIMB);
			
			//toggles run
			Mouse.getInstance().click(710, 480, true);
			Mouse.getInstance().click(625, 265, true);
			
			// walks to ladder
			System.out.println("Walking to ladder");
			
			//walks to ladder
			new Tile(3021, 9739).walkTo();
			SleepCondition ladder = new SleepCondition() {
				@Override
				public boolean isValid() {
					return getMyPlayer().getLocation().equals(new Tile(3021, 9739));
				}
			};
			Time.sleep(ladder, 30000);

			// interacts with ladder
			if (l != null) {
				System.out.println("Interacting with ladder");
				l.interact(0);
				//do not use conditional sleep here, it will mess up the script
				Time.sleep(2500);
			}

			// walks to bank
			if (Inventory.isFull() && getMyPlayer().getLocation().equals(new Tile(3021, 3339))) {
				System.out.println("Walking to bank");
				new Tile(3014, 3355).walkTo();
				SleepCondition walkBank = new SleepCondition() {
					@Override
					public boolean isValid() {
						return getMyPlayer().getLocation().equals(new Tile(3014, 3355));
					}
				};
				Time.sleep(walkBank, 30000);
			}

			// opens bank
			if (Game.getOpenInterfaceId() != 5292 && Inventory.isFull() && FALLY_BANK.inTheZone()) {
				System.out.println("Opening the bank");
				Utils.bank();
			}
			// banks
			if (Game.getOpenInterfaceId() == 5292) {
				System.out.println("Banking");
				Utils.depositAllExcept(PICKAXE);
				SleepCondition banking = new SleepCondition() {
					@Override
					public boolean isValid() {
						return Inventory.getCount(COAL) == 0;
					}
				};
				Time.sleep(banking, 30000);
				Bank.close();
				SleepCondition close = new SleepCondition() {
					@Override
					public boolean isValid() {
						return Game.getOpenInterfaceId() != 5292;
					}
				};
				Time.sleep(close, 30000);
			}
		}
	}

	/**
	 * A strategy class used to walk to the coal rocks.
	 * @author Masood
	 *
	 */
	public class Walk implements Strategy {
		@Override
		public boolean activate() {
			return !Bank.isOpen() && Inventory.getCount(COAL) == 0 && FALLY_BANK.inTheZone();
		}

		@Override
		public void execute() {
			SceneObject l = SceneObjects.getClosest(LADDER);
			// walk to ladder
			if (!Bank.isOpen() && Inventory.getCount(COAL) == 0 && FALLY_BANK.inTheZone()) {
				System.out.println("Walking to ladder");
				new Tile(3019, 3341).walkTo();
				SleepCondition walkLad = new SleepCondition() {
					@Override
					public boolean isValid() {
						return getMyPlayer().getLocation().equals(new Tile(3019, 3341));
					}
				};
				Time.sleep(walkLad, 30000);

				// interacts with ladder
				if (l != null) {
					System.out.println("Interacting with ladder");
					l.interact(0);
					// do not use conditional sleep here, it will mess up the script
					Time.sleep(2500);
				}
				// walking to rocks
				if (getMyPlayer().getLocation().equals(new Tile(3019, 9741))) {
					new Tile(3029, 9737).walkTo();
					System.out.println("Walking to coal");
					SleepCondition walkCoal = new SleepCondition() {
						@Override
						public boolean isValid() {
							return getMyPlayer().getLocation().equals(new Tile(3029, 9737));
						}
					};
					Time.sleep(walkCoal, 30000);
					
					//toggle run
					Mouse.getInstance().click(710, 480, true);
					Mouse.getInstance().click(580, 265, true);
				}
			}
		}
	}

	/**
	 * A strategy class used to mine coal.
	 * @author Masood
	 *
	 */
	public class Mine implements Strategy {

		@Override
		public boolean activate() {
			Npc[] golem = {};
			try {
			golem = Npcs.getNearest(8648);
			} catch (NullPointerException e) {
				System.out.println("Null pointer caught");
			}
			SceneObject[] c = SceneObjects.getNearest(COAL_ROCK);
			return Inventory.getCount(COAL) < 27 && c.length > 0 && golem.length < 1 && !getMyPlayer().isInCombat();
		}

		@Override
		public void execute() {
			// mine coal
			final SceneObject[] c = SceneObjects.getNearest(COAL_ROCK);
			if (c.length > 0 && getMyPlayer().getAnimation() == -1) {
				if (c[0] != null) {				
					// Objects must be clicked twice on 2006scape. 					 
					// clicks the object if not next to it, allows for first click
					if (c[0].distanceTo() > 1) {
						System.out.println("Clicking coal");
						try {
							c[0].interact(0);
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("ArrayIndexOutOfBoundsException Caught");
						}
						SleepCondition wait = new SleepCondition() {
							@Override
							public boolean isValid() {
								return c[0].distanceTo() < 2;
							}
						};
						Time.sleep(wait, 30000);
					}
					//allows for second click
					// clicks the object if right next to it
					if (c[0].distanceTo() < 2) {
						System.out.println("Mining...");
						try {
							c[0].interact(0);
							//static sleep added incase of lagg whilst clicking the rock
							Time.sleep(1000);
						} catch (ArrayIndexOutOfBoundsException e) {
							System.out.println("ArrayIndexOutOfBoundsException Caught");
						}
						SleepCondition mine = new SleepCondition() {
							@Override
							public boolean isValid() {
								return getMyPlayer().getAnimation() == -1;
							}
						};
						Time.sleep(mine, 30000);
					}
				}
			}
		}
	}
	
	/**
	 * A strategy class used to run away from the mining random.
	 * @author Masood
	 *
	 */
	public class AntiRandom implements Strategy {

		@Override
		public boolean activate() {
			Npc[] golem = {};
			try {
			golem = Npcs.getNearest(8648);
			} catch (NullPointerException e) {
				System.out.println("Null pointer caught");
			}
			return golem.length > 0 || (getMyPlayer().isInCombat() && !FALLY_BANK.inTheZone());
		}

		@Override
		public void execute() {
			SceneObject l = SceneObjects.getClosest(CLIMB);
			SceneObject d = SceneObjects.getClosest(LADDER);
			// walks to ladder
			System.out.println("Running from golem");
			new Tile(3021, 9739).walkTo();
			SleepCondition ladder = new SleepCondition() {
				@Override
				public boolean isValid() {
					return getMyPlayer().getLocation().equals(new Tile(3021, 9739));
				}
			};
			Time.sleep(ladder, 30000);

			// interacts with ladder
			if (l != null) {
				l.interact(0);
				//use static sleep here
				Time.sleep(2500);
			}
			// interacts with ladder
			if (d != null) {
				d.interact(0);
				//use static sleep here
				Time.sleep(2500);
			}

			// walking to rocks
			if (getMyPlayer().getLocation().equals(new Tile(3021, 9739))) {
				new Tile(3029, 9737).walkTo();
				System.out.println("Walking to coal, golem should be gone");
				SleepCondition walkCoal = new SleepCondition() {
					@Override
					public boolean isValid() {
						return getMyPlayer().getLocation().equals(new Tile(3029, 9737));
					}
				};
				Time.sleep(walkCoal, 30000);
			}
		}
	}
	@Override
	public void messageReceived(MessageEvent m) {
		/*
		 * currently doesn't work
		if (m.getType() == 0) {
			if ((m.getMessage().contains("manage to mine some"))) {
			}
		}
		*/
	}
}