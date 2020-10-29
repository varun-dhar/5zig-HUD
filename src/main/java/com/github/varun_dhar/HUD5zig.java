package com.github.varun_dhar;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HUD5zig.MODID)

public class HUD5zig//setup stuff
{
	public static final String MODID = "hudmod";
	public HUD5zig() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
	}
	private void commonSetup(FMLCommonSetupEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new RenderGuiHandler());
	}

}
