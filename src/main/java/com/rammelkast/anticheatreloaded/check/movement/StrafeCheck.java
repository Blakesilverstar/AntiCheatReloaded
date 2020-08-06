/*
 * AntiCheatReloaded for Bukkit and Spigot.
 * Copyright (c) 2012-2015 AntiCheat Team
 * Copyright (c) 2016-2020 Rammelkast
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.rammelkast.anticheatreloaded.check.movement;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.rammelkast.anticheatreloaded.AntiCheatReloaded;
import com.rammelkast.anticheatreloaded.check.CheckResult;
import com.rammelkast.anticheatreloaded.check.CheckType;
import com.rammelkast.anticheatreloaded.check.CheckResult.Result;
import com.rammelkast.anticheatreloaded.config.providers.Checks;
import com.rammelkast.anticheatreloaded.util.MovementManager;
import com.rammelkast.anticheatreloaded.util.Utilities;
import com.rammelkast.anticheatreloaded.util.VersionUtil;

/**
 * 
 * @author Rammelkast
 *
 */
public class StrafeCheck {

	private static final CheckResult PASS = new CheckResult(CheckResult.Result.PASSED);

	public static CheckResult runCheck(Player player, double x, double z, Location from, Location to) {
		if (!Utilities.cantStandAtExp(from) || !Utilities.cantStandAtExp(to) || Utilities.isNearWater(player)
				|| Utilities.isNearClimbable(player) || VersionUtil.isFlying(player) || player.isDead())
			return PASS;

		MovementManager movementManager = AntiCheatReloaded.getManager().getUserManager().getUser(player.getUniqueId())
				.getMovementManager();
		Checks checksConfig = AntiCheatReloaded.getManager().getConfiguration().getChecks();

		if (System.currentTimeMillis() - movementManager.lastTeleport <= checksConfig.getInteger(CheckType.STRAFE,
				"accountForTeleports") || movementManager.elytraEffectTicks >= 20)
			return PASS;

		Vector oldAcceleration = new Vector(movementManager.lastDistanceX, 0, movementManager.lastDistanceZ);
		Vector newAcceleration = new Vector(x, 0, z);

		float angle = newAcceleration.angle(oldAcceleration);
		double distance = newAcceleration.lengthSquared();
		if (angle > checksConfig
				.getDouble(CheckType.STRAFE, "maxAngleChange") && distance > checksConfig
				.getDouble(CheckType.STRAFE, "minActivationDistance") && Utilities.cantStandFar(to.getBlock()))
			return new CheckResult(Result.FAILED, "switched angle in air (angle=" + angle + ", dist=" + distance + ")");
		return PASS;
	}

}