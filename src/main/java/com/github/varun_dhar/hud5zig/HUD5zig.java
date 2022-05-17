/*
   Copyright 2022 Varun Dhar

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

package com.github.varun_dhar.hud5zig;

import com.github.varun_dhar.hud5zig.HUD.HUDRenderer;
import com.github.varun_dhar.hud5zig.commands.CommandParser;
import com.github.varun_dhar.hud5zig.misc.DeathHandler;
import com.github.varun_dhar.hud5zig.misc.StartupMessenger;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Mod(HUD5zig.MODID)
public class HUD5zig{
	public static final String MODID = "hudmod";
	public static final int version = 11;

	public enum Options {
		HUD_X, HUD_Y, HUD_ALIGN, HUD_ENABLED, ARMOR_X, ARMOR_Y, ARMOR_ALIGN, ARMOR_ENABLED, DT_X, DT_Y, DT_ALIGN, DT_ENABLED,
		UPDATER_ENABLED, NAV_X, NAV_Y, NAV_TO_DEATH_ENABLED, DVD_DISABLED;
		public static final Options[] vals = values();
		public static final int size = vals.length;
		private static final Map<String, Options> dir = ImmutableMap.<String, Options>builder()
				.put("HUD-X", HUD_X).put("HUD-Y", HUD_Y).put("HUD-Alignment", HUD_ALIGN)
				.put("HUD-Enabled", HUD_ENABLED).put("ArmorX", ARMOR_X).put("ArmorY", ARMOR_Y)
				.put("ArmorAlignment", ARMOR_ALIGN).put("ArmorEnabled", ARMOR_ENABLED)
				.put("DeathTimerX", DT_X).put("DeathTimerY", DT_Y).put("DeathTimerAlignment", DT_ALIGN)
				.put("DeathTimerEnabled", DT_ENABLED).put("UpdaterEnabled", UPDATER_ENABLED)
				.put("NavX", NAV_X).put("NavY", NAV_Y).put("NavToDeathEnabled", NAV_TO_DEATH_ENABLED).build();

		public static Options get(String name) {
			return dir.get(name);
		}

		public static Set<Map.Entry<String,Options>> entrySet(){
			return dir.entrySet();
		}
		@Override
		public String toString() {
			return null;
		}
	}

	public static EnumMap<Options, Integer> settings = new EnumMap<>(Options.class);

	public HUD5zig() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

	private void clientSetup(FMLClientSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(new HUDRenderer());
		MinecraftForge.EVENT_BUS.register(new CommandParser());
		MinecraftForge.EVENT_BUS.register(StartupMessenger.class);
		MinecraftForge.EVENT_BUS.register(DeathHandler.class);
		readSettings();
	}

	//read settings into map
	private void readSettings() {
		settings.put(Options.DVD_DISABLED, -1);
		try {
			File cfg = new File("mods/5zigHUD.cfg");
			if (!cfg.isFile()) {
				URL defaultConfig = getClass().getResource("/com/github/varun_dhar/5zigHUD.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig, cfg);
				for (var op : Options.vals) {
					settings.put(op, -1);
				}
				StartupMessenger.addMessage(
						"The configuration for 5zigHUD was not found. A new one was created with default settings");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader(cfg));
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {
					s = s.trim();
					if (s.charAt(0) == '#')
						continue;
					String[] fields = StringUtils.split(s, "=");
					if (fields.length == 2) {
						fields[0] = fields[0].trim();
						fields[1] = fields[1].trim();
						Options op;
						if ((op = Options.get(fields[0])) == null || (settings.containsKey(op) && op != Options.DVD_DISABLED)) {
							continue;
						}
						try {
							settings.put(op, Integer.parseInt(fields[1]));
						} catch (NumberFormatException ignored) {
						}
					}
				}
				reader.close();
				if (settings.size() < Options.size) {
					StartupMessenger.addMessage(
							"The configuration for 5zigHUD is invalid. Default settings for the invalid/missing fields were used.");
					BufferedWriter writer = new BufferedWriter(new FileWriter(cfg, true));
					for (var op : Options.vals) {
						if (!settings.containsKey(op)) {
							settings.put(op, -1);
							writer.write(settings.get(op) + "=-1\n");
						}
					}
					writer.close();
				}
			}
		} catch (IOException e) {
			StartupMessenger.addMessage("The configuration for 5zigHUD could not be read. Default settings were used.");
			for (var op : Options.vals) {
				settings.put(op, -1);
			}
		}
	}
}
