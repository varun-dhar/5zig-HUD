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
import com.github.doggo4242.StartupMessenger;
import com.google.common.io.Resources;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ActionCommands {
	public static HashMap<String,String[]> actions = new HashMap<>();

	public ActionCommands(){
		readActions();
	}

	private void readActions(){//read actions into dictionary
		try {
			File cfg = new File("mods/5zigHUDActions.cfg");
			if(!cfg.isFile())
			{
				URL defaultConfig = getClass().getResource("/com/github/doggo4242/5zigHUDActions.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig,cfg);
				StartupMessenger.messages.add("The 5zigHUD action configuration was not found. A new one was created.");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDActions.cfg"));
				String line;
				while((line = reader.readLine()) != null) {
					if(line.charAt(0) == '#')
					{
						continue;
					}
					String[] action = line.split(";");
					actions.put(action[0],action[1].split(":"));
				}
			}
		}catch (IOException e)
		{
			StartupMessenger.messages.add("The 5zigHUD action configuration could not be read.");
		}
	}
	@CommandParser.Command(help="Adds an action. Usage: /5h addAction <action name> <action 1>:<action 2>...",alias="aa")
	public static IFormattableTextComponent addAction(String[] args){
		if(args.length>=3)
		{
			StringBuilder cmd = new StringBuilder();
			for(int i = 2;i<args.length;i++)
			{
				if(args[i].equals(args[1])){
					return new StringTextComponent("Actions cannot be recursive.");
				}
				cmd.append(args[i]).append(" ");
			}
			actions.put(args[1],cmd.toString().split(":"));
			return new StringTextComponent((writeActions())?"Action saved successfully.":"Error saving action. Please try again later.");
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Deletes an action. Usage: /5h delAction <action name>",alias="da")
	public static IFormattableTextComponent delAction(String[] args){
		if(args.length==2)
		{
			if(actions.remove(args[1]) != null && writeActions())
			{
				return new StringTextComponent("Action deleted successfully.");
			}
			else
			{
				return new StringTextComponent("Error deleting action. Please try again later.");
			}
		}
		return new StringTextComponent("Invalid argument(s).");
	}
	@CommandParser.Command(help="Lists all actions.",alias="la")
	public static IFormattableTextComponent listActions(String[] args){
		StringBuilder actionList = new StringBuilder();
		actionList.append("Actions: ");
		actions.forEach((k,v)-> actionList.append(k).append(", "));
		actionList.deleteCharAt(actionList.length()-2);
		return new StringTextComponent(actionList.toString());
	}
	private static boolean writeActions()
	{
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDActions.cfg",false));
			String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUDActions.cfg"), Charset.defaultCharset());
			contents = contents.substring(0,contents.indexOf('\n',contents.lastIndexOf('#'))+1);
			writer.write(contents);
			for(HashMap.Entry<String, String[]> entry : actions.entrySet())
			{
				writer.write(entry.getKey()+';'+String.join(":",entry.getValue())+'\n');
			}
			writer.close();
		}catch(IOException e)
		{
			return false;
		}
		return true;
	}
}
