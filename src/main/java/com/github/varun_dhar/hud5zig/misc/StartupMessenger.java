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
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class StartupMessenger {
	private static final ArrayList<String> messages = new ArrayList<>();
	private static boolean firstRun = true;
	@SubscribeEvent
	public static void onInitialPlayerJoin(ClientPlayerNetworkEvent.LoggedInEvent event){
		if(!firstRun){
			return;
		}
		firstRun = false;
		if(Math.abs(HUD5zig.settings.get(HUD5zig.Options.UPDATER_ENABLED)) == 1) {
			UpdateChecker.updateNotif(false);
		}
		LocalPlayer player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}
		for(String msg : messages){
			player.sendMessage(new TextComponent(msg), Util.NIL_UUID);
		}
	}
	public static void addMessage(String message){messages.add(message);}
}
