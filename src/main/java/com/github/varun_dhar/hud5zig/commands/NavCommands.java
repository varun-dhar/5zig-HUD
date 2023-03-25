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

package com.github.varun_dhar.hud5zig.commands;

import com.github.varun_dhar.hud5zig.HUD5zig;
import com.github.varun_dhar.hud5zig.HUD.components.Navigation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class NavCommands {

	@CommandParser.Command(help = "Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> or /5h nav <location>", alias = "n")
	public static MutableComponent nav(int x, int y, int z) {
		Navigation.navigate(new int[]{x, y, z}, CoordinateCommands.getDimension());
		return Component.literal("Starting navigation.");
	}

	@CommandParser.Command(help = "Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> or /5h nav <location>", alias = "n")
	public static MutableComponent nav(String name) {
		int[] coords = CoordinateCommands.getCoordsI(name);
		if (coords == null) {
			return Component.literal("No such location exists.");
		}
		Navigation.navigate(coords, CoordinateCommands.getDimension());
		return Component.literal("Starting navigation.");
	}

	@CommandParser.Command(help = "Sets the x-coordinate of the navigation compass. Usage: /5h setNavX <X>", alias = "snx")
	public static MutableComponent setNavX(int x) {
		HUD5zig.settings.put(HUD5zig.Options.NAV_X, x);
		return Component.literal("Set navigation X to " + x);
	}

	@CommandParser.Command(help = "Sets the y-coordinate of the navigation compass. Usage: /5h setNavY <Y>", alias = "sny")
	public static MutableComponent setNavY(int y) {
		HUD5zig.settings.put(HUD5zig.Options.NAV_Y, y);
		return Component.literal("Set navigation Y to " + y);
	}

	@CommandParser.Command(help = "Stops navigation and hides compass.", alias = "fn")
	public static MutableComponent finishNav() {
		Navigation.endNavigation();
		return Component.literal("Navigation ended.");
	}

	@CommandParser.Command(help = "Toggles whether or not the navigation system navigates to the location of death upon dying.", alias = "tntd")
	public static MutableComponent toggleNavToDeath() {
		HUD5zig.settings.put(HUD5zig.Options.NAV_TO_DEATH_ENABLED, (Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED)) == 1) ? 0 : 1);
		return Component.literal("Set navigation to death coordinates to " + ((Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED)) == 1) ? "on." : "off."));
	}
}
