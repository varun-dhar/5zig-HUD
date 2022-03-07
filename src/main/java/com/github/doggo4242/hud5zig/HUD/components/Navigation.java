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

package com.github.doggo4242.hud5zig.HUD.components;

import com.github.doggo4242.hud5zig.HUD5zig;
import com.github.doggo4242.hud5zig.HUD.HUDComponent;
import com.github.doggo4242.hud5zig.HUD.HUDComponentImage;
import com.github.doggo4242.hud5zig.HUD.HUDComponentText;
import com.github.doggo4242.hud5zig.commands.CoordinateCommands;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class Navigation extends HUDComponent {
	private static int[] location = null;
	private static int dimensionID;
	private static final int nCompassStates = 28;
	private static ResourceLocation[] compassStates;
	public Navigation() {
		super();
		componentText = new HUDComponentText[1];
		componentText[0] = new HUDComponentText();
		componentImages = new HUDComponentImage[1];
		componentImages[0] = new HUDComponentImage();
		componentImages[0].width = 30;
		componentImages[0].height = 30;
		compassStates = new ResourceLocation[nCompassStates];
		for(int i = 0;i<nCompassStates;i++){
			compassStates[i] = new ResourceLocation(String.format("%s:nav%d.png",HUD5zig.MODID,i));
		}
	}

	public static void navigate(int[] loc, int dimension){
		location = loc;
		dimensionID = dimension;
	}

	public static void endNavigation(){
		location = null;
	}

	@Override
	public void updateComponent() {
		if ((disabled = player == null || location == null)) { //enable navigation system if string is not null
			return;
		}
		final int defNavX = scrWidth / 2 + 110;
		final int defNavY = scrHeight - 40;

		//extract x, y, and z values
		int x = location[0];
		int y = location[1];
		int z = location[2];
		//get the angle between the two points
		double a = player.getX() - x;
		double b = player.getZ() - z;
		double angle = Math.toDegrees(Math.atan2(b,a));
		//calculate difference, change 0 ref point to y-axis
		angle = ((player.getYRot()%360)-(angle+90)+360)%360;
		componentText[0].text = (((int) angle > 180) ? (int) angle - 360 : (int) angle) + "\u00b0";
		//invert degrees
		angle = (angle + 180 + (360. / nCompassStates / 2)) % 360;
		//set compass picture to use depending on which angle is closest (28 states)
		//load the compass image
		componentImages[0].image = compassStates[(int)Math.round(angle / (360. / (nCompassStates-1)))];
		//set x and y of compass and text
		int compX = (HUD5zig.settings.get(HUD5zig.Options.NAV_X) == -1) ? defNavX : HUD5zig.settings.get(HUD5zig.Options.NAV_X);
		int compY = (HUD5zig.settings.get(HUD5zig.Options.NAV_Y) == -1) ? defNavY : HUD5zig.settings.get(HUD5zig.Options.NAV_Y);
		componentImages[0].x = compX;
		componentImages[0].y = compY;
		componentText[0].x = compX + 5;
		componentText[0].y = compY + 30;

		int currentDimension = CoordinateCommands.getDimension();
		//check if close enough to stop navigation
		if (Math.abs(x - player.getX()) < 10 && Math.abs(z - player.getZ()) < 10 && Math.abs(y-player.getY()) < 10
				&& (dimensionID == -1 || currentDimension == dimensionID)) {
			player.sendMessage(new TextComponent("Arrived."), Util.NIL_UUID);
			endNavigation();
		}
	}

}
