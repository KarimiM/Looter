package com.scape06.misc;

import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.framework.Strategy;
/**
 * An anti macro class for 2006scape.
 * @author Masood
 *
 */
public class Click implements Strategy {
	
	private static Timer clickTime = new Timer();

	@Override
	public boolean activate() {		
		return clickTime.getElapsedTime() >= 60000;
	}

	@Override
	public void execute() {	
		//clicks the inventory tab to counteract the zZz event on 2006scape.
		Mouse.getInstance().click(650, 180, true);
		clickTime.reset();
		clickTime.restart();
	}
}
