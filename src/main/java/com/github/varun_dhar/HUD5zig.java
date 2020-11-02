package com.github.varun_dhar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.*;
import java.util.Arrays;

@Mod(HUD5zig.MODID)

public class HUD5zig//setup stuff
{
	public static final String MODID = "hudmod";
	/*settings array 0- HUD x 1- HUD y 2- HUD alignment: vert/hor 3- HUD enabled/disabled
	4- Armor x 5- Armor y 6- Armor alignment: vert/hor 7- Armor enabled/disabled
	8- Death timer/coords x 9- Death timer/coords y 10- Death timer/coords alignment: vert/hor 11- Death timer/coords enabled/disabled
	 */
	public static int[] settings = new int[12];
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
				BufferedReader config = new BufferedReader(new FileReader(cfg));
				String buf;
				int i = 0;
				while ((buf = config.readLine()) != null && i < settings.length) {
					if (buf.indexOf('#') != -1) {
						continue;
					}
					settings[i] = Integer.parseInt(buf.substring(buf.indexOf('=') + 1));
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
