package com.github.varun_dhar.commands;

import com.github.varun_dhar.CommandParser;
import com.github.varun_dhar.HUD5zig;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class HUDCommands {
	@CommandParser.Command(help="Shows/hides HUD.",alias="th")
	public static IFormattableTextComponent toggleHUD(String[] args){
		HUD5zig.set.put("HUD-Enabled",(Math.abs(HUD5zig.set.get("HUD-Enabled")) == 1)?0:1);
		return new StringTextComponent("HUD toggled to "+((HUD5zig.set.get("HUD-Enabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the HUD. Usage: /5h setHUDX <X value>",alias="shx")
	public static IFormattableTextComponent setHUDX(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("HUD-X",Integer.parseInt(args[1]));
				return new StringTextComponent("Set HUD X to "+HUD5zig.set.get("HUD-X"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'X'.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the HUD. Usage: /5h setHUDY <Y value>",alias="shy")
	public static IFormattableTextComponent setHUDY(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("HUD-Y",Integer.parseInt(args[1]));
				return new StringTextComponent("Set HUD Y to "+HUD5zig.set.get("HUD-Y"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'Y'.");
	}
	@CommandParser.Command(help="Sets the alignment of the HUD. Usage: /5h setHUDAlignment <0|1>",alias="sha")
	public static IFormattableTextComponent setHUDAlignment(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("HUD-Alignment",Integer.parseInt(args[1]));
				return new StringTextComponent("Successfully updated HUD alignment.");
			}catch(NumberFormatException e){
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'alignment'");
	}
	@CommandParser.Command(help="Shows/hides armor pane.",alias="tap")
	public static IFormattableTextComponent toggleArmorPane(String[] args){
		HUD5zig.set.put("ArmorEnabled",(Math.abs(HUD5zig.set.get("ArmorEnabled")) == 1)?0:1);
		return new StringTextComponent("Armor pane toggled to "+((HUD5zig.set.get("ArmorEnabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the x-coordinate of the armor pane. Usage: /5h setArmorX <X value>",alias="sax")
	public static IFormattableTextComponent setArmorX(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("ArmorX",Integer.parseInt(args[1]));
				return new StringTextComponent("Set armor Y to "+HUD5zig.set.get("ArmorX"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'X'");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the armor pane. Usage: /5h setArmorY <Y value>",alias="say")
	public static IFormattableTextComponent setArmorY(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("ArmorY",Integer.parseInt(args[1]));
				return new StringTextComponent("Set armor Y to "+HUD5zig.set.get("ArmorY"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'Y'");
	}
	@CommandParser.Command(help="Sets the alignment of the armor pane. Usage: /5h setArmorAlignment <0|1>",alias="saa")
	public static IFormattableTextComponent setArmorAlignment(String[] args){
		if(args.length==2){
			try {
				HUD5zig.set.put("ArmorAlignment",Integer.parseInt(args[1]));
				return new StringTextComponent("Successfully updated armor alignment.");
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'alignment'");
	}
	@CommandParser.Command(help="Shows/hides death timer.",alias="tt")
	public static IFormattableTextComponent toggleTimer(String[] args){
		HUD5zig.set.put("DeathTimerEnabled",(Math.abs(HUD5zig.set.get("DeathTimerEnabled")) == 1)?0:1);
		return new StringTextComponent("Timer toggled to "+((HUD5zig.set.get("DeathTimerEnabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Sets the alignment of the death timer. Usage: /5h setDeathTimerAlignment <0|1>",alias="sdta")
	public static IFormattableTextComponent setDeathTimerAlignment(String[] args){
		if(args.length==2) {
			try {
				HUD5zig.set.put("DeathTimerAlignment",Integer.parseInt(args[1]));
				return new StringTextComponent("Successfully updated death timer alignment.");
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument 'alignment'");
	}
}
