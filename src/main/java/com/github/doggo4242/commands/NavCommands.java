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

package com.github.doggo4242.commands;

import com.github.doggo4242.CommandParser;
import com.github.doggo4242.HUD5zig;
import com.github.doggo4242.components.Navigation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NavCommands {

	@CommandParser.Command(help="Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> or /5h nav <location>",alias="n")
	public static IFormattableTextComponent nav(String[] args){
		if(args != null && args.length == 1) {
			Coordinate coordinate = CoordinateCommands.getCoordsI(args[0]);
			if(coordinate == null){
				return new StringTextComponent("No such location exists.");
			}
			Navigation.location = coordinate.coords;
			Navigation.dimensionID = coordinate.dimensionID;
		} else if(args != null && args.length == 3) {
			int[] coords = new int[3];
			try {
				for (int i = 0; i < coords.length; i++) {
					coords[i] = Integer.parseInt(args[i+1]);
				}
			}catch(NumberFormatException e){
				return new StringTextComponent("Invalid argument.");
			}
			Navigation.location = coords;
		} else{
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
			}catch(NumberFormatException e) {
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
			}catch(NumberFormatException e) {
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
	@CommandParser.Command(help="Toggles whether or not the navigation system navigates to the location of death upon dying.",alias="tntd")
	public static IFormattableTextComponent toggleNavToDeath(String[] args){
		HUD5zig.settings.put("NavToDeathEnabled", (Math.abs(HUD5zig.settings.get("NavToDeathEnabled"))==1)?0:1);
		return new StringTextComponent("Set navigation to death coordinates to "+((Math.abs(HUD5zig.settings.get("NavToDeathEnabled")) == 1)?"on.":"off."));
	}
}
