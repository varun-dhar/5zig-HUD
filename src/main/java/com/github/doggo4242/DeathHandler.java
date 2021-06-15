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

package com.github.doggo4242;

import com.github.doggo4242.commands.CoordinateCommands;
import com.github.doggo4242.commands.NavCommands;
import com.github.doggo4242.components.DeathTimer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathHandler
{
	@SubscribeEvent
	public void onDeath(final TickEvent.PlayerTickEvent event){
		if(!event.player.isAlive() && event.player.equals(Minecraft.getInstance().player))
		{
			DeathTimer.dead = true;
			DeathTimer.deathCoords[0]=(int)Minecraft.getInstance().player.lastTickPosX;
			DeathTimer.deathCoords[1]=(int)Minecraft.getInstance().player.lastTickPosY;
			DeathTimer.deathCoords[2]=(int)Minecraft.getInstance().player.lastTickPosZ;
		}
	}
	@SubscribeEvent
	public void onRespawn(final PlayerEvent.PlayerRespawnEvent event){
		if(event.getPlayer().equals(Minecraft.getInstance().player)){
			DeathTimer.dead = false;
			DeathTimer.deathTime = System.currentTimeMillis();
			CoordinateCommands.saveCoords(new String[]{null, "lastDeath", String.valueOf(DeathTimer.deathCoords[0]),
					String.valueOf(DeathTimer.deathCoords[1]), String.valueOf(DeathTimer.deathCoords[2])});
			if(Math.abs(HUD5zig.settings.get("NavToDeathEnabled")) == 1){
				NavCommands.nav(new String[]{null,"lastDeath"});
			}
		}
	}
}