package com.github.varun_dhar.commands;

import com.github.varun_dhar.CommandParser;
import com.github.varun_dhar.HUD;
import com.github.varun_dhar.HUD5zig;
import com.github.varun_dhar.UpdateChecker;
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
		return new StringTextComponent(String.format("X: %d Y: %d Z: %d", HUD.deathCoords[0],HUD.deathCoords[1],HUD.deathCoords[2]));
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
			String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUD.cfg"), Charset.defaultCharset());
			contents = contents.substring(0,contents.indexOf('\n',contents.lastIndexOf('#'))+1);
			writer.write(contents);
			for (Map.Entry<String, Integer> entry : HUD5zig.set.entrySet()) {
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
		HUD5zig.settings[12] = (Math.abs(HUD5zig.settings[7]) == 1)?0:1;
		return new StringTextComponent("HUD toggled to "+((HUD5zig.set.get("UpdaterEnabled") == 0)?"off.":"on."));
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
		ClickEvent openWiki = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/varun-dhar/5zig-HUD/wiki");
		wikiLink.setStyle(wikiLink.getStyle().setUnderlined(true));
		wikiLink.setStyle(wikiLink.getStyle().setClickEvent(openWiki));
		return new StringTextComponent(helpStr.toString()).append(wikiLink);
	}
	@CommandParser.Command(help="Resets all settings to defaults.",alias="r")
	public static IFormattableTextComponent reset(String[] args){
		for(String op : HUD5zig.confOps){
			HUD5zig.set.put(op,-1);
		}
		return new StringTextComponent("Defaults restored.");
	}
	@CommandParser.Command(help="Internal command, turns updater off and saves settings.",alias="tus")
	public static IFormattableTextComponent updaterOffSave(String[] args){
		HUD5zig.set.put("UpdaterEnabled",0);
		save(null);
		return null;
	}
}
