package com.github.varun_dhar.commands;

import com.github.varun_dhar.CommandParser;
import com.github.varun_dhar.HUD;
import com.github.varun_dhar.HUD5zig;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class NavCommands {

	@CommandParser.Command(help="Navigates to location from current location. Usage: /5h nav <X> <Y> <Z> ",alias="n")
	public static IFormattableTextComponent nav(String[] args){
		if(args.length == 2)
		{
			HUD.navLoc = CoordinateCommands.savedCoords.get(args[1]);
			if(HUD.navLoc == null){
				return new StringTextComponent("Invalid argument.");
			}
		}
		else if(args.length == 4)
		{
			HUD.navLoc = "X: "+args[1]+" Y: "+args[2]+" Z: "+args[3];
		}
		else{
			return new StringTextComponent("Invalid argument.");
		}
		return new StringTextComponent("Starting navigation.");
	}
	@CommandParser.Command(help="Sets the x-coordinate of the navigation compass. Usage: /5h setNavX <X>",alias="snx")
	public static IFormattableTextComponent setNavX(String[] args){
		if(args.length == 2) {
			try {
				HUD5zig.set.put("NavX",Integer.parseInt(args[1]));
				return new StringTextComponent("Set navigation X to "+HUD5zig.set.get("NavX"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument <X>.");
	}
	@CommandParser.Command(help="Sets the y-coordinate of the navigation compass. Usage: /5h setNavY <Y>",alias="sny")
	public static IFormattableTextComponent setNavY(String[] args){
		if(args.length == 2) {
			try {
				HUD5zig.set.put("NavY", Integer.parseInt(args[1]));
				return new StringTextComponent("Set navigation Y to "+HUD5zig.set.get("NavY"));
			}catch(NumberFormatException e)
			{
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Missing argument <Y>.");
	}
	@CommandParser.Command(help="Stops navigation and hides compass.",alias="fn")
	public static IFormattableTextComponent finishNav(String[] args){
		HUD.navLoc = null;
		return new StringTextComponent("Navigation ended.");
	}
}
