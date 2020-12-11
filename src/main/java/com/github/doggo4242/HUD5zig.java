package com.github.doggo4242;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.system.CallbackI;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

@Mod(HUD5zig.MODID)

public class HUD5zig//setup stuff
{
	public static final String MODID = "hudmod";
	public static final int NUM_OPS = 13;
	public static final int version = 7;
	public static final String[] confOps = {"HUD-X=","HUD-Y=","HUD-Alignment=","HUD-Enabled=","ArmorX=","ArmorY=","ArmorAlignment=","ArmorEnabled=","DeathTimerX=","DeathTimerY=","DeathTimerAlignment=","DeathTimerEnabled=","UpdaterEnabled="};
	/*settings array 0- HUD x 1- HUD y 2- HUD alignment: vert/hor 3- HUD enabled/disabled
	4- Armor x 5- Armor y 6- Armor alignment: vert/hor 7- Armor enabled/disabled
	8- Death timer/coords x 9- Death timer/coords y 10- Death timer/coords alignment: vert/hor 11- Death timer/coords enabled/disabled
	12- Update message enabled/disabled
	 */
	public static int[] settings = new int[NUM_OPS];
	public HUD5zig() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	private void clientSetup(FMLClientSetupEvent event)
	{
		//MinecraftForge.EVENT_BUS.register(new RenderGuiHandler());
		MinecraftForge.EVENT_BUS.register(new HUD());
		MinecraftForge.EVENT_BUS.register(new CommandParser());
		Arrays.fill(settings,-1);
		readSettings();
		readMacros();
		readCoords();
		if(Math.abs(settings[11])==1) {//enable death timer if specified in config
			MinecraftForge.EVENT_BUS.register(new DeathHandler());
		}
		if(Math.abs(settings[12]) == 1)
		{
			MinecraftForge.EVENT_BUS.register(new UpdateChecker());
		}
	}
	public void readSettings(){
		try {
			File cfg = new File("mods/5zigHUD.cfg");
			if(!cfg.isFile())
			{
				cfg.createNewFile();
				FileWriter creator = new FileWriter("mods/5zigHUD.cfg");
				creator.write(ConfigFile.configContents);
				creator.close();
				System.out.println("Configuration not found. Creating new with default settings");
			}
			else {
				String buf = new Scanner(cfg).useDelimiter("\\Z").next();
				int i = 0;
				while(i < NUM_OPS) {
					int o = buf.indexOf(confOps[i]);
					int n = buf.indexOf('\n',o);
					n = (n<0)?buf.length():n;
					if(o<0){
						break;
					}
					settings[i] = Integer.parseInt(buf.substring(o+confOps[i].length(),n));
					++i;
				}
				if (i < NUM_OPS) {
					Arrays.fill(settings, -1);
					System.out.println("Configuration invalid. Using default settings");
					FileWriter writer = new FileWriter("mods/5zigHUD.cfg",true);
					writer.write("Configuration invalid. " + i);
					writer.close();
				}
			}
		}catch (IOException e) {
			System.out.println("Could not read config. Using default settings");
		}
	}
	public void readMacros(){
		try {
			File cfg = new File("mods/5zigHUDMacros.cfg");
			if(!cfg.isFile())
			{
				cfg.createNewFile();
				FileWriter creator = new FileWriter("mods/5zigHUDMacros.cfg");
				creator.write(ConfigFile.macroContents);
				creator.close();
				System.out.println("Configuration not found. Creating new configuration.");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader("mods/5zigHUDMacros.cfg"));
				String line;
				while((line = reader.readLine()) != null) {
					if(line.charAt(0) == '#')
					{
						continue;
					}
					String[] macros = line.split(";");
					CommandParser.macros.put(macros[0],macros[1]);
				}
			}
		}catch (IOException e)
		{
			System.out.println("Could not read macro config.");
		}
	}
	public void readCoords(){
		try {
			File cfg = new File("mods/5zigHUDCoords.cfg");
			if(!cfg.isFile())
			{
				cfg.createNewFile();
				FileWriter creator = new FileWriter("mods/5zigHUDCoords.cfg");
				creator.write(ConfigFile.coordsContents);
				creator.close();
				System.out.println("Coordinate configuration not found. Creating new configuration.");
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
					CommandParser.saveCoords.put(macros[0],macros[1]);
				}
			}
		}catch (IOException e)
		{
			System.out.println("Could not read config. Using default settings");
		}

	}
}
