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
import com.github.doggo4242.UpdateChecker;
import com.github.doggo4242.components.DeathTimer;
import com.google.common.io.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class GeneralCommands {
	@CommandParser.Command(help="Dumps current coordinates to chat.",alias="d")
	public static IFormattableTextComponent dump(String[] args){
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player != null){
			return new StringTextComponent(String.format("X: %d Y: %d Z: %d",(int)player.lastTickPosX,(int)player.lastTickPosY,(int)player.lastTickPosZ));
		}else{
			return null;
		}
	}
	@CommandParser.Command(help="Dumps death coordinates to chat (dumps 0,0,0 if none exist).",alias="dd")
	public static IFormattableTextComponent dumpDeath(String[] args){
		return new StringTextComponent(String.format("X: %d Y: %d Z: %d", DeathTimer.deathCoords[0], DeathTimer.deathCoords[1], DeathTimer.deathCoords[2]));
	}
	@CommandParser.Command(help="Checks for 5zig-HUD updates.",alias="cfu")
	public static IFormattableTextComponent checkForUpdates(String[] args){
		UpdateChecker.updateNotif(true);
		return null;
	}
	@CommandParser.Command(help="Saves changed settings.",alias="s")
	public static IFormattableTextComponent save(String[] args){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg",false));
			String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUD.cfg"), Charset.defaultCharset());
			contents = contents.substring(0,contents.indexOf('\n',contents.lastIndexOf('#'))+1);
			writer.write(contents);
			for (Map.Entry<String, Integer> entry : HUD5zig.settings.entrySet()) {
				writer.write(String.format("%s=%d", entry.getKey(), entry.getValue()));
			}
			writer.close();
		}catch(IOException e)
		{
			return new StringTextComponent("Settings failed to save.");
		}
		return new StringTextComponent("Settings saved.");
	}
	@CommandParser.Command(help="Turns update notifications on or off.",alias="tu")
	public static IFormattableTextComponent toggleUpdater(String[] args){
		HUD5zig.settings.put("UpdaterEnabled",Math.abs((HUD5zig.settings.get("UpdaterEnabled") == 1)?0:1));
		return new StringTextComponent("HUD toggled to "+((HUD5zig.settings.get("UpdaterEnabled") == 0)?"off.":"on."));
	}
	@CommandParser.Command(help="Shows this message.",alias="h")
	public static IFormattableTextComponent help(String[] args){
		StringBuilder helpStr = new StringBuilder();
		helpStr.append("Commands available:\n");
		for(HashMap.Entry<String,String> entry : CommandParser.commandHelp.entrySet()){
			helpStr.append("/5h ").append(entry.getKey()).append(" ").append(entry.getValue()).append('\n');
		}
		helpStr.append("For more information, visit the ");
		StringTextComponent wikiLink = new StringTextComponent("wiki\n");
		ClickEvent openWiki = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/doggo4242/5zig-HUD/wiki");
		wikiLink.setStyle(wikiLink.getStyle().setUnderlined(true));
		wikiLink.setStyle(wikiLink.getStyle().setClickEvent(openWiki));
		return new StringTextComponent(helpStr.toString()).append(wikiLink);
	}
	@CommandParser.Command(help="Resets all settings to defaults.",alias="r")
	public static IFormattableTextComponent reset(String[] args){
		for(String op : HUD5zig.confOps){
			HUD5zig.settings.put(op,-1);
		}
		return new StringTextComponent("Defaults restored.");
	}
	@CommandParser.Command(help="Internal command, turns updater off and saves settings.",alias="uos")
	public static IFormattableTextComponent updaterOffSave(String[] args){
		HUD5zig.settings.put("UpdaterEnabled",0);
		save(null);
		return null;
	}
}
