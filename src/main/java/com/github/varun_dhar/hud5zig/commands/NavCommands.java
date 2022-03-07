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

package com.github.varun_dhar.hud5zig.commands;

import com.github.varun_dhar.hud5zig.HUD5zig;
import com.github.varun_dhar.hud5zig.HUD.components.Navigation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class NavCommands {

	@CommandParser.Command(help="Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> or /5h nav <location>",alias="n")
	public static MutableComponent nav(String[] args){
		if(args != null && args.length == 1) {
			int[] coords = CoordinateCommands.getCoordsI(args[0]);
			if(coords == null){
				return new TextComponent("No such location exists.");
			}
			Navigation.navigate(coords,CoordinateCommands.getDimension());
		} else if(args != null && args.length == 3) {
			int[] coords = new int[3];
			try {
				for (int i = 0; i < coords.length; i++) {
					coords[i] = Integer.parseInt(args[i]);
				}
			}catch(NumberFormatException e){
				return new TextComponent("Invalid argument.");
			}
			Navigation.navigate(coords,CoordinateCommands.getDimension());
		} else{
			return new TextComponent("Invalid/missing argument(s).");
		}
		return new TextComponent("Starting navigation.");
	}
	@CommandParser.Command(help="Sets the x-coordinate of the navigation compass. Usage: /5h setNavX <X>",alias="snx")
	public static MutableComponent setNavX(String[] args){
		if(args != null && args.length == 1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.NAV_X,Integer.parseInt(args[0]));
				return new TextComponent("Set navigation X to "+HUD5zig.settings.get(HUD5zig.Options.NAV_Y));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <X>.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the navigation compass. Usage: /5h setNavY <Y>",alias="sny")
	public static MutableComponent setNavY(String[] args){
		if(args != null && args.length == 1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.NAV_Y, Integer.parseInt(args[0]));
				return new TextComponent("Set navigation Y to "+HUD5zig.settings.get(HUD5zig.Options.NAV_Y));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}	
		return new TextComponent("Invalid/missing argument <Y>.");
	}
	@CommandParser.Command(help="Stops navigation and hides compass.",alias="fn")
	public static MutableComponent finishNav(String[] args){
		Navigation.endNavigation();
		return new TextComponent("Navigation ended.");
	}
	@CommandParser.Command(help="Toggles whether or not the navigation system navigates to the location of death upon dying.",alias="tntd")
	public static MutableComponent toggleNavToDeath(String[] args){
		HUD5zig.settings.put(HUD5zig.Options.NAV_TO_DEATH_ENABLED, (Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED))==1)?0:1);
		return new TextComponent("Set navigation to death coordinates to "+((Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED)) == 1)?"on.":"off."));
	}
}
