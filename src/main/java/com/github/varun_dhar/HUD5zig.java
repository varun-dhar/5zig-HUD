package com.github.varun_dhar;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.ConfigParser;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

@Mod(HUD5zig.MODID)

public class HUD5zig//setup stuff
{
	public static final String MODID = "hudmod";
	private static final int NUM_OPS = 12;
	/*settings array 0- HUD x 1- HUD y 2- HUD alignment: vert/hor 3- HUD enabled/disabled
	4- Armor x 5- Armor y 6- Armor alignment: vert/hor 7- Armor enabled/disabled
	8- Death timer/coords x 9- Death timer/coords y 10- Death timer/coords alignment: vert/hor 11- Death timer/coords enabled/disabled
	 */
	public static int[] settings = new int[NUM_OPS];
	public HUD5zig() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	private void clientSetup(FMLClientSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new RenderGuiHandler());
		Arrays.fill(settings,-1);
		try {
			File cfg = new File("mods/5zigHUD.cfg");
			if(!cfg.isFile())
			{
				cfg.createNewFile();
				FileWriter creator = new FileWriter("mods/5zigHUD.cfg");
				creator.write(ConfigFile.contents);
				creator.close();
				System.out.println("Configuration not found. Creating new with default settings");
			}
			else {
				String buf = new Scanner(cfg).useDelimiter("\\Z").next();
				String[] ops = {"HUD-X=","HUD-Y=","HUD-Alignment=","HUD-Enabled=","ArmorX=","ArmorY=","ArmorAlignment=","ArmorEnabled=","DeathTimerX=","DeathTimerY=","DeathTimerAlignment=","DeathTimerEnabled="};
				int i = 0;
				while(i < settings.length) {
					int o = buf.indexOf(ops[i]);
					int n = buf.indexOf('\n',o);
					if(o<0||n<0){
						break;
					}
					settings[i] = Integer.parseInt(buf.substring(o+ops[i].length(),n));
					i++;
				}
				if (i < settings.length) {
					Arrays.fill(settings, -1);
					System.out.println("Configuration invalid. Using default settings");
				}
			}
		}catch (IOException e)
		{
			System.out.println("Could not read config. Using default settings");
		}
		if(Math.abs(settings[11])==1) {//enable death timer if specified in config
			MinecraftForge.EVENT_BUS.register(new DeathHandler());
		}
	}
}
