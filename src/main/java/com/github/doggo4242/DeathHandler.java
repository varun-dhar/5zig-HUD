package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathHandler
{
	@SubscribeEvent
	public void onDeath(final TickEvent.PlayerTickEvent event){
		if(!event.player.isAlive() && Minecraft.getInstance().player != null)
		{
			System.out.println("uh oh ya deado");
			HUD.dead = true;
			HUD.deathCoords[0]=(int)Minecraft.getInstance().player.lastTickPosX;
			HUD.deathCoords[1]=(int)Minecraft.getInstance().player.lastTickPosY;
			HUD.deathCoords[2]=(int)Minecraft.getInstance().player.lastTickPosZ;
		}
	}
}