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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateCommands {
    private static class CoordinateMap extends HashMap<String, int[]> {
    }

    private static final HashMap<String, CoordinateMap[]> savedCoords = new HashMap<>();
    private static final String globalCoords = "__5ZIG_HUD_GLOBAL_COORDINATES";
    private static final int unknownDim = 3;
    private static final Minecraft mc = Minecraft.getInstance();
    private static final LocalPlayer player = mc.player;

    static {
        readCoords();
    }

    public static int getDimension() {
        if (mc.level == null) {
            return -1;
        }
        ResourceKey<Level> dim = mc.level.dimension();
        if (dim.compareTo(Level.OVERWORLD) == 0) {
            return 0;
        } else if (dim.compareTo(Level.NETHER) == 0) {
            return 1;
        } else if (dim.compareTo(Level.END) == 0) {
            return 2;
        }
        return unknownDim;
    }

    public static String getWorldName() {
        if (mc.hasSingleplayerServer()) {
            return ServerLifecycleHooks.getCurrentServer().storageSource.getLevelId();
        } else {
            ServerData data = mc.getCurrentServer();
            if (data == null) {
                return null;
            }
            return data.ip;
        }
    }

    private static void readCoords() {
        try {
            File cfg = new File("mods/5zigHUDCoords.cfg");
            if (!cfg.isFile()) {
                URL defaultConfig = CoordinateCommands.class.getResource("/com/github/varun_dhar/5zigHUDCoords.cfg");
                cfg.createNewFile();
                FileUtils.copyURLToFile(defaultConfig, cfg);
                StartupMessenger.addMessage("The 5zigHUD coordinate configuration was not found. A new one was created.");
            } else {
                var reader = new BufferedReader(new FileReader("mods/5zigHUDCoords.cfg"));
                ArrayList<String> invalidEntries = new ArrayList<>();
                String line;
                final Pattern oldFormat = Pattern.compile("X: (-?\\d+) Y: (-?\\d+) Z: (-?\\d+)");
                boolean update = false;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.charAt(0) == '#') {
                        continue;
                    }
                    String[] tmp = StringUtils.split(line, ";");
                    String name = tmp[0];
                    Matcher matcher = oldFormat.matcher(tmp[1]);
                    if (matcher.matches()) {
                        int[] coords = new int[3];
                        try {
                            for (int i = 0; i < coords.length; i++) {
                                coords[i] = Integer.parseInt(matcher.group(i + 1));
                            }
                        } catch (NumberFormatException e) {
                            invalidEntries.add(line);
                            StartupMessenger.addMessage("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
                            continue;
                        }
                        CoordinateMap[] map;
                        if (!savedCoords.containsKey(globalCoords)) {
                            map = new CoordinateMap[1];
                            map[0] = new CoordinateMap();
                            savedCoords.put(globalCoords, map);
                        } else {
                            map = savedCoords.get(globalCoords);
                        }
                        map[0].put(name, coords);
                        update = true;
                    } else {
                        String[] coords = StringUtils.split(tmp[1], " ", 5);
                        if (coords.length != 5) {
                            invalidEntries.add(line);
                            StartupMessenger.addMessage("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
                            continue;
                        }
                        int[] convCoords = new int[3];
                        int dimensionID;
                        try {
                            for (int i = 0; i < convCoords.length; i++) {
                                convCoords[i] = Integer.parseInt(coords[i]);
                            }
                            dimensionID = Integer.parseInt(coords[3]);
                        } catch (NumberFormatException e) {
                            invalidEntries.add(line);
                            StartupMessenger.addMessage("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
                            continue;
                        }
                        CoordinateMap[] map;
                        if (!savedCoords.containsKey(coords[4])) {
                            map = new CoordinateMap[4];
                            map[dimensionID] = new CoordinateMap();
                            savedCoords.put(coords[4], map);
                        } else if ((map = savedCoords.get(coords[4]))[dimensionID] == null) {
                            map[dimensionID] = new CoordinateMap();
                        } else {
                            map = savedCoords.get(coords[4]);
                        }
                        map[dimensionID].put(name, convCoords);
                    }
                }
                reader.close();
                if (update) {
                    writeCoords(null, "Error writing to the 5zigHUD coordinate configuration.",true);
                    if (invalidEntries.size() > 0) {
                        try {
                            BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDCoords.cfg"));
                            for (String s : invalidEntries) {
                                writer.write(s + '\n');
                            }
                            writer.close();
                        } catch (IOException e) {
                            StartupMessenger.addMessage("Error writing to the 5zigHUD coordinate configuration.");
                        }
                    }
                }
            }
        } catch (IOException e) {
            StartupMessenger.addMessage("The 5zigHUD coordinate configuration could not be read.");
        }
    }

    @CommandParser.Command(help = "Sets location dimension and sets world to current world. Specify the location to set, " +
            "the desired dimension, and optionally, the current dimension of the location to change. 0: Overworld 1: Nether " +
            "2: End. 3: Other.  Usage: /5h setLocationDimension name <0|1|2|3> [<0|1|2|3>]", alias = "sld")
    public static MutableComponent setLocationDimension(String[] args) {
        if (args != null && args.length >= 2 && args.length <= 3) {
            try {
                int currentDim;
                if (args.length == 3) {
                    currentDim = Integer.parseInt(args[2]);
                    if (!(currentDim >= 0 && currentDim <= unknownDim)) {
                        return new TextComponent("Invalid argument.");
                    }
                } else {
                    currentDim = getDimension();
                }
                int dim = Integer.parseInt(args[1]);
                if (!(dim >= 0 && dim <= unknownDim)) {
                    return new TextComponent("Invalid argument.");
                }
                int[] coords;
                String world = getWorldName();
                CoordinateMap[] tmpWMap = savedCoords.get(world);
                CoordinateMap tmpMap;
                if (savedCoords.containsKey(globalCoords) && (tmpMap = savedCoords.get(globalCoords)[0]).containsKey(args[0])) {
                    coords = tmpMap.get(args[0]);
                } else if (savedCoords.containsKey(world) && (tmpMap = tmpWMap[currentDim]) != null
                        && tmpMap.containsKey(args[0])) {
                    coords = tmpMap.get(args[0]);
                } else {
                    return new TextComponent("Nonexistent location or wrong dimension.");
                }
                if (tmpWMap[dim] == null) {
                    tmpWMap[dim] = new CoordinateMap();
                }
                if (tmpWMap[dim].containsKey(args[0])) {
                    return new TextComponent("This location already exists in the desired dimension.");
                }
                tmpWMap[dim].put(args[0], coords);
                tmpMap.remove(args[0]);
                writeCoords("Successfully updated location \"" + args[0] + "\".", "Error updating location. Please try again later.",false);
                return null;
            } catch (NumberFormatException e) {
                return new TextComponent("Invalid argument.");
            }
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Saves specified coordinates. Usage: /5h saveCoords <name> <X> <Y> <Z>", alias = "sc")
    public static MutableComponent saveCoords(String[] args) {
        if (args != null && args.length == 4) {
            int[] coords = new int[3];
            try {
                for (int i = 0; i < coords.length; i++) {
                    coords[i] = Integer.parseInt(args[i + 1]);
                }
            } catch (NumberFormatException e) {
                return new TextComponent("Invalid argument(s)");
            }
            saveCoordsI(args[0], coords, "Coordinates saved successfully.", "Error saving coordinates. Please try again later.");
            return null;
        }
        return new TextComponent("Invalid/missing argument(s)");
    }

    public static void saveCoordsI(String name, int[] coords, String success, String fail) {
        if (coords.length == 3) {
            String world = getWorldName();
            int dimensionID = getDimension();
            CoordinateMap[] map;
            if (!savedCoords.containsKey(world)) {
                map = new CoordinateMap[4];
                map[dimensionID] = new CoordinateMap();
                savedCoords.put(world, map);
            } else if ((map = savedCoords.get(world))[dimensionID] == null) {
                map[dimensionID] = new CoordinateMap();
            }
            if (savedCoords.containsKey(globalCoords)) {
                savedCoords.get(globalCoords)[0].remove(name);
            }
            map[dimensionID].put(name, coords);
            writeCoords(success, fail,false);
        }
    }

    @CommandParser.Command(help = "Saves current coordinates. Usage: /5h saveCurrentCoords <name>", alias = "scc")
    public static MutableComponent saveCurrentCoords(String[] args) {
        if (args != null && args.length == 1) {
            LocalPlayer player = mc.player;
            if (player != null) {
                saveCoordsI(args[0],
                        new int[]{(int) player.getX(), (int) player.getY(), (int) player.getZ()},
                        "Coordinates saved successfully.", "Error saving coordinates. Please try again later.");
                return null;
            }
            return new TextComponent("Could not get coordinates.");
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Deletes the coordinates of a specified location. Optionally provide a dimension." +
            " Usage: /5h delCoords <name> [<0|1|2|3>]", alias = "dc")
    public static MutableComponent delCoords(String[] args) {
        if (args != null && (args.length == 1 || args.length == 2)) {
            int dim;
            if (args.length == 2) {
                try {
                    dim = Integer.parseInt(args[1]);
                    if (!(dim >= 0 && dim <= unknownDim)) {
                        return new TextComponent("Invalid argument \"dimension\".");
                    }
                } catch (NumberFormatException e) {
                    return new TextComponent("Invalid argument \"dimension\".");
                }
            } else {
                dim = getDimension();
            }
            String world = getWorldName();
            CoordinateMap tmpMap;
            if (((savedCoords.containsKey(world) && (tmpMap = savedCoords.get(world)[dim]) != null && tmpMap.remove(args[0]) != null)
                    || (savedCoords.containsKey(globalCoords) && savedCoords.get(globalCoords)[0].remove(args[0]) != null))) {
                writeCoords("Coordinates deleted successfully.", "Error deleting coordinates. Check the location name and try again.",false);
                return null;
            } else {
                return new TextComponent("Error deleting coordinates. Check the location name and try again.");
            }
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    public static int[] getCoordsI(String name, int dim) {
        String world = getWorldName();
        CoordinateMap map;
        if (savedCoords.containsKey(world) && (map = savedCoords.get(world)[dim]) != null && map.containsKey(name)) {
            return map.get(name);
        } else if (savedCoords.containsKey(globalCoords) && (map = savedCoords.get(globalCoords)[0]) != null && map.containsKey(name)) {
            return map.get(name);
        } else {
            return null;
        }
    }

    public static int[] getCoordsI(String name) {
        return getCoordsI(name, getDimension());
    }

    @CommandParser.Command(help = "Gets the coordinates of a specified location. Optionally provide a dimension. " +
            "Usage: /5h getCoords <location name> [<0|1|2|3>]", alias = "gc")
    public static MutableComponent getCoords(String[] args) {
        if (args != null && args.length >= 1 && args.length <= 2) {
            int dim;
            if (args.length == 2) {
                try {
                    dim = Integer.parseInt(args[1]);
                    if (!(dim >= 0 && dim <= unknownDim)) {
                        return new TextComponent("Invalid argument.");
                    }
                } catch (NumberFormatException e) {
                    return new TextComponent("Invalid argument.");
                }
            } else {
                dim = getDimension();
            }
            int[] coords = getCoordsI(args[0], dim);
            if (coords == null) {
                return new TextComponent("No such location exists.");
            }
            return new TextComponent(String.format("X: %d Y: %d Z: %d", coords[0], coords[1], coords[2]));
        }
        return new TextComponent("Invalid/missing argument(s).");
    }

    @CommandParser.Command(help = "Lists the coordinate locations of the current world and global locations.", alias = "lc")
    public static MutableComponent listCoords(String[] args) {
        StringBuilder coordsList = new StringBuilder();
        coordsList.append("Locations:\n");
        final String[] worlds = {getWorldName(), globalCoords};
        final String[] dimensionNames = {"Overworld:\n", "Nether:\n", "End:\n", "Unknown dimension:\n"};
        for (String world : worlds) {
            if (!savedCoords.containsKey(world)) {
                continue;
            }
            if (world.equals(globalCoords)) {
                coordsList.append("  Global:\n");
            }
            CoordinateMap[] maps = savedCoords.get(world);
            for (int i = 0; i < maps.length; i++) {
                if (maps[i] == null) {
                    continue;
                }
                if (!world.equals(globalCoords)) {
                    coordsList.append("  ").append(dimensionNames[i]);
                }
                for (var entry : maps[i].entrySet()) {
                    int[] coords = entry.getValue();
                    coordsList.append(String.format("    %s X: %d Y: %d Z: %d\n", entry.getKey(), coords[0], coords[1], coords[2]));
                }
            }
        }
        coordsList.deleteCharAt(coordsList.length() - 1);
        return new TextComponent(coordsList.toString());
    }

    @CommandParser.Command(help = "Lists coordinate locations of all worlds", alias = "lac")
    public static MutableComponent listAllCoords(String[] args) {
        var coordsList = new StringBuilder();
        coordsList.append("Locations:\n");
        final String[] dimensionNames = {"Overworld:\n", "Nether:\n", "End:\n", "Unknown dimension:\n"};
        for (var map : savedCoords.entrySet()) {
            String key = map.getKey();
            if (key.equals(globalCoords)) {
                coordsList.append("  Global:\n    Unknown dimension:\n");
            } else {
                coordsList.append("  ").append(key).append(":\n");
            }
            CoordinateMap[] mapEntries = map.getValue();
            for (int j = 0; j < mapEntries.length; j++) {
                if (mapEntries[j] == null) {
                    continue;
                }
                if (!key.equals(globalCoords)) {
                    coordsList.append("    ").append(dimensionNames[j]);
                }
                for (var entry : mapEntries[j].entrySet()) {
                    int[] coords = entry.getValue();
                    coordsList.append(String.format("      %s X: %d Y: %d Z: %d\n", entry.getKey(), coords[0], coords[1], coords[2]));
                }
            }
        }
        coordsList.deleteCharAt(coordsList.length() - 1);
        return new TextComponent(coordsList.toString());
    }

    @CommandParser.Command(help = "Renames a location. Optionally takes the dimension of the location. " +
            "Usage: /5h renameCoords <location> <new name> [<0|1|2|3>]", alias = "rc")
    public static MutableComponent renameCoords(String[] args) {
        if (args != null && args.length >= 2 && args.length <= 3) {
            int dimension;
            if (args.length == 3) {
                try {
                    dimension = Integer.parseInt(args[2]);
                    if (!(dimension >= 0 && dimension <= unknownDim)) {
                        return new TextComponent("Invalid argument: \"dimension\"");
                    }
                } catch (NumberFormatException e) {
                    return new TextComponent("Invalid argument: \"dimension\"");
                }
            } else {
                dimension = getDimension();
            }
            String world = getWorldName();
            CoordinateMap map;
            if (savedCoords.containsKey(world) && (map = savedCoords.get(world)[dimension]) != null && map.containsKey(args[0])) {
                map.put(args[1], map.remove(args[0]));
            } else if (savedCoords.containsKey(globalCoords) && (map = savedCoords.get(globalCoords)[0]).containsKey(args[0])) {
                map.put(args[1], map.remove(args[0]));
            } else {
                return new TextComponent("Invalid/missing argument(s)");
            }
            writeCoords("Error saving coordinates. Please try again later.", String.format("Location \"%s\" renamed to \"%s\"", args[0], args[1]),false);
            return null;
        }
        return new TextComponent("Invalid/missing argument(s)");
    }

    private static void writeCoords(String success, String fail,boolean block) {
        Consumer<Void> write = (unused)->{
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDCoords.cfg", false));
                String contents = Resources.toString(Resources.getResource("/com/github/varun_dhar/5zigHUDCoords.cfg"), Charset.defaultCharset());
                contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
                writer.write(contents);
                for (var map : savedCoords.entrySet()) {
                    CoordinateMap[] mapEntries = map.getValue();
                    for (int i = 0; i < mapEntries.length; i++) {
                        if (mapEntries[i] == null) {
                            continue;
                        }
                        for (var entry : mapEntries[i].entrySet()) {
                            int[] coords = entry.getValue();
                            writer.write(String.format("%s;%d %d %d %d %s\n", entry.getKey(), coords[0],
                                    coords[1], coords[2], i, map.getKey()));
                        }
                    }
                }
                writer.close();
                if (player != null && success != null) {
                    player.sendMessage(new TextComponent(success), Util.NIL_UUID);
                }
            } catch (IOException e) {
                if (player != null && fail != null) {
                    player.sendMessage(new TextComponent(fail), Util.NIL_UUID);
                } else if (fail != null) {
                    StartupMessenger.addMessage(fail);
                }
            }
        };
        if(block){
            write.accept(null);
        }else{
            new Thread(() -> {
                synchronized (mc) {
                    write.accept(null);
                }
            }).start();
        }
    }
}
