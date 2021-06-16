/*
   Copyright 2021 Varun Dhar

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

package com.github.varun_dhar.commands;

import com.github.varun_dhar.CommandParser;
import com.github.varun_dhar.StartupMessenger;
import com.google.common.io.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public class CoordinateCommands {
	public static HashMap<String, String> savedCoords = new HashMap<>();

	public CoordinateCommands(){
		readCoords();
	}

	private void readCoords(){//reads macros into dictionary
		try {
			File cfg = new File("mods/5zigHUDCoords.cfg");
			if(!cfg.isFile())
			{
				URL defaultConfig = getClass().getResource("/com/github/varun_dhar/5zigHUDCoords.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig,cfg);
				StartupMessenger.messages.add("The 5zigHUD coordinate configuration was not found. A new one was created.");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDCoords.cfg"));
				String line;
				while((line = reader.readLine()) != null) {
					if(line.charAt(0) == '#')
					{
						continue;
					}
					String[] macros = line.split(";");
					savedCoords.put(macros[0],macros[1]);
				}
			}
		}catch (IOException e)
		{
			StartupMessenger.messages.add("The 5zigHUD coordinate configuration could not be read.");
		}
	}

	@CommandParser.Command(help="Saves specified coordinates. Usage: /5h saveCoords <name> <X> <Y> <Z>",alias="sc")
	public static IFormattableTextComponent saveCoords(String[] args){
		if(args != null && args.length==4)
		{
			savedCoords.put(args[0],"X: "+args[1]+" Y: "+args[2]+" Z: "+args[3]);
			return new StringTextComponent((writeCoords())?"Coordinates saved successfully.":"Error saving coordinates. Please try again later.");
		}
		return new StringTextComponent("Invalid argument(s)");
	}
	@CommandParser.Command(help="Saves current coordinates. Usage: /5h saveCoords <name>",alias="scc")
	public static IFormattableTextComponent saveCurrentCoords(String[] args){
		if(args != null && args.length == 1) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player != null) {
				saveCoords(new String[]{args[0], String.valueOf((int) player.lastTickPosX),
						String.valueOf((int) player.lastTickPosY), String.valueOf((int) player.lastTickPosZ)});
			}
			return null;
		}
		return new StringTextComponent("Missing argument 'name'.");
	}
	@CommandParser.Command(help="Deletes the coordinates of a specified location. Usage: /5h delCoords <name>",alias="dc")
	public static IFormattableTextComponent delCoords(String[] args){
		if(args != null && args.length==1)
		{
			if(savedCoords.remove(args[0]) != null && writeCoords())
			{
				return new StringTextComponent("Coordinates deleted successfully.");
			}
			else
			{
				return new StringTextComponent("Error deleting coordinates. Please try again later.");
			}
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Gets the coordinates of a specified location. Usage: /5h getCoords <location name>",alias="gc")
	public static IFormattableTextComponent getCoords(String[] args){
		if(args != null && args.length==1)
		{
			String coords = savedCoords.get(args[0]);
			return new StringTextComponent((coords!=null)?coords:"Invalid location.");
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Lists all coordinate locations.",alias="lc")
	public static IFormattableTextComponent listCoords(String[] args){
		StringBuilder coordsList = new StringBuilder();
		coordsList.append("Locations: \n");
		savedCoords.forEach((k,v)-> coordsList.append(k).append(" ").append(v).append("\n"));
		return new StringTextComponent(coordsList.toString());
	}
	private static boolean writeCoords(){
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDCoords.cfg",false));
			String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUDCoords.cfg"), Charset.defaultCharset());
			contents = contents.substring(0,contents.indexOf('\n',contents.lastIndexOf('#'))+1);
			writer.write(contents);
			for(HashMap.Entry<String, String> entry : savedCoords.entrySet())
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

}
