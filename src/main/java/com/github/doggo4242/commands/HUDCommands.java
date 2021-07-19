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
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class HUDCommands {
	@CommandParser.Command(help="Shows/hides HUD.",alias="th")
	public static IFormattableTextComponent toggleHUD(String[] args){
		HUD5zig.settings.put("HUD-Enabled",(Math.abs(HUD5zig.settings.get("HUD-Enabled")) == 1)?0:1);
		return new StringTextComponent("HUD toggled to "+((HUD5zig.settings.get("HUD-Enabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the HUD. Usage: /5h setHUDX <X value>",alias="shx")
	public static IFormattableTextComponent setHUDX(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put("HUD-X",Integer.parseInt(args[0]));
				return new StringTextComponent("Set HUD X to "+HUD5zig.settings.get("HUD-X"));
			}catch(NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'X'.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the HUD. Usage: /5h setHUDY <Y value>",alias="shy")
	public static IFormattableTextComponent setHUDY(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put("HUD-Y",Integer.parseInt(args[0]));
				return new StringTextComponent("Set HUD Y to "+HUD5zig.settings.get("HUD-Y"));
			}catch(NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'Y'.");
	}
	@CommandParser.Command(help="Sets the alignment of the HUD. Usage: /5h setHUDAlignment <0|1>",alias="sha")
	public static IFormattableTextComponent setHUDAlignment(String[] args){
		if(args != null && args.length==1) {
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new StringTextComponent("Invalid argument.");
				}
				HUD5zig.settings.put("HUD-Alignment",n);
				return new StringTextComponent("Successfully updated HUD alignment.");
			}catch(NumberFormatException e){
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'alignment'");
	}
	@CommandParser.Command(help="Shows/hides armor pane.",alias="tap")
	public static IFormattableTextComponent toggleArmorPane(String[] args){
		HUD5zig.settings.put("ArmorEnabled",(Math.abs(HUD5zig.settings.get("ArmorEnabled")) == 1)?0:1);
		return new StringTextComponent("Armor pane toggled to "+((HUD5zig.settings.get("ArmorEnabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the armor pane. Usage: /5h setArmorX <X value>",alias="sax")
	public static IFormattableTextComponent setArmorX(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put("ArmorX",Integer.parseInt(args[0]));
				return new StringTextComponent("Set armor X to "+HUD5zig.settings.get("ArmorX"));
			}catch(NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'X'");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the armor pane. Usage: /5h setArmorY <Y value>",alias="say")
	public static IFormattableTextComponent setArmorY(String[] args){
		if(args != null && args.length==1) {
			try {
				HUD5zig.settings.put("ArmorY",Integer.parseInt(args[0]));
				return new StringTextComponent("Set armor Y to "+HUD5zig.settings.get("ArmorY"));
			}catch(NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'Y'");
	}
	@CommandParser.Command(help="Sets the alignment of the armor pane. Usage: /5h setArmorAlignment <0|1>",alias="saa")
	public static IFormattableTextComponent setArmorAlignment(String[] args){
		if(args != null && args.length==1){
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new StringTextComponent("Invalid argument.");
				}
				HUD5zig.settings.put("ArmorAlignment",n);
				return new StringTextComponent("Successfully updated armor alignment.");
			}catch(NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing/invalid argument 'alignment'");
	}
	@CommandParser.Command(help="Shows/hides death timer.",alias="tt")
	public static IFormattableTextComponent toggleTimer(String[] args){
		HUD5zig.settings.put("DeathTimerEnabled",(Math.abs(HUD5zig.settings.get("DeathTimerEnabled")) == 1)?0:1);
		return new StringTextComponent("Timer toggled to "+((HUD5zig.settings.get("DeathTimerEnabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the alignment of the death timer. Usage: /5h setDeathTimerAlignment <0|1>",alias="sdta")
	public static IFormattableTextComponent setDeathTimerAlignment(String[] args){
		if(args != null && args.length==1) {
			try {
				int n = Integer.parseInt(args[0]);
				if(n != 0 && n != 1){
					return new StringTextComponent("Invalid argument.");
				}
				HUD5zig.settings.put("DeathTimerAlignment",n);
				return new StringTextComponent("Successfully updated death timer alignment.");
			}catch(NumberFormatException e){
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'alignment'");
	}
}
