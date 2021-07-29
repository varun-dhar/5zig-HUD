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

package com.github.doggo4242;

import com.github.doggo4242.HUD.HUDRenderer;
import com.github.doggo4242.commands.CommandParser;
import com.github.doggo4242.misc.DeathHandler;
import com.github.doggo4242.misc.StartupMessenger;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Mod(HUD5zig.MODID)

public class HUD5zig//setup stuff
{
	public static final String MODID = "hudmod";
	public static final int version = 8;
	public static final List<String> confOps = Arrays.asList("HUD-X", "HUD-Y", "HUD-Alignment", "HUD-Enabled", "ArmorX",
			"ArmorY", "ArmorAlignment", "ArmorEnabled", "DeathTimerX", "DeathTimerY", "DeathTimerAlignment",
			"DeathTimerEnabled", "UpdaterEnabled", "NavX", "NavY","NavToDeathEnabled");
	public static HashMap<String, Integer> settings = new HashMap<>();
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
	private void readSettings(){
		try {
			File cfg = new File("mods/5zigHUD.cfg");
			if(!cfg.isFile())
			{
				URL defaultConfig = getClass().getResource("/com/github/doggo4242/5zigHUD.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig,cfg);
				for(String op : confOps){
					settings.put(op,-1);
				}
				StartupMessenger.messages.add(
						"The configuration for 5zigHUD was not found. A new one was created with default settings");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader(cfg));
				for(String s = reader.readLine();s != null;s = reader.readLine()){
					s = s.trim();
					if(s.charAt(0) == '#')
						continue;
					String[] fields = StringUtils.split(s,"=");
					if(fields.length == 2){
						fields[0] = fields[0].trim();
						fields[1] = fields[1].trim();
						if(settings.containsKey(fields[0]) || !confOps.contains(fields[0])){
							continue;
						}
						try{
							settings.put(fields[0],Integer.parseInt(fields[1]));
						}catch(NumberFormatException ignored){}
					}
				}
				reader.close();
				if (!settings.containsKey("DVDEnabled")) {
					settings.put("DVDEnabled", 0);
				}
				if (settings.size() < confOps.size()) {
					StartupMessenger.messages.add(
							"The configuration for 5zigHUD is invalid. Default settings for the invalid/missing fields were used.");
					BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg",true));
					for(String op : confOps){
						if(!settings.containsKey(op)) {
							settings.put(op, -1);
							writer.write(op+"=-1");
						}
					}
					writer.close();
				}
			}
		}catch (IOException e) {
			StartupMessenger.messages.add("The configuration for 5zigHUD could not be read. Default settings were used.");
			for(String op : confOps){
				settings.put(op,-1);
			}
		}
	}
}
