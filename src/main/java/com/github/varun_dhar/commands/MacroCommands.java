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

public class MacroCommands {
	public static HashMap<String,String> macros = new HashMap<>();

	public MacroCommands(){
		readMacros();
	}

	private void readMacros(){//read macros into dictionary
		try {
			File cfg = new File("mods/5zigHUDMacros.cfg");
			if(!cfg.isFile())
			{
				URL defaultConfig = getClass().getResource("/com/github/varun_dhar/5zigHUDMacros.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig,cfg);
				StartupMessenger.messages.add("The 5zigHUD macro configuration was not found. A new one was created.");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDMacros.cfg"));
				String line;
				while((line = reader.readLine()) != null) {
					if(line.charAt(0) == '#')
					{
						continue;
					}
					String[] macro = line.split(";");
					macros.put(macro[0],macro[1]);
				}
			}
		}catch (IOException e)
		{
			StartupMessenger.messages.add("The 5zigHUD macro configuration could not be read.");
		}
	}
	@CommandParser.Command(help="Adds a macro. Usage: /5h addMacro <macro name> <macro value>",alias="am")
	public static IFormattableTextComponent addMacro(String[] args){
		if(args.length>=3)
		{
			StringBuilder cmd = new StringBuilder();
			for(int i = 2;i<args.length;i++)
			{
				cmd.append(args[i]).append(" ");
			}
			macros.put(args[1],cmd.toString());
			return new StringTextComponent((writeMacros())?"Macro saved successfully.":"Error saving macro. Please try again later.");
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Deletes a macro. Usage: /5h delMacro <macro name>",alias="dm")
	public static IFormattableTextComponent delMacro(String[] args){
		if(args.length==2)
		{
			if(macros.remove(args[1]) != null && writeMacros())
			{
				return new StringTextComponent("Macro deleted successfully.");
			}
			else
			{
				return new StringTextComponent("Error deleting macro. Please try again later.");
			}
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Lists all macros.",alias="lm")
	public static IFormattableTextComponent listMacros(String[] args){
		StringBuilder macroList = new StringBuilder();
		macroList.append("Macros: ");
		macros.forEach((k,v)-> macroList.append(k).append(", "));
		macroList.deleteCharAt(macroList.length()-2);
		return new StringTextComponent(macroList.toString());
	}
	private static boolean writeMacros()
	{
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDMacros.cfg",false));
			String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUDMacros.cfg"), Charset.defaultCharset());
			contents = contents.substring(0,contents.indexOf('\n',contents.lastIndexOf('#'))+1);
			writer.write(contents);
			for(HashMap.Entry<String, String> entry : macros.entrySet())
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
