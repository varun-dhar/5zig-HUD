/*
   Copyright 2023 Varun Dhar

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

package com.github.varun_dhar.hud5zig.HUD.components;

import com.github.varun_dhar.hud5zig.HUD.HUDComponentTextGroup;
import com.github.varun_dhar.hud5zig.HUD5zig;
import com.github.varun_dhar.hud5zig.HUD.HUDComponent;
import net.minecraft.client.Minecraft;

public class HUD extends HUDComponent {
	private static final int defYPos = 3;
	private static final String[] face = {"S " + gray + "+Z", "SW " + gray + "-X +Z", "W " + gray + "-X", "NW " + gray + "-X -Z",
			"N " + gray + "-Z", "NE " + gray + "+X -Z", "E " + gray + "+X", "SE " + gray + "+X +Z"};
	private final HUDComponentTextGroup textGroup;

	public HUD() {
		super();
		componentImages = null;
		componentTextGroups = new HUDComponentTextGroup[1];
		componentTextGroups[0] = new HUDComponentTextGroup(5, defYSpacing);
		textGroup = componentTextGroups[0];
	}

	@Override
	public void updateComponent() {
		if ((disabled = player == null || Math.abs(HUD5zig.settings.get(HUD5zig.Options.HUD_ENABLED)) != 1)) {
			return;
		}
		//set alignment and position
		textGroup.alignment = Math.abs(HUD5zig.settings.get(HUD5zig.Options.HUD_ALIGN)) == 1;
		int x = HUD5zig.settings.get(HUD5zig.Options.HUD_X);
		int y = HUD5zig.settings.get(HUD5zig.Options.HUD_Y);
		int dir = (int) player.getYRot();//getting direction
		dir = (dir % 360 + 360 + 22) % 360;//prevent negatives, get 0-359 angle, add 45/2
		dir /= 45;//determine direction
		x = (x == -1 || ((x + defYSpacing * textGroup.text.length) > scrWidth && textGroup.alignment) ||
				(x > scrWidth && !textGroup.alignment)) ? defXPos : x;
		y = (y == -1 || ((y + defYSpacing * textGroup.text.length) > scrHeight && textGroup.alignment) ||
				(y > scrHeight && !textGroup.alignment)) ? defYPos : y;
		textGroup.x = x;
		textGroup.y = y;
		textGroup.text[0] = String.format("%sX%s> %d", red, white, (int) player.getX());
		textGroup.text[1] = String.format("%sY%s> %d", red, white, (int) player.getY());
		textGroup.text[2] = String.format("%sZ%s> %d", red, white, (int) player.getZ());
		textGroup.text[3] = String.format("%sFPS%s> %d", red, white, Minecraft.fps);
		textGroup.text[4] = String.format("%sF%s> %s", red, white, face[dir]);
	}
}
