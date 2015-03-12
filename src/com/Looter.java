package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Time;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.input.Keyboard;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.min.Loader;
import org.rev317.min.api.methods.GroundItems;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.wrappers.GroundItem;

@ScriptManifest(author = "Empathy", category = Category.OTHER, description = "Picks up stuff", name = "DropLooter", servers = { "Ikov" }, version = 2.0)
public class Looter extends Script implements Paintable {
	public static Timer time;
	private final int[] ITEMS = { 11283, 11732, 11726, 21787, 11716, 21371, 13742, 21561, 21557, 1055, 21793, 13740, 14484, 11694, 11696, 11698, 11700, 1050, 21559, 7668, 1053, 13738, 11724, 19784, 1645, 2572, 13893, 995, 20135, 20139, 21539, 21541, 20143, 20171, 20147, 20151, 20155, 20159, 20163, 20167, 1057, 10348, 10346, 19311, 19317, 21537, 21545, 21547, 21541, 19314, 15220, 6585, 11725, 11727 };

	public boolean onExecute() {
		time = new Timer();
		provide(Arrays.asList(new Relog(), new Loot()));
		return true;
	}

	public class Loot implements Strategy {
		@Override
		public boolean activate() {
			GroundItem[] toPickup = GroundItems.getNearest(ITEMS);
			return toPickup.length > 0 && toPickup[0] != null && toPickup[0].distanceTo() < 7;
		}

		@Override
		public void execute() {
			final GroundItem[] toPickup = GroundItems.getNearest(ITEMS);
			if (toPickup.length > 0 && !Inventory.isFull() && toPickup[0] != null) {
				Menu.sendAction(234, toPickup[0].getId(), toPickup[0].getX(), toPickup[0].getY());
				Time.sleep(new SleepCondition() {
					@Override
					public boolean isValid() {
						return toPickup[0] == null;
					}
				}, 1500);
			}
		}
	}

	public class Relog implements Strategy {
		@Override
		public boolean activate() {
			return !isLoggedIn();
		}

		@Override
		public void execute() {
			if (!isLoggedIn()) {
				Time.sleep(5000);
				Keyboard.getInstance().clickKey(KeyEvent.VK_ENTER);
				Time.sleep(new SleepCondition() {
					@Override
					public boolean isValid() {
						return isLoggedIn();
					}
				}, 5000);
				if (isLoggedIn()) {
					Time.sleep(4000);
					Menu.sendAction(679, 17825792, 113, 4907, 1088, 1);
					Time.sleep(500);
				}
			}
		}
	}

	public static boolean isLoggedIn() {
		try {
			Field f = Loader.getClient().getClass().getDeclaredField("bz");
			f.setAccessible(true);
			return f.getBoolean(Loader.getClient());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D gr = (Graphics2D) g1;
		gr.setColor(Color.WHITE);
		gr.setFont(new Font("Verdana", 0, 12));
		gr.drawString("By: Empathy", 333, 120);
		gr.drawString("Runtime: " + time.toString(), 333, 100);
		gr.drawRect(330, 6, 183, 130);
		Graphics2D rect = (Graphics2D) g1;
		rect.setColor(new Color(0, 0, 0, 120));
		rect.fillRect(330, 6, 183, 130);
	}
}
