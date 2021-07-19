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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Coordinate {
	public int[] coords;
	public int dimensionID;

	public Coordinate(int[] coords, int dimensionID) {
		this.coords = coords;
		this.dimensionID = dimensionID;
	}
}

public class CoordinateCommands {
	public static HashMap<String, HashMap<String, Coordinate>> savedCoords = new HashMap<>();
	public static String globalCoords = "__5ZIG_HUD_GLOBAL_COORDINATES";
	private static final Minecraft mc = Minecraft.getInstance();

	public CoordinateCommands() { readCoords(); }

	public static int getDimension(RegistryKey<World> dim) {
		if (dim.compareTo(World.OVERWORLD) == 0) {
			return 0;
		} else if (dim.compareTo(World.THE_NETHER) == 0) {
			return 1;
		} else if (dim.compareTo(World.THE_END) == 0) {
			return 2;
		}
		return -1;
	}

	public static String getWorldName() {
		if (mc.isSingleplayer()) {
			return StringUtils.replace(ServerLifecycleHooks.getCurrentServer().anvilConverterForAnvilFile.getSaveName(),
					" ", "_");
		} else {
			ServerData data = mc.getCurrentServerData();
			if (data == null) {
				return null;
			}
			return data.serverIP;
		}
	}

	private void readCoords() {
		try {
			File cfg = new File("mods/5zigHUDCoords.cfg");
			if (!cfg.isFile()) {
				URL defaultConfig = getClass().getResource("/com/github/doggo4242/5zigHUDCoords.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig, cfg);
				StartupMessenger.messages.add("The 5zigHUD coordinate configuration was not found. A new one was created.");
			} else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDCoords.cfg"));
				String line;
				Pattern oldFormat = Pattern.compile("X: (-?\\d+) Y: (-?\\d+) Z: (-?\\d+)");
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
							StartupMessenger.messages.add("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
							continue;
						}
						if (!savedCoords.containsKey(globalCoords)) {
							savedCoords.put(globalCoords, new HashMap<>());
						}
						savedCoords.get(globalCoords).put(name, new Coordinate(coords, -1));
						update = true;
					} else {
						String[] coords = StringUtils.split(tmp[1], " ");
						if (coords.length != 5) {
							StartupMessenger.messages.add("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
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
							StartupMessenger.messages.add("The coordinates file contains an invalid location, \"" + name + "\". Please correct or remove it.");
							continue;
						}
						if (!savedCoords.containsKey(coords[4])) {
							savedCoords.put(coords[4], new HashMap<>());
						}
						savedCoords.get(coords[4]).put(name, new Coordinate(convCoords, dimensionID));
					}
				}
				if (update) {
					writeCoords();
				}
				reader.close();
			}
		} catch (IOException e) {
			StartupMessenger.messages.add("The 5zigHUD coordinate configuration could not be read.");
		}
	}

	@CommandParser.Command(help = "Sets location dimension. 0: Overworld 1: Nether 2: End.  Usage: /5h setLocationDimension name <0|1|2>", alias = "sld")
	public static IFormattableTextComponent setLocationDimension(String[] args) {
		if (args != null && args.length == 2) {
			try {
				int dim = Integer.parseInt(args[1]);
				if (dim >= 0 && dim <= 2) {
					Coordinate coordinate;
					String world = getWorldName();
					if (savedCoords.containsKey(world) && savedCoords.get(world).containsKey(args[0])) {
						coordinate = savedCoords.get(world).get(args[0]);
					} else if (savedCoords.containsKey(globalCoords) && savedCoords.get(globalCoords).containsKey(args[0])) {
						coordinate = savedCoords.get(globalCoords).remove(args[0]);
					} else {
						return new StringTextComponent("Invalid argument.");
					}
					coordinate.dimensionID = dim;
					savedCoords.get(world).put(args[0], coordinate);
					if(writeCoords()) {
						return new StringTextComponent("Successfully updated location \"" + args[0] + "\".");
					}else{
						return new StringTextComponent("Error updating location. Please try again later.");
					}
				}
			} catch (NumberFormatException e) {
				return new StringTextComponent("Invalid argument.");
			}
		}
		return new StringTextComponent("Invalid/missing argument.");
	}

	@CommandParser.Command(help = "Sets worldless location's world to current world. Usage: /5h setLocationWorld <name>", alias = "slw")
	public static IFormattableTextComponent setLocationWorld(String[] args) {
		if (args != null && args.length == 1) {
			if (savedCoords.containsKey(globalCoords) && savedCoords.get(globalCoords).containsKey(args[0])) {
				Coordinate coordinate = savedCoords.get(globalCoords).get(args[0]);
				savedCoords.get(globalCoords).remove(args[0]);
				String world = getWorldName();
				if (!savedCoords.containsKey(world)) {
					savedCoords.put(world, new HashMap<>());
				}
				savedCoords.get(world).put(args[0], coordinate);
				if(writeCoords()) {
					return new StringTextComponent("Successfully updated location \"" + args[0] + "\".");
				}else{
					return new StringTextComponent("Error updating location. Please try again later.");
				}
			}
		}
		return new StringTextComponent("Invalid/missing argument.");
	}

	@CommandParser.Command(help = "Saves specified coordinates. Usage: /5h saveCoords <name> <X> <Y> <Z>", alias = "sc")
	public static IFormattableTextComponent saveCoords(String[] args) {
		if (args != null && args.length == 4) {
			if (mc.world == null) {
				return null;
			}
			int[] coords = new int[3];
			try {
				for (int i = 0; i < coords.length; i++) {
					coords[i] = Integer.parseInt(args[i + 1]);
				}
			} catch (NumberFormatException e) {
				return new StringTextComponent("Invalid argument(s)");
			}
			return new StringTextComponent((saveCoordsI(args[0], coords, mc.world.getDimensionKey())) ? "Coordinates saved successfully." : "Error saving coordinates. Please try again later.");
		}
		return new StringTextComponent("Invalid argument(s)");
	}

	public static boolean saveCoordsI(String name, int[] coords, RegistryKey<World> dim) {
		if (coords.length == 3) {
			String world = getWorldName();
			if (!savedCoords.containsKey(world)) {
				savedCoords.put(world, new HashMap<>());
			}
			if (savedCoords.containsKey(globalCoords)) {
				savedCoords.get(globalCoords).remove(name);
			}
			savedCoords.get(world).put(name, new Coordinate(coords, getDimension(dim)));
			return writeCoords();
		}
		return false;
	}

	@CommandParser.Command(help = "Saves current coordinates. Usage: /5h saveCurrentCoords <name>", alias = "scc")
	public static IFormattableTextComponent saveCurrentCoords(String[] args) {
		if (args != null && args.length == 1) {
			ClientPlayerEntity player = mc.player;
			if (player != null && mc.world != null) {
				boolean success = saveCoordsI(args[0],
						new int[]{(int) player.lastTickPosX, (int) player.lastTickPosY, (int) player.lastTickPosZ},
						mc.world.getDimensionKey());
				return new StringTextComponent((success) ? "Coordinates saved successfully." : "Error saving coordinates. Please try again later.");
			}
			return new StringTextComponent("Could not get coordinates.");
		}
		return new StringTextComponent("Invalid argument(s).");
	}

	@CommandParser.Command(help = "Deletes the coordinates of a specified location. Usage: /5h delCoords <name>", alias = "dc")
	public static IFormattableTextComponent delCoords(String[] args) {
		if (args != null && args.length == 1) {
			String world = getWorldName();
			if (((savedCoords.containsKey(world) && savedCoords.get(world).remove(args[0]) != null)
					|| (savedCoords.containsKey(globalCoords) && savedCoords.get(globalCoords).remove(args[0]) != null))
					&& writeCoords()) {
				return new StringTextComponent("Coordinates deleted successfully.");
			} else {
				return new StringTextComponent("Error deleting coordinates. Check the location name and try again.");
			}
		}
		return new StringTextComponent("Invalid argument(s).");
	}

	public static Coordinate getCoordsI(String name) {
		String world = getWorldName();
		if (savedCoords.containsKey(world) && savedCoords.get(world).containsKey(name)) {
			return savedCoords.get(world).get(name);
		} else if (savedCoords.containsKey(globalCoords) && savedCoords.get(globalCoords).containsKey(name)) {
			return savedCoords.get(globalCoords).get(name);
		} else {
			return null;
		}
	}	
	@CommandParser.Command(help = "Gets the coordinates of a specified location. Usage: /5h getCoords <location name>", alias = "gc")
	public static IFormattableTextComponent getCoords(String[] args) {
		if (args != null && args.length == 1) {
			Coordinate coordinate = getCoordsI(args[0]);
			if(coordinate == null) {
				return new StringTextComponent("No such location exists.");
			}
			int[] coords = coordinate.coords;
			return new StringTextComponent(String.format("X: %d Y: %d Z: %d", coords[0], coords[1], coords[2]));
		}
		return new StringTextComponent("Invalid argument(s).");
	}

	@CommandParser.Command(help = "Lists the coordinate locations of the current world and global locations.", alias = "lc")
	public static IFormattableTextComponent listCoords(String[] args) {
		StringBuilder coordsList = new StringBuilder();
		coordsList.append("Locations:\n");
		String[] worlds = {getWorldName(), globalCoords};
		for (String world : worlds) {
			if (!savedCoords.containsKey(world)) {
				continue;
			}
			int j = savedCoords.get(world).size();
			for (HashMap.Entry<String, Coordinate> entry : savedCoords.get(world).entrySet()) {
				int[] coords = entry.getValue().coords;
				coordsList.append(String.format("%s X: %d Y: %d Z: %d", entry.getKey(), coords[0], coords[1], coords[2]));
				if (j-- != 1) {
					coordsList.append('\n');
				}
			}
		}
		/*savedCoords.forEach((k,v)-> coordsList.append(k).append(" ").append(v).append("\n"));
		coordsList.deleteCharAt(coordsList.length()-1);*/
		return new StringTextComponent(coordsList.toString().trim());
	}

	@CommandParser.Command(help="Lists coordinate locations of all worlds",alias="lac")
	public static IFormattableTextComponent listAllCoords(String[] args) {
		StringBuilder coordsList = new StringBuilder();
		coordsList.append("Locations:\n");
		int i = savedCoords.size();
		for (HashMap.Entry<String, HashMap<String, Coordinate>> mapEntry : savedCoords.entrySet()) {
			String key = mapEntry.getKey();
			if (key.equals(globalCoords)) {
				coordsList.append("Global:\n");
			} else {
				coordsList.append(StringUtils.replace(key, "_", " ")).append(":\n");
			}
			int j = mapEntry.getValue().size();
			for (HashMap.Entry<String, Coordinate> entry : mapEntry.getValue().entrySet()) {
				int[] coords = entry.getValue().coords;
				coordsList.append(String.format("%s X: %d Y: %d Z: %d", entry.getKey(), coords[0], coords[1], coords[2]));
				if (j-- != 1) {
					coordsList.append('\n');
				}
			}
			if (i-- != 1) {
				coordsList.append('\n');
			}
		}
		return new StringTextComponent(coordsList.toString());
	}

	private static boolean writeCoords() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUDCoords.cfg", false));
			String contents = Resources.toString(Resources.getResource("/com/github/doggo4242/5zigHUDCoords.cfg"), Charset.defaultCharset());
			contents = contents.substring(0, contents.indexOf('\n', contents.lastIndexOf('#')) + 1);
			writer.write(contents);
			for (HashMap.Entry<String, HashMap<String, Coordinate>> entryMap : savedCoords.entrySet()) {
				for (HashMap.Entry<String, Coordinate> entry : entryMap.getValue().entrySet()) {
					Coordinate coordinate = entry.getValue();
					int[] coords = coordinate.coords;
					writer.write(String.format("%s;%d %d %d %d %s\n", entry.getKey(), coords[0],
							coords[1], coords[2], coordinate.dimensionID, entryMap.getKey()));
				}
			}
			writer.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
