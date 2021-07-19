/*
   Copyright 2021 Varun Dhar

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

package com.github.varun_dhar.components;

import com.github.varun_dhar.HUD5zig;
import com.github.varun_dhar.HUDComponent;
import com.github.varun_dhar.HUDComponentText;
import net.minecraft.client.Minecraft;

public class HUD extends HUDComponent {
	private static final int defYPos = 3;
	private static final String[] face = {"S "+gray+"+Z","SW "+gray+"-X +Z","W "+gray+"-X","NW "+ gray+"-X -Z",
			"N "+gray+"-Z","NE "+gray+"+X -Z","E "+gray+"+X","SE "+gray+"+X +Z"};
	public HUD(){
		componentImages = null;
		componentText = new HUDComponentText[5];
		for(int i = 0;i<componentText.length;i++){
			componentText[i] = new HUDComponentText();
		}
	}
	@Override
	public void updateComponent() {
		if((disabled = player == null || Math.abs(HUD5zig.settings.get("HUD-Enabled")) != 1)){
			return;
		}
		//set alignment and position
		boolean alignment = Math.abs(HUD5zig.settings.get("HUD-Alignment")) == 1;
		int x = HUD5zig.settings.get("HUD-X");
		int y = HUD5zig.settings.get("HUD-Y");
		int dir = (int)player.rotationYaw;//getting direction
		dir=(dir%360+360+22)%360;//prevent negatives, get 0-359 angle, add magic number
		dir/=45;//determine direction
		x = (x == -1 || ((x + defYSpacing*componentText.length) > scrWidth && alignment) || (x > scrWidth && !alignment)) ? defXPos : x;
		y = (y == -1 || ((y + defYSpacing*componentText.length) > scrHeight && alignment) || (y > scrHeight && !alignment)) ? defYPos : y;
		componentText[0].text = String.format("%sX%s> %d",red,white,(int)player.getPosX());
		componentText[1].text = String.format("%sY%s> %d",red,white,(int)player.getPosY());
		componentText[2].text = String.format("%sZ%s> %d",red,white,(int)player.getPosZ());
		componentText[3].text = String.format("%sFPS%s> %d",red,white,Minecraft.debugFPS);
		componentText[4].text = String.format("%sF%s> %s",red,white,face[dir]);
		for (HUDComponentText text : componentText) {
			text.x = x;
			text.y = y;
			if(alignment) {
				y += defYSpacing;
			}
		}
	}
}
