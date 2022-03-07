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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class HUDCommands {
	@CommandParser.Command(help="Shows/hides HUD.",alias="th")
	public static MutableComponent toggleHUD(String[] args){
		HUD5zig.settings.put(HUD5zig.Options.HUD_ENABLED,(Math.abs(HUD5zig.settings.get(HUD5zig.Options.HUD_ENABLED)) == 1)?0:1);
		return new TextComponent("HUD toggled to "+((HUD5zig.settings.get(HUD5zig.Options.HUD_ENABLED) == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the HUD. Usage: /5h setHUDX <X value>",alias="shx")
	public static MutableComponent setHUDX(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.HUD_X,Integer.parseInt(args[0]));
				return new TextComponent("Set HUD X to "+HUD5zig.settings.get(HUD5zig.Options.HUD_X));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <X>.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the HUD. Usage: /5h setHUDY <Y value>",alias="shy")
	public static MutableComponent setHUDY(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.HUD_Y,Integer.parseInt(args[0]));
				return new TextComponent("Set HUD Y to "+HUD5zig.settings.get(HUD5zig.Options.HUD_Y));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <Y>.");
	}
	@CommandParser.Command(help="Sets the alignment of the HUD. Usage: /5h setHUDAlignment <0|1>",alias="sha")
	public static MutableComponent setHUDAlignment(String[] args){
		if(args != null && args.length==1) {
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new TextComponent("Invalid argument.");
				}
				HUD5zig.settings.put(HUD5zig.Options.HUD_ALIGN,n);
				return new TextComponent("Successfully updated HUD alignment.");
			}catch(NumberFormatException e){
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <alignment>");
	}
	@CommandParser.Command(help="Shows/hides armor pane.",alias="tap")
	public static MutableComponent toggleArmorPane(String[] args){
		HUD5zig.settings.put(HUD5zig.Options.ARMOR_ENABLED,(Math.abs(HUD5zig.settings.get(HUD5zig.Options.ARMOR_ENABLED)) == 1)?0:1);
		return new TextComponent("Armor pane toggled to "+((HUD5zig.settings.get(HUD5zig.Options.ARMOR_ENABLED) == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the armor pane. Usage: /5h setArmorX <X value>",alias="sax")
	public static MutableComponent setArmorX(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.ARMOR_X,Integer.parseInt(args[0]));
				return new TextComponent("Set armor X to "+HUD5zig.settings.get(HUD5zig.Options.ARMOR_X));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <X>");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the armor pane. Usage: /5h setArmorY <Y value>",alias="say")
	public static MutableComponent setArmorY(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put(HUD5zig.Options.ARMOR_Y,Integer.parseInt(args[0]));
				return new TextComponent("Set armor Y to "+HUD5zig.settings.get(HUD5zig.Options.ARMOR_Y));
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <Y>");
	}
	@CommandParser.Command(help="Sets the alignment of the armor pane. Usage: /5h setArmorAlignment <0|1>",alias="saa")
	public static MutableComponent setArmorAlignment(String[] args){
		if(args != null && args.length==1){
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new TextComponent("Invalid argument.");
				}
				HUD5zig.settings.put(HUD5zig.Options.ARMOR_ALIGN,n);
				return new TextComponent("Successfully updated armor alignment.");
			}catch(NumberFormatException e) {
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <alignment>");
	}
	@CommandParser.Command(help="Shows/hides death timer.",alias="tt")
	public static MutableComponent toggleTimer(String[] args){
		HUD5zig.settings.put(HUD5zig.Options.DT_ENABLED,(Math.abs(HUD5zig.settings.get(HUD5zig.Options.DT_ENABLED)) == 1)?0:1);
		return new TextComponent("Timer toggled to "+((HUD5zig.settings.get(HUD5zig.Options.DT_ENABLED) == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the alignment of the death timer. Usage: /5h setDeathTimerAlignment <0|1>",alias="sdta")
	public static MutableComponent setDeathTimerAlignment(String[] args){
		if(args != null && args.length==1) {
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new TextComponent("Invalid argument.");
				}
				HUD5zig.settings.put(HUD5zig.Options.DT_ALIGN,n);
				return new TextComponent("Successfully updated death timer alignment.");
			}catch(NumberFormatException e){
				return new TextComponent("Invalid argument.");
			}
		}
		return new TextComponent("Invalid/missing argument <alignment>");
	}
}
