package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.entity.player.PlayerEntity;


public class DeathHandler
{
	@SubscribeEvent
	public void onDeath(LivingDeathEvent event){
		if(event.getEntity() instanceof PlayerEntity)
		{
			System.out.println("uh oh ya deado");
			HUD.dead = true;
			HUD.deathCoords[0]=(int)Minecraft.getInstance().player.lastTickPosX;
			HUD.deathCoords[1]=(int)Minecraft.getInstance().player.lastTickPosY;
			HUD.deathCoords[2]=(int)Minecraft.getInstance().player.lastTickPosZ;
		}
	}
}