package com.scape06.misc;

import org.parabot.environment.api.utils.Timer;
import org.rev317.min.api.methods.Skill;

/**
 * A wrapper class used to time a skill.
 * @author Masood
 *
 */
public class ScriptTimer extends Timer {

	/**
	 * The skill to time.
	 */
	private final Skill skill;

	/**
	 * The start exp.
	 */
	private final double startExp;

	/**
	 * The exp gained per product.
	 */
	private final int expPerProduct;

	/**
	 * Constructs a new script timer.
	 * @param skill the skill to time.
	 * @Param expPerProduct the exp per product.
	 */
	public ScriptTimer(Skill skill, int expPerProduct) {
		this.skill = skill;
		this.expPerProduct = expPerProduct;
		this.startExp = skill.getExperience();
	}
	
	/**
	 * Gets the product amount.
	 * @return the product amount.
	 */
	public int getProductAmount() {
		return getXpGained() / expPerProduct;
	}

	/**
	 * Gets the exp gained over time.
	 * @return the exp gained.
	 */
	public int getXpGained() {
		return (int) (skill.getExperience() - startExp);
	}
	
	/**
	 * Gets the exp gained per product.
	 * @return the exp per product.
	 */
	public int getExpPerProduct() {
		return expPerProduct;
	}

	/**
	 * @return the skill
	 */
	public Skill getSkill() {
		return skill;
	}

	/**
	 * @return the startExp
	 */
	public double getStartExp() {
		return startExp;
	}

}
