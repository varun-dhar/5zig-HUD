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

package com.github.varun_dhar.hud5zig.misc;

import com.github.varun_dhar.hud5zig.HUD5zig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.ClickEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {
	public static void updateNotif(boolean cInv) {
		var player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}
		try {
			URL url = new URL("https://raw.githubusercontent.com/varun-dhar/5zig-HUD/main/version.json");
			InputStream stream = url.openStream();
			JsonElement root = JsonParser.parseReader(new InputStreamReader(stream));
			JsonObject rootObj = root.getAsJsonObject();
			if(rootObj.get("version").getAsInt() > HUD5zig.version) {
				var dlPage = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/the5zig-hud/files");
				var updateMsg = new TextComponent("An update is available for 5zig-HUD. Click ");
				var link = new TextComponent("here");
				link.setStyle(Style.EMPTY.setUnderlined(true));
				link.setStyle(link.getStyle().withClickEvent(dlPage));
				updateMsg.append(link);
				updateMsg.append(" to download the new version.");
				player.sendMessage(updateMsg, Util.NIL_UUID);
				if(!cInv) {
					var disableClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"5h uos");
					var disableMessage = new TextComponent("Click ");
					var disableLink = new TextComponent("here");
					disableLink.setStyle(Style.EMPTY.setUnderlined(true));
					disableLink.setStyle(disableLink.getStyle().withClickEvent(disableClick));
					disableMessage.append(disableLink);
					disableMessage.append(" to disable these notifications.");
					player.sendMessage(disableMessage, Util.NIL_UUID);
				}
			} else if(cInv){
				player.sendMessage(new TextComponent("You have the latest version of 5zig-HUD."),Util.NIL_UUID);
			}
			stream.close();
		} catch (IOException e) {
			if(cInv) {
				player.sendMessage(new TextComponent("Could not check for updates."), Util.NIL_UUID);
			}
		}
	}
}
