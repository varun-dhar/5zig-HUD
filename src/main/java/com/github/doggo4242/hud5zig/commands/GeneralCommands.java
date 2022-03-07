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

package com.github.doggo4242.hud5zig.commands;

import com.github.doggo4242.hud5zig.HUD5zig;
import com.github.doggo4242.hud5zig.misc.UpdateChecker;
import com.google.common.io.Resources;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.ClickEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

public class GeneralCommands {
	private final static LocalPlayer player = Minecraft.getInstance().player;

	@CommandParser.Command(help = "Dumps current coordinates to chat.", alias = "d")
	public static MutableComponent dump(String[] args) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			return new TextComponent(String.format("X: %d Y: %d Z: %d", (int) player.getX(), (int) player.getY(), (int) player.getZ()));
		}
		return null;
	}

	@CommandParser.Command(help = "Dumps death coordinates to chat, if they exist.", alias = "dd")
	public static MutableComponent dumpDeath(String[] args) {
		return CoordinateCommands.getCoords(new String[]{"lastDeath"});
	}

	@CommandParser.Command(help = "Checks for 5zig-HUD updates.", alias = "cfu")
	public static MutableComponent checkForUpdates(String[] args) {
		UpdateChecker.updateNotif(true);
		return null;
	}

	@CommandParser.Command(help = "Saves changed settings.", alias = "s")
	public static MutableComponent save(String[] args) {
		new Thread(() -> {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg", false));
				String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUD.cfg"), Charset.defaultCharset());
				contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
				writer.write(contents);
				for (var entry : HUD5zig.Options.entrySet()) {
					writer.write(String.format("%s=%d\n", entry.getKey(), HUD5zig.settings.get(entry.getValue())));
				}
				writer.close();
			} catch (IOException e) {
				player.sendMessage(new TextComponent("Settings failed to save."), Util.NIL_UUID);
			}
			player.sendMessage(new TextComponent("Settings saved."), Util.NIL_UUID);
		}).start();
		return null;
	}

	@CommandParser.Command(help = "Turns update notifications on or off.", alias = "tu")
	public static MutableComponent toggleUpdater(String[] args) {
		HUD5zig.settings.put(HUD5zig.Options.UPDATER_ENABLED, Math.abs((HUD5zig.settings.get(HUD5zig.Options.UPDATER_ENABLED) == 1) ? 0 : 1));
		return new TextComponent("Updater toggled to " + ((HUD5zig.settings.get(HUD5zig.Options.UPDATER_ENABLED) == 0) ? "off." : "on."));
	}

	@CommandParser.Command(help = "Shows help for a single command or all commands. Usage: /5h help [command name/alias]", alias = "h")
	public static MutableComponent help(String[] args) {
		if(args.length == 0){
			StringBuilder helpStr = new StringBuilder();
			helpStr.append("Commands available:\n");
			for (var entry : CommandParser.commandHelp.entrySet()) {
				helpStr.append("/5h ").append(entry.getKey()).append(' ').append(entry.getValue()).append('\n');
			}
			helpStr.append("For more information, visit the ");
			TextComponent wikiLink = new TextComponent("wiki.");
			ClickEvent openWiki = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/doggo4242/5zig-HUD/wiki");
			wikiLink.setStyle(wikiLink.getStyle().setUnderlined(true));
			wikiLink.setStyle(wikiLink.getStyle().withClickEvent(openWiki));
			return new TextComponent(helpStr.toString()).append(wikiLink);
		} else if (args.length == 1) {
			String help;
			if ((help = CommandParser.commandHelp.get(args[0])) == null &&
					(help = CommandParser.commandHelp.get(CommandParser.commandAliases.get(args[0]))) == null) {
				return new TextComponent("Command not found.");
			}
			return new TextComponent(help);
		}
		return new TextComponent("Invalid argument(s).");
	}

	@CommandParser.Command(help = "Resets all settings to defaults.", alias = "r")
	public static MutableComponent reset(String[] args) {
		for (var op : HUD5zig.Options.values()) {
			HUD5zig.settings.put(op, -1);
		}
		return new TextComponent("Defaults restored.");
	}

	@CommandParser.Command(help = "Internal command, turns updater off and saves settings.", alias = "uos")
	public static MutableComponent updaterOffSave(String[] args) {
		HUD5zig.settings.put(HUD5zig.Options.UPDATER_ENABLED, 0);
		save(null);
		return null;
	}
}
