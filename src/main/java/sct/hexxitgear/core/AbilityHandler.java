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

package sct.hexxitgear.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import sct.hexxitgear.core.ability.Ability;
import sct.hexxitgear.net.AbilityRenderMessage;
import sct.hexxitgear.net.ActionTextMessage;
import sct.hexxitgear.net.HexNetwork;
import shadows.placebo.util.NetworkUtils;

public class AbilityHandler {

	private int activeTime;
	private int cooldownTime;
	private Ability ability;
	private boolean ended = false;
	private boolean started = false;

	public static final Map<UUID, AbilityHandler> ACTIVE_HANDLERS = new HashMap<>();

	private AbilityHandler(PlayerEntity player) {
		if (player.world.isRemote) throw new IllegalArgumentException("Ability handler has been constructed on a client world, please report this!");
		this.ability = ArmorSet.getCurrentArmorSet(player).getAbility();
		this.activeTime = ability.getDuration();
		this.cooldownTime = ability.getCooldown();
	}

	public static AbilityHandler getActiveAbility(PlayerEntity player) {
		if (player.world.isRemote) return null;
		return ACTIVE_HANDLERS.get(player.getUniqueID());
	}

	public static void activateAbility(PlayerEntity player) {
		if (ACTIVE_HANDLERS.get(player.getUniqueID()) != null) {
			Ability ability = ACTIVE_HANDLERS.get(player.getUniqueID()).ability;
			NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(0, ability.getId()), player);
			return;
		}
		AbilityHandler handler = new AbilityHandler(player);
		if (!player.isCreative() && player.experienceTotal < handler.ability.getXpCost()) {
			NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(4, handler.ability.getId()), player);
			return;
		}
		int food = player.getFoodStats().getFoodLevel();
		if (!player.isCreative() && food < handler.ability.getHungerCost()) {
			NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(5, handler.ability.getId()), player);
			return;
		}
		if (!player.isCreative()) {
			player.experienceTotal -= handler.ability.getXpCost();
			player.getFoodStats().setFoodLevel(food - handler.ability.getHungerCost());
		}
		ACTIVE_HANDLERS.put(player.getUniqueID(), handler);
	}

	public void onTick(PlayerEntity player) {
		if (activeTime > 0) {
			if (ability != null) {
				if (ability.getDuration() == activeTime || ability.isInstant()) {
					NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(1, ability.getId()), player);
				}
				if (!started) {
					ability.start(player);
					NetworkUtils.sendToTracking(HexNetwork.INSTANCE, new AbilityRenderMessage(0, ability.getId(), player, 0), (ServerWorld) player.world, player.getPosition());
					started = true;
				} else {
					ability.tick(player, activeTime);
					NetworkUtils.sendToTracking(HexNetwork.INSTANCE, new AbilityRenderMessage(1, ability.getId(), player, activeTime), (ServerWorld) player.world, player.getPosition());
				}
				if (ability.isInstant()) activeTime = 0;
			}
			activeTime--;
		} else if (cooldownTime > 0) {
			if (ability != null && !ended) {
				setEnded(player);
			}
			cooldownTime--;
		} else {
			NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(3, ability.getId()), player);
			ability = null;
			ACTIVE_HANDLERS.remove(player.getUniqueID());
		}
	}

	public void setEnded(PlayerEntity player) {
		ability.end(player);
		ended = true;
		activeTime = 0;
		if (ability.getDuration() >= 100) NetworkUtils.sendTo(HexNetwork.INSTANCE, new ActionTextMessage(2, ability.getId()), player);
	}
}
