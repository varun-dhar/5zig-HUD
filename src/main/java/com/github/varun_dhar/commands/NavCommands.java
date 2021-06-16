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

package com.github.varun_dhar.commands;

import com.github.varun_dhar.CommandParser;
import com.github.varun_dhar.HUD5zig;
import com.github.varun_dhar.components.Navigation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NavCommands {

	@CommandParser.Command(help="Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> ",alias="n")
	public static IFormattableTextComponent nav(String[] args){
		if(args != null && args.length == 1)
		{
			Navigation.location = CoordinateCommands.savedCoords.get(args[0]);
			if(Navigation.location == null){
				return new StringTextComponent("Invalid argument.");
			}
		}
		else if(args != null && args.length == 3)
		{
			Navigation.location = "X: "+args[0]+" Y: "+args[1]+" Z: "+args[2];
		}
		else{
			return new StringTextComponent("Invalid argument.");
		}
		return new StringTextComponent("Starting navigation.");
	}
	@CommandParser.Command(help="Sets the x-coordinate of the navigation compass. Usage: /5h setNavX <X>",alias="snx")
	public static IFormattableTextComponent setNavX(String[] args){
		if(args != null && args.length == 1) {
			try {
				HUD5zig.settings.put("NavX",Integer.parseInt(args[0]));
				return new StringTextComponent("Set navigation X to "+HUD5zig.settings.get("NavX"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument <X>.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the navigation compass. Usage: /5h setNavY <Y>",alias="sny")
	public static IFormattableTextComponent setNavY(String[] args){
		if(args != null && args.length == 1) {
			try {
				HUD5zig.settings.put("NavY", Integer.parseInt(args[0]));
				return new StringTextComponent("Set navigation Y to "+HUD5zig.settings.get("NavY"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument <Y>.");
	}
	@CommandParser.Command(help="Stops navigation and hides compass.",alias="fn")
	public static IFormattableTextComponent finishNav(String[] args){
		Navigation.location = null;
		return new StringTextComponent("Navigation ended.");
	}
	@CommandParser.Command(help="Toggles whether or not the navigation system navigates to the location of death.",alias="tntd")
	public static IFormattableTextComponent toggleNavToDeath(String[] args){
		HUD5zig.settings.put("NavToDeathEnabled", (Math.abs(HUD5zig.settings.get("NavToDeathEnabled"))==1)?0:1);
		return new StringTextComponent("Set navigation to death coordinates to "+((Math.abs(HUD5zig.settings.get("NavToDeathEnabled")) == 1)?"on.":"off."));
	}
}
