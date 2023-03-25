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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;

public class HUDCommands {
	@CommandParser.Command(help = "Shows/hides HUD.", alias = "th")
	public static MutableComponent toggleHUD() {
		HUD5zig.settings.put(HUD5zig.Options.HUD_ENABLED, (Math.abs(HUD5zig.settings.get(HUD5zig.Options.HUD_ENABLED)) == 1) ? 0 : 1);
		return Component.literal("HUD toggled to " + ((HUD5zig.settings.get(HUD5zig.Options.HUD_ENABLED) == 0) ? "off." : "on."));
	}

	@CommandParser.Command(help = "Sets the x-coordinate of the HUD. Usage: /5h setHUDX <X value>", alias = "shx")
	public static MutableComponent setHUDX(int x) {
		HUD5zig.settings.put(HUD5zig.Options.HUD_X, x);
		return Component.literal("Set HUD X to " + x);
	}

	@CommandParser.Command(help = "Sets the y-coordinate of the HUD. Usage: /5h setHUDY <Y value>", alias = "shy")
	public static MutableComponent setHUDY(int y) {
		HUD5zig.settings.put(HUD5zig.Options.HUD_Y, y);
		return Component.literal("Set HUD Y to " + y);
	}

	@CommandParser.Command(help = "Sets the alignment of the HUD. Usage: /5h setHUDAlignment <0|1>", alias = "sha")
	public static MutableComponent setHUDAlignment(int n) {
		if (n != 0 && n != 1) {
			return Component.literal("Invalid argument.");
		}
		HUD5zig.settings.put(HUD5zig.Options.HUD_ALIGN, n);
		return Component.literal("Successfully updated HUD alignment.");
	}

	@CommandParser.Command(help = "Shows/hides armor pane.", alias = "tap")
	public static MutableComponent toggleArmorPane() {
		HUD5zig.settings.put(HUD5zig.Options.ARMOR_ENABLED, (Math.abs(HUD5zig.settings.get(HUD5zig.Options.ARMOR_ENABLED)) == 1) ? 0 : 1);
		return Component.literal("Armor pane toggled to " + ((HUD5zig.settings.get(HUD5zig.Options.ARMOR_ENABLED) == 0) ? "off." : "on."));
	}

	@CommandParser.Command(help = "Sets the x-coordinate of the armor pane. Usage: /5h setArmorX <X value>", alias = "sax")
	public static MutableComponent setArmorX(int x) {
		HUD5zig.settings.put(HUD5zig.Options.ARMOR_X, x);
		return Component.literal("Set armor X to " + x);
	}

	@CommandParser.Command(help = "Sets the y-coordinate of the armor pane. Usage: /5h setArmorY <Y value>", alias = "say")
	public static MutableComponent setArmorY(int y) {
		HUD5zig.settings.put(HUD5zig.Options.ARMOR_Y, y);
		return Component.literal("Set armor Y to " + y);
	}

	@CommandParser.Command(help = "Sets the alignment of the armor pane. Usage: /5h setArmorAlignment <0|1>", alias = "saa")
	public static MutableComponent setArmorAlignment(int n) {
		if (n != 0 && n != 1) {
			return Component.literal("Invalid argument.");
		}
		HUD5zig.settings.put(HUD5zig.Options.ARMOR_ALIGN, n);
		return Component.literal("Successfully updated armor alignment.");
	}

	@CommandParser.Command(help = "Shows/hides death timer.", alias = "tt")
	public static MutableComponent toggleTimer() {
		HUD5zig.settings.put(HUD5zig.Options.DT_ENABLED, (Math.abs(HUD5zig.settings.get(HUD5zig.Options.DT_ENABLED)) == 1) ? 0 : 1);
		return Component.literal("Timer toggled to " + ((HUD5zig.settings.get(HUD5zig.Options.DT_ENABLED) == 0) ? "off." : "on."));
	}

	@CommandParser.Command(help = "Sets the alignment of the death timer. Usage: /5h setDeathTimerAlignment <0|1>", alias = "sdta")
	public static MutableComponent setDeathTimerAlignment(int n) {
		if (n != 0 && n != 1) {
			return Component.literal("Invalid argument.");
		}
		HUD5zig.settings.put(HUD5zig.Options.DT_ALIGN, n);
		return Component.literal("Successfully updated death timer alignment.");
	}
}
