package com.github.varun_dhar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.io.FileUtils;

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
	public static final int NUM_OPS = 15;
	public static final int version = 8;
	public static final List<String> confOps = Arrays.asList("HUD-X", "HUD-Y", "HUD-Alignment", "HUD-Enabled", "ArmorX", "ArmorY", "ArmorAlignment",
			"ArmorEnabled", "DeathTimerX", "DeathTimerY", "DeathTimerAlignment", "DeathTimerEnabled", "UpdaterEnabled", "NavX", "NavY");
	/*settings array 0- HUD x 1- HUD y 2- HUD alignment: vert/hor 3- HUD enabled/disabled
	4- Armor x 5- Armor y 6- Armor alignment: vert/hor 7- Armor enabled/disabled
	8- Death timer/coords x 9- Death timer/coords y 10- Death timer/coords alignment: vert/hor 11- Death timer/coords enabled/disabled
	12- Update message enabled/disabled 13- Navigation compass x 14- Navigation compass y
	 */
	public static int[] settings = new int[NUM_OPS];
	public static HashMap<String, Integer> set = new HashMap<>();
	public HUD5zig() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	private void clientSetup(FMLClientSetupEvent event)
	{
		//MinecraftForge.EVENT_BUS.register(new RenderGuiHandler());
		MinecraftForge.EVENT_BUS.register(new HUD());
		MinecraftForge.EVENT_BUS.register(new CommandParser());
		readSettings();
		if(Math.abs(set.get("DeathTimerEnabled"))==1) {//enable death timer if specified in config
			MinecraftForge.EVENT_BUS.register(new DeathHandler());
		}
		if(Math.abs(set.get("UpdaterEnabled")) == 1)//enable update checker if specified in config
		{
			MinecraftForge.EVENT_BUS.register(new UpdateChecker());
		}
	}
	public void readSettings(){//read settings into array
		try {
			File cfg = new File("mods/5zigHUD.cfg");
			if(!cfg.isFile())
			{
				URL defaultConfig = getClass().getResource("/com/github/varun_dhar/5zigHUD.cfg");
				cfg.createNewFile();
				FileUtils.copyURLToFile(defaultConfig,cfg);
				System.out.println("Configuration not found. Creating new with default settings");
			}
			else {
				BufferedReader reader = new BufferedReader(new FileReader(cfg));
				for(String s = reader.readLine();s != null;s = reader.readLine()){
					s = s.trim();
					if(s.charAt(0) == '#')
						continue;
					String[] fields = s.split("=");
					if(fields.length == 2){
						fields[0] = fields[0].trim();
						fields[1] = fields[1].trim();
						if(set.containsKey(fields[0]) || !confOps.contains(fields[0])){
							continue;
						}
						try{
							set.put(fields[0],Integer.parseInt(fields[1]));
						}catch(NumberFormatException ignored){}
					}
				}
				reader.close();
				if (set.size() < NUM_OPS) {
					for(String op : confOps){
						set.put(op,-1);
					}
					System.out.println("Configuration invalid. Using default settings");
					BufferedWriter writer = new BufferedWriter(new FileWriter("mods/5zigHUD.cfg",true));
					writer.write("Configuration invalid. " + (NUM_OPS-set.size()) + '/'+ NUM_OPS+" options missing.");
					writer.close();
				}
			}
		}catch (IOException e) {
			System.out.println("Could not read config. Using default settings");
			for(String op : confOps){
				set.put(op,-1);
			}
		}
	}
}
