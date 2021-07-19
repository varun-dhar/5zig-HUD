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
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

public class ActionCommands {
	public static HashMap<String, String[]> actions = new HashMap<>();

	public ActionCommands() {
		readActions();
	}

	private void readActions() {
		try {
			File cfg = new File("mods/5zigHUDActions.cfg");
			if (!cfg.isFile()) {
				URL defaultConfig = getClass().getResource("/com/github/doggo4242/5zigHUDActions.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig, cfg);
				StartupMessenger.messages.add("The 5zigHUD action configuration was not found. A new one was created.");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDActions.cfg"));
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.charAt(0) == '#') {
						continue;
					}
					String[] action = StringUtils.split(line, ";");
					String[] actionArgs = StringUtils.split(action[1], ":");
					for (int i = 0; i < actionArgs.length; i++) {
						if (actionArgs[i].equals(action[0])) {
							StartupMessenger.messages.add("Actions may not be recursive. Please remove or correct action \"" + action[0] + "\".");
							break;
						} else if (i == actionArgs.length - 1) {
							actions.put(action[0], actionArgs);
						}
					}
				}
			}
		} catch (IOException e) {
			StartupMessenger.messages.add("The 5zigHUD action configuration could not be read.");
		}
	}

	@CommandParser.Command(help = "Adds an action. Usage: /5h addAction <action name> <command 1>:<command 2>...", alias = "aa")
	public static IFormattableTextComponent addAction(String[] args) {
		if (args != null && args.length >= 3) {
			StringBuilder cmd = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				cmd.append(args[i]);
				if (i != (args.length - 1)) {
					cmd.append(' ');
				}
			}
			String[] action = StringUtils.split(cmd.toString(), ":");
			for (String subAction : action) {
				if (subAction.equals(args[0])) {
					return new StringTextComponent("Actions cannot be recursive.");
				}
			}
			actions.put(args[0], action);
			return new StringTextComponent((writeActions()) ? "Action saved successfully." : "Error saving action. Please try again later.");
		}
		return new StringTextComponent("Invalid argument(s).");
	}

	@CommandParser.Command(help = "Deletes an action. Usage: /5h delAction <action name>", alias = "da")
	public static IFormattableTextComponent delAction(String[] args) {
		if (args != null && args.length == 1) {
			if (actions.remove(args[0]) != null) {
				if (writeActions()) {
					return new StringTextComponent("Action deleted successfully.");
				} else {
					return new StringTextComponent("Error deleting action. Please try again later.");
				}
			} else {
				return new StringTextComponent("No such action exists.");
			}
		}
		return new StringTextComponent("Invalid/missing argument(s).");
	}

	@CommandParser.Command(help = "Lists all actions.", alias = "la")
	public static IFormattableTextComponent listActions(String[] args) {
		StringBuilder actionList = new StringBuilder();
		actionList.append("Actions: ");
		int i = actions.keySet().size();
		for (String s : actions.keySet()) {
			actionList.append(s);
			if (i-- != 1) {
				actionList.append(", ");
			}
		}
		/*
		actions.forEach((k,v)-> actionList.append(k).append(", "));
		actionList.deleteCharAt(actionList.length()-2);*/
		return new StringTextComponent(actionList.toString());
	}

	private static boolean writeActions() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDActions.cfg", false));
			String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUDActions.cfg"), Charset.defaultCharset());
			contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
			writer.write(contents);
			for (HashMap.Entry<String, String[]> entry : actions.entrySet()) {
				writer.write(entry.getKey() + ';' + String.join(":", entry.getValue()) + '\n');
			}
			writer.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
