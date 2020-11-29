package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CommandParser {
	@SubscribeEvent
	public void parseChat(ClientChatEvent event)
	{
		int pos;
		List<String> cmds = Arrays.asList("dump","dumpDeath","toggleTimer","toggleHUD","toggleArmorPane",
				"checkForUpdates","save","toggleUpdater","help","setHUDX","setHUDY","setArmorX",
				"setArmorY","setArmorAlignment","setHUDAlignment","setDeathTimerAlignment","reset","toggleUpdaterSave");
		List<String> shCmds = Arrays.asList("d","dd","tt","th","tap","cfu","s","tu","h",
				"shx","shy","sax","say","saa","sha","sdta","r","tus");
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
		"Internal command, turns updater on or off and saves settings."};
		System.out.println("chat fired. "+event.getMessage());
		String msg = event.getMessage().replace("/","");
		if(msg.startsWith("5h"))
		{
			event.setCanceled(true);
			msg = (msg.length()>=3)?msg.substring(3):msg;
			int arg = -2;
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if(msg.matches(".*\\d.*"))
			{
				arg = Integer.parseInt(msg.replaceAll("[^0-9]+",""));
				msg = msg.replaceAll("\\d","");
				msg = msg.replaceAll("\\s","");
			}
			if((pos=cmds.indexOf(msg)) == -1)
			{
				if((pos=shCmds.indexOf(msg)) == -1) {
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
					player.sendMessage(new StringTextComponent(help.toString()),Util.DUMMY_UUID);
					break;
				case 9:
					HUD5zig.settings[HUD.set.HUDX.ordinal()] = arg;
					break;
				case 10:
					HUD5zig.settings[HUD.set.HUDY.ordinal()] = arg;
					break;
				case 11:
					HUD5zig.settings[HUD.set.ARX.ordinal()] = arg;
					break;
				case 12:
					HUD5zig.settings[HUD.set.ARY.ordinal()] = arg;
					break;
				case 13:
					HUD5zig.settings[HUD.set.ARAL.ordinal()] = arg;
					break;
				case 14:
					HUD5zig.settings[HUD.set.HUDAL.ordinal()] = arg;
					break;
				case 15:
					HUD5zig.settings[HUD.set.DETIAL.ordinal()] = arg;
					break;
				case 16:
					Arrays.fill(HUD5zig.settings,-1);
					player.sendMessage(new StringTextComponent("Defaults restored."),Util.DUMMY_UUID);
					break;
				case 17:
					HUD5zig.settings[12] = (Math.abs(HUD5zig.settings[7]) == 1)?0:1;
					writeSettings();
					break;
			}
		}
	}
	public static boolean writeSettings()
	{
		try {
			FileWriter writer = new FileWriter("mods/5zigHUD.cfg",false);
			writer.write(ConfigFile.contents.substring(0,ConfigFile.contents.indexOf('\n',ConfigFile.contents.lastIndexOf('#'))));
			for(int i = 0;i<HUD5zig.confOps.length;i++)
			{
				writer.write(HUD5zig.confOps[i]+HUD5zig.settings[i]	+'\n');
			}
			writer.close();
		}catch(IOException e)
		{
			return false;
		}
		return true;
	}
}
