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

package com.github.varun_dhar.hud5zig.commands;

import com.github.varun_dhar.hud5zig.misc.StartupMessenger;
import com.google.common.io.Resources;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Minecraft;
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
    private static final LocalPlayer player = Minecraft.getInstance().player;

    static {
        readActions();
    }

    private static void readActions() {
        try {
            File cfg = new File("mods/5zigHUDActions.cfg");
            if (!cfg.isFile()) {
                URL defaultConfig = ActionCommands.class.getResource("/com/github/varun_dhar/5zigHUDActions.cfg");
                cfg.createNewFile();
                FileUtils.copyURLToFile(defaultConfig, cfg);
                StartupMessenger.addMessage("The 5zigHUD action configuration was not found. A new one was created.");
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
                            StartupMessenger.addMessage("Actions may not be recursive. Please remove or correct action \"" + action[0] + "\".");
                            break;
                        } else if (i == actionArgs.length - 1) {
                            actions.put(action[0], actionArgs);
                        }
                    }
                }
            }
        } catch (IOException e) {
            StartupMessenger.addMessage("The 5zigHUD action configuration could not be read.");
        }
    }

    @CommandParser.Command(help = "Adds an action. Usage: /5h addAction <action name> <command 1>:<command 2>...", alias = "aa")
    public static MutableComponent addAction(String[] args) {
        if (args != null && args.length >= 2) {
            StringBuilder cmd = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                cmd.append(args[i]);
                if (i != (args.length - 1)) {
                    cmd.append(' ');
                }
            }
            String[] action = StringUtils.split(cmd.toString(), ":");
            for (var subAction : action) {
                if (subAction.equals(args[0])) {
                    return new TextComponent("Actions cannot be recursive.");
                }
            }
            actions.put(args[0], action);

            writeActions("Action saved successfully.", "Error saving action. Please try again later.");
            return null;
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Deletes an action. Usage: /5h delAction <action name>", alias = "da")
    public static MutableComponent delAction(String[] args) {
        if (args != null && args.length == 1) {
            if (actions.remove(args[0]) != null) {
                writeActions("Action deleted successfully.", "Error deleting action. Please try again later.");
                return null;
            } else {
                return new TextComponent("No such action exists.");
            }
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Lists all actions.", alias = "la")
    public static MutableComponent listActions(String[] args) {
        return new TextComponent("Actions: " + StringUtils.join(actions.keySet(), ", "));
    }

    private static void writeActions(String success, String fail) {
        new Thread(() -> {
            synchronized (player) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDActions.cfg", false));
                    String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUDActions.cfg"), Charset.defaultCharset());
                    contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
                    writer.write(contents);
                    for (var entry : actions.entrySet()) {
                        writer.write(entry.getKey() + ';' + String.join(":", entry.getValue()) + '\n');
                    }
                    writer.close();
                    player.sendMessage(new TextComponent(success), Util.NIL_UUID);
                } catch (IOException e) {
                    player.sendMessage(new TextComponent(fail), Util.NIL_UUID);
                }
            }
        }).start();
    }
}
