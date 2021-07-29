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

package com.github.doggo4242.misc;

import com.github.doggo4242.HUD5zig;
import com.github.doggo4242.commands.CoordinateCommands;
import com.github.doggo4242.commands.NavCommands;
import com.github.doggo4242.HUD.components.DeathTimer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DeathHandler {
	private static final Minecraft mc = Minecraft.getInstance();
	@SubscribeEvent
	public static void onDeath(final TickEvent.PlayerTickEvent event){
		if(!event.player.isAlive() && event.player.equals(mc.player)) {
			DeathTimer.setDeathInfo((int)mc.player.lastTickPosX,(int)mc.player.lastTickPosY,(int)mc.player.lastTickPosZ,
					CoordinateCommands.getDimension());
		}
	}
	@SubscribeEvent
	public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event){
		if(event.getPlayer().equals(mc.player)){
			DeathTimer.setRespawnInfo();
			CoordinateCommands.saveCoordsI("lastDeath", DeathTimer.getDeathCoords());
			if(Math.abs(HUD5zig.settings.get("NavToDeathEnabled")) == 1){
				NavCommands.nav(new String[]{"lastDeath"});
			}
		}
	}
}