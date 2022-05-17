/*
   Copyright 2022 doggo4242 Development

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

import com.github.doggo4242.hud5zig.misc.StartupMessenger;
import com.google.common.io.Resources;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.MutableComponent;
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

public class MacroCommands {
    public static HashMap<String, String> macros = new HashMap<>();
    private final static LocalPlayer player = Minecraft.getInstance().player;

    static {
        readMacros();
    }

    private static void readMacros() {
        try {
            File cfg = new File("mods/5zigHUDMacros.cfg");
            if (!cfg.isFile()) {
                URL defaultConfig = MacroCommands.class.getResource("/com/github/doggo4242/5zigHUDMacros.cfg");
                cfg.createNewFile();
                FileUtils.copyURLToFile(defaultConfig, cfg);
                StartupMessenger.addMessage("The 5zigHUD macro configuration was not found. A new one was created.");
            } else {
                BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDMacros.cfg"));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.charAt(0) == '#') {
                        continue;
                    }
                    String[] macro = StringUtils.split(line, ";");
                    macros.put(macro[0], macro[1]);
                }
            }
        } catch (IOException e) {
            StartupMessenger.addMessage("The 5zigHUD macro configuration could not be read.");
        }
    }

    @CommandParser.Command(help = "Adds a macro. Usage: /5h addMacro <macro name> <macro value>", alias = "am")
    public static MutableComponent addMacro(String[] args) {
        if (args != null && args.length >= 2) {
            StringBuilder cmd = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                cmd.append(args[i]).append(" ");
            }
            macros.put(args[0], cmd.toString());
            writeMacros("Macro saved successfully." ,"Error saving macro. Please try again later.");
            return null;
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Deletes a macro. Usage: /5h delMacro <macro name>", alias = "dm")
    public static MutableComponent delMacro(String[] args) {
        if (args != null && args.length == 1) {
            if (macros.remove(args[0]) != null) {
                writeMacros("Macro deleted successfully.","Error deleting macro. Please try again later.");
                return null;
            } else {
                return new TextComponent("Macro " + args[0] + " does not exist.");
            }
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Lists all macros.", alias = "lm")
    public static MutableComponent listMacros(String[] args) {
        return new TextComponent("Macros: " + StringUtils.join(macros.keySet(), ", "));
    }

    private static void writeMacros(String success,String fail) {
        new Thread(() -> {
            synchronized (player) {
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDMacros.cfg", false));
                    String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUDMacros.cfg"), Charset.defaultCharset());
                    contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
                    writer.write(contents);
                    for (HashMap.Entry<String, String> entry : macros.entrySet()) {
                        writer.write(entry.getKey() + ';' + entry.getValue() + '\n');
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
