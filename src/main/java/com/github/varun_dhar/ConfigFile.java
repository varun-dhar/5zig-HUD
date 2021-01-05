package com.github.varun_dhar;

public class ConfigFile {
	//probably a better way to do this, but i wanted it bundled
	//stores default config files
	public static String configContents = "#This is the config file for 5zigHUD.\n" +
			"#Using this file, you can customize the features of the mod.\n" +
			"#Lines prefaced with # signs are comments and are not read by 5zigHUD.\n" +
			"#At this time, 5zigHUD lets you customize the position of the HUD and \n" +
			"#armor panes, as well as their alignment and whether or not they are enabled.\n" +
			"#If the configuration is invalid, default settings will be used, and a message \n" +
			"#will appear at the end of this file that says \"Configuration Invalid\".\n" +
			"#In order to set default values for a feature, input -1.\n" +
			"#To turn on features, input 1. To turn them off, input 0.\n" +
			"#To set position of features, input their X and Y coordinates in their X and Y fields.\n" +
			"#To set alignment of features, input 0 for horizontal and 1 for vertical.\n" +
			"#To reset this file to default settings, delete it. It will be recreated next time the mod runs.\n" +
			"HUD-X=-1\n" +
			"HUD-Y=-1\n" +
			"HUD-Alignment=-1\n" +
			"HUD-Enabled=-1\n" +
			"ArmorX=-1\n" +
			"ArmorY=-1\n" +
			"ArmorAlignment=-1\n" +
			"ArmorEnabled=-1\n" +
			"DeathTimerX=-1\n" +
			"DeathTimerY=-1\n" +
			"DeathTimerAlignment=-1\n" +
			"DeathTimerEnabled=-1\n" +
			"UpdaterEnabled=-1\n";
	public static String macroContents = "#This is the config file for the macro feature of 5zig-HUD\n" +
			"#Lines prefaced with # signs are comments and not read by 5zig-HUD\n" +
			"#Macros are written in the format [macro name];[command/message]\n" +
			"#For example, a macro that wrote the message \"Welcome\" when \"hi\" was inputted\n" +
			"#would be written like this: hi;Welcome\n" +
			"#Macros can also be added with commands\n" +
			"#See https://github.com/varun-dhar/5zig-HUD/wiki for more information\n";
	public static String coordsContents = "#This is the config file for the coordinate mapping feature of 5zig-HUD\n" +
			"#Lines prefaced with # signs are comments and are not read by 5zig-HUD\n" +
			"#Coordinate mappings are written in the format \n" +
			"#[location name];X: [x coordinate] Y: [y coordinate] Z: [z coordinate]\n" +
			"#For example, a coordinate mapping that stored the \n" +
			"#location \"home\" as X: 0 Y: 70 Z: 0 would be writen as \n" +
			"#home;X: 0 Y: 70 Z: 0 \n" +
			"#Mappings can be created with the saveCoords and saveCurrentCoords commands\n" +
			"#Mappings can be printed by with the getCoords command, which takes the \n" +
			"#location name as an argument\n" +
			"#See https://github.com/varun-dhar/5zig-HUD/wiki for more information\n";
}
