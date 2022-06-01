/*
   Copyright 2022 Varun Dhar

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

package com.github.varun_dhar.hud5zig.misc;

import com.github.varun_dhar.hud5zig.HUD5zig;
import com.github.varun_dhar.hud5zig.commands.CoordinateCommands;
import com.github.varun_dhar.hud5zig.commands.NavCommands;
import com.github.varun_dhar.hud5zig.HUD.components.DeathTimer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraft.client.gui.screens.DeathScreen;

public class DeathHandler {
	private static final Minecraft mc = Minecraft.getInstance();
	@SubscribeEvent
	public static void onDeath(final TickEvent.PlayerTickEvent event){
		if(!(mc.screen instanceof DeathScreen) && DeathTimer.isDead()){
			DeathTimer.setRespawnInfo();
			CoordinateCommands.saveCoordsI("lastDeath", DeathTimer.getDeathCoords(),null,null);
			if(Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED)) == 1){
				NavCommands.nav(new String[]{"lastDeath"});
			}
		}
	}

	@SubscribeEvent
	public static void onDeath(final ScreenEvent.DrawScreenEvent event){
		if(event.getScreen() instanceof DeathScreen){
			DeathTimer.setDeathInfo((int)mc.player.xOld,(int)mc.player.yOld,(int)mc.player.zOld,
					CoordinateCommands.getDimension());
		}
	}

/*
	@SubscribeEvent
	public static void onRespawn(final PlayerEvent.PlayerRespawnEvent event){
		if(event.getPlayer().equals(mc.player)){
			DeathTimer.setRespawnInfo();
			CoordinateCommands.saveCoordsI("lastDeath", DeathTimer.getDeathCoords(),null,null);
			if(Math.abs(HUD5zig.settings.get(HUD5zig.Options.NAV_TO_DEATH_ENABLED)) == 1){
				NavCommands.nav(new String[]{"lastDeath"});
			}
		}
	}*/
}
