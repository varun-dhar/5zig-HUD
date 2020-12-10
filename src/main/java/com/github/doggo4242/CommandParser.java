package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CommandParser {
	public static Map<String,String> macros = new HashMap<String,String>();
	public static Map<String, String> saveCoords = new HashMap<String,String>();
	@SubscribeEvent
	public void parseChat(ClientChatEvent event)
	{
		int pos;
		List<String> cmds = Arrays.asList("dump","dumpDeath","toggleTimer","toggleHUD","toggleArmorPane",
				"checkForUpdates","save","toggleUpdater","help","setHUDX","setHUDY","setArmorX",
				"setArmorY","setArmorAlignment","setHUDAlignment","setDeathTimerAlignment","reset","updaterOffSave",
				"saveCoords","saveCurrentCoords","addMacro","delMacro","getCoords","delCoords","listMacros","listCoords");
		List<String> shCmds = Arrays.asList("d","dd","tt","th","tap","cfu","s","tu","h",
				"shx","shy","sax","say","saa","sha","sdta","r","uos","sc","scc","am","dm","gc","dc","lm","lc");
		String[] helpRef = {"Dumps coordinates to chat.",
		"Dumps death coordinates to chat (dumps 0,0,0 if none exist).",
		"Shows/hides death timer.",
		"Shows/hides HUD.",
		"Shows/hides armor pane.",
		"Checks for 5zig-HUD updates.",
		"Saves changed settings.",
		"Turns update notification on or off.",
		"Shows this message.",
		"Sets the x-coordinate of the HUD. Takes 1 argument.",
		"Sets the y-coordinate of the HUD. Takes 1 argument.",
		"Sets the x-coordinate of the armor pane. Takes 1 argument.",
		"Sets the y-coordinate of the armor pane. Takes 1 argument.",
		"Sets the alignment of the armor pane. Takes 1 argument.",
		"Sets the alignment of the HUD. Takes 1 argument.",
		"Sets the alignment of the death timer. Takes 1 argument.",
		"Resets all settings to defaults.",
		"Internal command, turns updater off and saves settings.",
		"Saves specified coordinates. Takes a name for the location and the x, y, and z coordinates as arguments",
		"Saves current coordinates. Takes a name for the location as an argument",
		"Adds a macro. Takes the name of the macro and the text/command the macro executes as arguments",
		"Deletes a macro. Takes the name of the macro as an argument.",
		"Gets the coordinates of a specified location. Takes the name of the location as an argument.",
		"Deletes the coordinates of a specified location. Takes the name of the location as an argument",
		"Lists all macros",
		"Lists all coordinate locations"};
		String msg = event.getMessage();
		msg = (msg.charAt(0) == '/')?msg.substring(1):msg;
		if(macros.get(msg) != null)
		{
			event.setMessage(macros.get(msg));
		}
		if(msg.startsWith("5h"))
		{
			event.setCanceled(true);
			msg = (msg.length()>=3)?msg.substring(3):msg;
			String[] args = msg.split(" ");
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if((pos=cmds.indexOf(args[0])) == -1)
			{
				if((pos=shCmds.indexOf(args[0])) == -1) {
					player.sendMessage(new StringTextComponent("Unknown command or invalid argument. Run /5h help for a list of commands"), Util.DUMMY_UUID);
					return;
				}
			}
			switch (pos)
			{
				case 0://dump coordinates to chat
					player.sendMessage(new StringTextComponent("X: "+(int)player.lastTickPosX+" Y: "+(int)player.lastTickPosY+" Z: "+(int)player.lastTickPosZ), Util.DUMMY_UUID);
					break;
				case 1://dump death coordinates to chat
					player.sendMessage(new StringTextComponent("X: "+HUD.deathCoords[0]+" Y: "+HUD.deathCoords[1]+" Z: "+HUD.deathCoords[2]),Util.DUMMY_UUID);
					break;
				case 2://turn timer on or off depending on current state
					HUD5zig.settings[11] = (Math.abs(HUD5zig.settings[11]) == 1)?0:1;
					break;
				case 3://turn HUD on or off depending on current state
					HUD5zig.settings[3] = (Math.abs(HUD5zig.settings[3]) == 1)?0:1;
					break;
				case 4://turn armor pane on or off depending on current state
					HUD5zig.settings[7] = (Math.abs(HUD5zig.settings[7]) == 1)?0:1;
					break;
				case 5://check for updates and notify user
					UpdateChecker.updateNotif(true);
					break;
				case 6:
					if(writeSettings())
					{
						player.sendMessage(new StringTextComponent("Settings saved."),Util.DUMMY_UUID);
					}
					else{
						player.sendMessage(new StringTextComponent("Settings failed to save."),Util.DUMMY_UUID);
					}
					break;
				case 7:
					HUD5zig.settings[12] = (Math.abs(HUD5zig.settings[7]) == 1)?0:1;
					break;
				case 8:
					StringBuilder help = new StringBuilder();
					help.append("Commands available:\n");
					for (int i = 0;i<cmds.size();i++) {
						help.append("/5h ").append(cmds.get(i)).append(" ").append(helpRef[i]).append('\n');
					}
					help.append("For more information, visit the ");
					StringTextComponent wikiLink = new StringTextComponent("wiki\n");
					ClickEvent openWiki = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/doggo4242/5zig-HUD/wiki");
					wikiLink.setStyle(wikiLink.getStyle().setUnderlined(true));
					wikiLink.setStyle(wikiLink.getStyle().setClickEvent(openWiki));
					player.sendMessage(new StringTextComponent(help.toString()).append(wikiLink),Util.DUMMY_UUID);
					break;
				case 9:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.HUDX.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 10:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.HUDY.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 11:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.ARX.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 12:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.ARY.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 13:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.ARAL.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 14:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.HUDAL.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 15:
					if(args.length>1) {
						HUD5zig.settings[HUD.set.DETIAL.ordinal()] = Integer.parseInt(args[1]);
					}
					break;
				case 16:
					Arrays.fill(HUD5zig.settings,-1);
					player.sendMessage(new StringTextComponent("Defaults restored."),Util.DUMMY_UUID);
					break;
				case 17:
					HUD5zig.settings[12] = (Math.abs(HUD5zig.settings[7]) == 1)?0:1;
					writeSettings();
					break;
				case 18:
				case 19:
					if(args.length==5)
					{
						saveCoords.put(args[1],"X: "+args[2]+" Y: "+args[3]+" Z: "+args[4]);
					}
					else if((args[0].equals("saveCurrentCoords") || args[0].equals("scc")) && args.length > 1)
					{
						saveCoords.put(args[1],"X: "+(int)player.lastTickPosX+" Y: "+(int)player.lastTickPosY+" Z: "+(int)player.lastTickPosZ);
					}
					else
					{
						player.sendMessage(new StringTextComponent("Invalid argument(s)."),Util.DUMMY_UUID);
						break;
					}
					if(writeCoords())
					{
						player.sendMessage(new StringTextComponent("Coordinates saved successfully."),Util.DUMMY_UUID);
					}
					else
					{
						player.sendMessage(new StringTextComponent("Error saving coordinates. Please try again later."),Util.DUMMY_UUID);
					}
					break;
				case 20:
					if(args.length>=3)
					{
						StringBuilder cmd = new StringBuilder();
						for(int i = 2;i<args.length;i++)
						{
							cmd.append(args[i]).append(" ");
						}
						macros.put(args[1],cmd.toString());
						if(writeMacros())
						{
							player.sendMessage(new StringTextComponent("Macro saved successfully."),Util.DUMMY_UUID);
						}
						else
						{
							player.sendMessage(new StringTextComponent("Error saving macro. Please try again later."),Util.DUMMY_UUID);
						}
					}
					else
					{
						player.sendMessage(new StringTextComponent("Invalid argument(s)."),Util.DUMMY_UUID);
					}
					break;
				case 21:
					if(args.length==2)
					{
						if(macros.remove(args[1]) != null && writeMacros())
						{
							player.sendMessage(new StringTextComponent("Macro deleted successfully."),Util.DUMMY_UUID);
						}
						else
						{
							player.sendMessage(new StringTextComponent("Error deleting macro. Please try again later."),Util.DUMMY_UUID);
						}
					}
					else
					{
						player.sendMessage(new StringTextComponent("Invalid argument(s)."),Util.DUMMY_UUID);
					}
					break;
				case 22:
					if(args.length==2)
					{
						String coords = saveCoords.get(args[1]);
						player.sendMessage(new StringTextComponent((coords!=null)?coords:"Invalid location."),Util.DUMMY_UUID);
					}
					else
					{
						player.sendMessage(new StringTextComponent("Invalid argument(s)."),Util.DUMMY_UUID);
					}
					break;
				case 23:
					if(args.length==2)
					{
						if(saveCoords.remove(args[1]) != null && writeCoords())
						{
							player.sendMessage(new StringTextComponent("Coordinates deleted successfully."),Util.DUMMY_UUID);
						}
						else
						{
							player.sendMessage(new StringTextComponent("Error deleting coordinates. Please try again later."),Util.DUMMY_UUID);
						}
					}
					break;
				case 24:
					StringBuilder macroList = new StringBuilder();
					macroList.append("Locations: ");
					macros.forEach((k,v)-> macroList.append(k).append(", "));
					player.sendMessage(new StringTextComponent(macroList.toString()),Util.DUMMY_UUID);
					break;
				case 25:
					StringBuilder coordsList = new StringBuilder();
					coordsList.append("Macros: \n");
					saveCoords.forEach((k,v)-> coordsList.append(k).append(" ").append(v).append("\n"));
					player.sendMessage(new StringTextComponent(coordsList.toString()),Util.DUMMY_UUID);
					break;
			}
		}
	}
	public static boolean writeSettings()
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg",false));
			writer.write(ConfigFile.configContents.substring(0,ConfigFile.configContents.indexOf('\n',ConfigFile.configContents.lastIndexOf('#')))+'\n');
			for(int i = 0;i<HUD5zig.confOps.length;i++)
			{
				writer.write(HUD5zig.confOps[i]+HUD5zig.settings[i]+'\n');
			}
			writer.close();
		}catch(IOException e)
		{
			return false;
		}
		return true;
	}
	public boolean writeCoords()
	{
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDCoords.cfg",false));
			writer.write(ConfigFile.coordsContents);
			for(Map.Entry<String, String> entry : saveCoords.entrySet())
			{
				writer.write(entry.getKey()+';'+entry.getValue()+'\n');
			}
			writer.close();
		}catch (IOException e)
		{
			return false;
		}
		return true;
	}
	public boolean writeMacros()
	{
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDMacros.cfg",false));
			writer.write(ConfigFile.macroContents);
			for(Map.Entry<String, String> entry : macros.entrySet())
			{
				writer.write(entry.getKey()+';'+entry.getValue()+'\n');
			}
			writer.close();
		}catch(IOException e)
		{
			return false;
		}
		return true;
	}
}
