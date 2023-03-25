/*
   Copyright 2023 Varun Dhar

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
import com.github.varun_dhar.hud5zig.misc.UpdateChecker;
import com.google.common.io.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ClickEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

public class GeneralCommands {
	private final static LocalPlayer player = Minecraft.getInstance().player;

	@CommandParser.Command(help = "Dumps current coordinates to chat.", alias = "d")
	public static MutableComponent dump() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			return Component.literal(String.format("X: %d Y: %d Z: %d", (int) player.getX(), (int) player.getY(), (int) player.getZ()));
		}
		return null;
	}

	@CommandParser.Command(help = "Dumps death coordinates to chat, if they exist.", alias = "dd")
	public static MutableComponent dumpDeath() {
		return CoordinateCommands.getCoords("lastDeath");
	}

	@CommandParser.Command(help = "Checks for 5zig-HUD updates.", alias = "cfu")
	public static MutableComponent checkForUpdates() {
		UpdateChecker.updateNotif(true);
		return null;
	}

	@CommandParser.Command(help = "Saves changed settings.", alias = "s")
	public static MutableComponent save() {
		new Thread(() -> {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg", false));
				String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUD.cfg"), Charset.defaultCharset());
				contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
				writer.write(contents);
				for (var entry : HUD5zig.Options.entrySet()) {
					writer.write(String.format("%s=%d\n", entry.getKey(), HUD5zig.settings.get(entry.getValue())));
				}
				writer.close();
			} catch (IOException e) {
				player.displayClientMessage(Component.literal("Settings failed to save."), true);
			}
			player.displayClientMessage(Component.literal("Settings saved."), true);
		}).start();
		return null;
	}

	@CommandParser.Command(help = "Turns update notifications on or off.", alias = "tu")
	public static MutableComponent toggleUpdater() {
		HUD5zig.settings.put(HUD5zig.Options.UPDATER_ENABLED, Math.abs((HUD5zig.settings.get(HUD5zig.Options.UPDATER_ENABLED) == 1) ? 0 : 1));
		return Component.literal("Updater toggled to " + ((HUD5zig.settings.get(HUD5zig.Options.UPDATER_ENABLED) == 0) ? "off." : "on."));
	}

	@CommandParser.Command(help = "Shows help for a single command or all commands. Usage: /5h help [command name/alias]", alias = "h")
	public static MutableComponent help() {
		StringBuilder helpStr = new StringBuilder();
		helpStr.append("Commands available:\n");
		for (var entry : CommandParser.commandHelp.entrySet()) {
			helpStr.append("/5h ").append(entry.getKey()).append(' ').append(entry.getValue()).append('\n');
		}
		helpStr.append("For more information, visit the ");
		var wikiLink = Component.literal("wiki.");
		ClickEvent openWiki = new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/varun-dhar/5zig-HUD/wiki");
		wikiLink.setStyle(wikiLink.getStyle().withUnderlined(true));
		wikiLink.setStyle(wikiLink.getStyle().withClickEvent(openWiki));
		return Component.literal(helpStr.toString()).append(wikiLink);
	}

	@CommandParser.Command(help = "Shows help for a single command or all commands. Usage: /5h help [command name/alias]", alias = "h")
	public static MutableComponent help(String command) {
		String help;
		if ((help = CommandParser.commandHelp.get(command)) == null &&
				(help = CommandParser.commandHelp.get(CommandParser.commandAliases.get(command))) == null) {
			return Component.literal("Command not found.");
		}
		return Component.literal(help);
	}

	@CommandParser.Command(help = "Resets all settings to defaults.", alias = "r")
	public static MutableComponent reset() {
		for (var op : HUD5zig.Options.values()) {
			HUD5zig.settings.put(op, -1);
		}
		return Component.literal("Defaults restored.");
	}

	@CommandParser.Command(help = "Internal command, turns updater off and saves settings.", alias = "uos")
	public static MutableComponent updaterOffSave() {
		HUD5zig.settings.put(HUD5zig.Options.UPDATER_ENABLED, 0);
		save();
		return null;
	}
}
