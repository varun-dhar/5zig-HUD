/*
   Copyright 2021 doggo4242 Development

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.github.doggo4242.HUD.components;

import com.github.doggo4242.HUD.HUDComponentTextGroup;
import com.github.doggo4242.HUD5zig;
import com.github.doggo4242.HUD.HUDComponent;
import com.github.doggo4242.commands.CoordinateCommands;

import java.util.concurrent.TimeUnit;

public class DeathTimer extends HUDComponent {
	private static boolean dead = false;
	private static final int[] deathCoords = new int[3];
	private static int dimension;
	private static long deathTime = 0;
	private final static TimeUnit tUnitMs = TimeUnit.MILLISECONDS;
	private final int defVerYPos = 126;
	private final int defHorYPos = 43;

	private final HUDComponentTextGroup textGroup;

	public DeathTimer(){
		super();
		componentImages = null;
		componentTextGroups = new HUDComponentTextGroup[1];
		componentTextGroups[0] = new HUDComponentTextGroup(2);
		textGroup = componentTextGroups[0];
	}

	public static void setDeathInfo(int x, int y, int z,int dim){
		deathCoords[0] = x;
		deathCoords[1] = y;
		deathCoords[2] = z;
		dimension = dim;
		dead = true;
	}

	public static void setRespawnInfo(){
		dead = false;
		deathTime = System.currentTimeMillis();
	}

	public static int[] getDeathCoords(){
		return deathCoords;
	}

	@Override
	public void updateComponent() {
		boolean nearby = false;
		//enable if < 5m since death, alive, and not close to death location
		if ((disabled = !(!dead && (System.currentTimeMillis() - deathTime) < tUnitMs.convert(5, TimeUnit.MINUTES)
				&& Math.abs(HUD5zig.settings.get("DeathTimerEnabled")) == 1
				&& !(nearby = (Math.abs(player.lastTickPosX-deathCoords[0]) < 10 && Math.abs(player.lastTickPosZ-deathCoords[2]) < 10
				&& CoordinateCommands.getDimension() == dimension))))) {
			if(nearby){
				deathTime = 0;
			}
			return;
		}
		textGroup.alignment = Math.abs(HUD5zig.settings.get("DeathTimerAlignment")) == 1;
		//get seconds without minutes
		int sec = (int) ((tUnitMs.convert(5, TimeUnit.MINUTES) -
				(System.currentTimeMillis() - deathTime)) % tUnitMs.convert(1, TimeUnit.MINUTES) /
				tUnitMs.convert(1, TimeUnit.SECONDS));
		int min = (int) ((tUnitMs.convert(5, TimeUnit.MINUTES) - (System.currentTimeMillis() - deathTime)) /
				tUnitMs.convert(1, TimeUnit.MINUTES));
		//set alignment and position
		int x = HUD5zig.settings.get("DeathTimerX");
		int y = HUD5zig.settings.get("DeathTimerY");
		x = (x == -1 || (x + 10 * textGroup.text.length) > scrWidth) ? defXPos : x;
		if (textGroup.alignment && (y == -1 || (y + 10 * textGroup.text.length) > scrHeight)) {
			y = defVerYPos;
		} else if (!textGroup.alignment && (y == -1 || y > scrHeight)) {
			y = defHorYPos;
		}
		textGroup.x = x;
		textGroup.y = y;
		textGroup.text[0] = String.format("%sItems despawn in> %s %d:%02d", red, white, min, sec);
		textGroup.text[1] = String.format("%sDied at> X: %s%d%s Y: %s%d%s Z: %s%d",
				red, white, deathCoords[0], red, white, deathCoords[1], red, white, deathCoords[2]);
	}
}
