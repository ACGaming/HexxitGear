/*
 * HexxitGear
 * Copyright (C) 2013  Ryan Cohen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package sct.hexxitgear.core.ability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Ability {

	public static final List<Ability> ABILITIES = new ArrayList<>();
	private static int curId = 0;

	private final String name;
	private final int duration;
	private final int cooldown;
	private final boolean instant;
	private final int id;
	private final int xpCost;
	private final int hungerCost;

	/**
	 * Generates an ability
	 * @param name The ability name
	 * @param duration The active duration (in ticks)
	 * @param cooldown The cooldown (in ticks)
	 * @param xpCost The value of XP to remove to activate.  Not in levels.
	 * @param hungerCost The amount of hunger to remove to activate. 
	 */
	public Ability(String name, int duration, int cooldown, int xpCost, int hungerCost) {
		this.name = name;
		this.duration = duration;
		this.cooldown = cooldown;
		this.instant = false;
		id = curId++;
		this.xpCost = xpCost;
		this.hungerCost = hungerCost;
		ABILITIES.add(this);
	}

	/**
	 * Generates an instant ability
	 * @param name The ability name
	 * @param cooldown The cooldown (in ticks)
	 */
	public Ability(String name, int cooldown, int xpCost, int hungerCost) {
		this.name = name;
		this.duration = 1;
		this.cooldown = cooldown;
		this.instant = true;
		id = curId++;
		this.xpCost = xpCost;
		this.hungerCost = hungerCost;
		ABILITIES.add(this);
	}

	public String getName() {
		return name;
	}

	public int getDuration() {
		return duration;
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean isInstant() {
		return instant;
	}

	public int getId() {
		return id;
	}
	
	public int getXpCost() {
		return xpCost;
	}
	
	public int getHungerCost() {
		return hungerCost;
	}
	
	public boolean canCast(EntityPlayer player) {
		return player.experience >= getXpCost() && player.getFoodStats().getFoodLevel() >= hungerCost;
	}

	/**
	 * Called on the first tick of this ability's activation.
	 * @param player The ability caster.
	 */
	public abstract void start(EntityPlayer player);

	/**
	 * Called every subsequent tick after the first tick.
	 * @param player The ability caster.
	 */
	public abstract void tick(EntityPlayer player, int duration);

	/**
	 * Called after the duration of this ability has ended.
	 * @param player The ability caster.
	 */
	public abstract void end(EntityPlayer player);

	/**
	 * Render effects for this ability, called when start is called.
	 * Can also be used to play cast sounds.
	 * @param player The ability caster.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void renderFirst(EntityPlayer player);

	/**
	 * Render effects for this ability, called when tick is called.
	 * @param player The ability caster.
	 * @param duration The remaining duration of the ability.
	 */
	@SideOnly(Side.CLIENT)
	public abstract void renderAt(EntityPlayer player, int duration);
}
