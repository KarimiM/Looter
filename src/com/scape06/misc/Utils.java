package com.scape06.misc;

import java.util.ArrayList;

import org.parabot.environment.api.utils.Time;
import org.parabot.environment.scripts.framework.SleepCondition;
import org.rev317.min.api.methods.Game;
import org.rev317.min.api.methods.Inventory;
import org.rev317.min.api.methods.Menu;
import org.rev317.min.api.methods.SceneObjects;
import org.rev317.min.api.wrappers.Item;
import org.rev317.min.api.wrappers.SceneObject;

/**
 * A utility class containing useful methods.
 * @author Masood
 *
 */
public class Utils {

	/**
	 * Deposits all items except the listed ids.
	 * @param itemIDs the item ids to not deposit.
	 */
	public static void depositAllExcept(int... itemIDs) {
		final ArrayList<Integer> dontDeposit = new ArrayList<Integer>();
		if (Inventory.getCount(false) <= 2) {
			return;
		} else {
			for (int i : itemIDs) {
				dontDeposit.add(i);
			}
		}
		for (Item inventoryItem : Inventory.getItems()) {
			if (!dontDeposit.contains(inventoryItem.getId())) {
				Menu.sendAction(431, inventoryItem.getId() - 1, inventoryItem.getSlot(), 5064, 2213, 3);
				Time.sleep(80);
			}
		}
	}
	/**
	 * A wrapper sleep method.
	 * @param condition the condition.
	 */
	public static void sleep(boolean condition) {
		sleep(condition, 30000);
	}

	/**
	 * Calls a sleep condition.
	 * @param condition the condition to meet.
	 */
	public static void sleep(final boolean condition, int timeout) {
		Time.sleep(new SleepCondition() {
			@Override
			public boolean isValid() {
				return condition;
			}
		}, timeout);
	}
	/**
	 * Calls an interaction with a bank booth.
	 */
	public static void bank() {
		SceneObject[] booth = SceneObjects.getNearest(11758);
		if (Game.getOpenInterfaceId() != 5292) {
			if (booth.length > 0) {
				while (Game.getOpenInterfaceId() != 5292 && booth[0] != null) {
					booth[0].interact(1);
					SleepCondition condition = new SleepCondition() {
						@Override
						public boolean isValid() {
							return Game.getOpenInterfaceId() == 5292;
						}
					};
					Time.sleep(condition, 8000);
				}
			}
		}
	}

	/**
	 * Formats a number as a string.
	 * @param number the number to format.
	 * @return the string formatted.
	 */
	public static String formatNumb(int number) {
		String numberString = String.valueOf(number);
		if (Integer.parseInt(numberString) < 1000) {
			return numberString;
		} else if (Integer.parseInt(numberString) > 1000 && Integer.parseInt(numberString) < 10000) {
			return numberString.charAt(0) + "." + numberString.charAt(1) + "k";
		} else if (Integer.parseInt(numberString) > 10000 && Integer.parseInt(numberString) < 100000) {
			return numberString.substring(0, 2) + "." + numberString.charAt(2) + "k";
		} else if (Integer.parseInt(numberString) > 100000 && Integer.parseInt(numberString) < 1000000) {
			return numberString.substring(0, 3) + "." + numberString.charAt(3) + "k";
		} else if (Integer.parseInt(numberString) > 1000000 && Integer.parseInt(numberString) < 10000000) {
			return numberString.charAt(0) + "." + numberString.charAt(1) + "m";
		} else if (Integer.parseInt(numberString) > 10000000 && Integer.parseInt(numberString) < 100000000) {
			return numberString.substring(0, 2) + "." + numberString.charAt(2) + "m";
		} else if (Integer.parseInt(numberString) > 100000000 && Integer.parseInt(numberString) < 1000000000) {
			return numberString.substring(0, 3) + "." + numberString.charAt(3) + "m";
		} else if (Long.valueOf(numberString) > 1000000000L && Long.valueOf(numberString) < 10000000000L) {
			return numberString.charAt(0) + "." + numberString.charAt(1) + "b";
		} else if (Long.valueOf(numberString) > 10000000000L && Long.valueOf(numberString) < 100000000000L) {
			return numberString.substring(0, 2) + "." + numberString.charAt(2) + "b";
		} else {
			return numberString;
		}
	}

}
