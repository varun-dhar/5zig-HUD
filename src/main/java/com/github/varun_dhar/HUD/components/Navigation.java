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

package com.github.varun_dhar.HUD.components;

import com.github.varun_dhar.HUD5zig;
import com.github.varun_dhar.HUD.HUDComponent;
import com.github.varun_dhar.HUD.HUDComponentImage;
import com.github.varun_dhar.HUD.HUDComponentText;
import com.github.varun_dhar.commands.CoordinateCommands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class Navigation extends HUDComponent {
	private static int[] location = null;
	private static int dimensionID;
	public Navigation() {
		super();
		componentText = new HUDComponentText[1];
		componentText[0] = new HUDComponentText();
		componentImages = new HUDComponentImage[1];
		componentImages[0] = new HUDComponentImage();
		componentImages[0].width = 30;
		componentImages[0].height = 30;
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
		//extract x and z values
		final int defNavX = scrWidth / 2 + 110;
		final int defNavY = scrHeight - 40;

		int x = location[0];
		int z = location[2];
		//get the angle between the two points
		double a = Math.abs(player.getPosX() - x);
		double b = Math.abs(player.getPosZ() - z);
		double angle = Math.toDegrees(Math.atan(b / a));
		//find quadrant, use to determine exact angle
		//270+angle +x, +z
		//90-angle -x, +z
		//270-angle +x, -z
		//90+angle -x, -z
		int q = (player.getPosX() > x) ? -90 : 270;//if -x
		q *= (player.getPosZ() > z) ? -1 : 1;//if -z
		angle = (q < 0) ? -(angle + q) : angle + q;
		//get difference between calculated angle and player angle. takes care of negatives with (x%360+360)%360
		angle = ((player.rotationYaw - angle) % 360 + 360) % 360;
		componentText[0].text = (((int) angle > 180) ? (int) angle - 360 : (int) angle) + "\u00b0";
		//invert degrees
		angle = ((angle - 180) % 360 + 360 + (360 / 27.0 / 2)) % 360;
		//set compass picture to use depending on which angle is closest (27 states)
		angle = Math.round(angle / (360 / 27.0));

		//load the compass image
		componentImages[0].image = new ResourceLocation(String.format("%s:nav%d.png", HUD5zig.MODID, (int) angle));
		//set x and y of compass and text
		int compX = (HUD5zig.settings.get("NavX") == -1) ? defNavX : HUD5zig.settings.get("NavX");
		int compY = (HUD5zig.settings.get("NavY") == -1) ? defNavY : HUD5zig.settings.get("NavY");
		componentImages[0].x = compX;
		componentImages[0].y = compY;
		componentText[0].x = compX + 5;
		componentText[0].y = compY + 30;

		int currentDimension = CoordinateCommands.getDimension();
		//check if close enough to stop navigation
		if (Math.abs(x - player.getPosX()) < 10 && Math.abs(z - player.getPosZ()) < 10
				&& (dimensionID == -1 || currentDimension == dimensionID)) {
			player.sendMessage(new StringTextComponent("Arrived."), Util.DUMMY_UUID);
			endNavigation();
		}
	}

}