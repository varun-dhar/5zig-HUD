/*
   Copyright 2021 Varun Dhar

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

package com.github.varun_dhar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

public class StartupMessenger {
	public static ArrayList<String> messages = new ArrayList<>();
	public static boolean firstRun = true;
	@SubscribeEvent
	public void onInitialPlayerJoin(ClientPlayerNetworkEvent.LoggedInEvent event){
		if(!firstRun){
			return;
		}
		firstRun = false;
		if(Math.abs(HUD5zig.settings.get("UpdaterEnabled")) == 1) {
			UpdateChecker.updateNotif(false);
		}
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}
		for(String msg : messages){
			player.sendMessage(new StringTextComponent(msg), Util.DUMMY_UUID);
		}
	}
}
