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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {
	public static void updateNotif(boolean cInv) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if(player == null){
			return;
		}
		try {
			URL url = new URL("https://raw.githubusercontent.com/doggo4242/5zig-HUD/main/version.json");
			JsonParser jp = new JsonParser();
			InputStream stream = url.openStream();
			JsonElement root = jp.parse(new InputStreamReader(stream));
			JsonObject rootObj = root.getAsJsonObject();
			if(rootObj.get("version").getAsInt() > HUD5zig.version) {
				ClickEvent dlPage = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/the5zig-hud/files");
				StringTextComponent updateMsg = new StringTextComponent("An update is available for 5zig-HUD. Click ");
				StringTextComponent updateMsg2 = new StringTextComponent("here");
				updateMsg2.setStyle(Style.EMPTY.setUnderlined(true));
				updateMsg2.setStyle(updateMsg2.getStyle().setClickEvent(dlPage));
				updateMsg.append(updateMsg2);
				updateMsg.appendString(" to download the new version.");
				player.sendMessage(updateMsg, Util.DUMMY_UUID);
				if(!cInv) {
					ClickEvent disableClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"5h uos");
					StringTextComponent disableMessage1 = new StringTextComponent("Click ");
					StringTextComponent disableMessage2 = new StringTextComponent("here");
					disableMessage2.setStyle(Style.EMPTY.setUnderlined(true));
					disableMessage2.setStyle(disableMessage2.getStyle().setClickEvent(disableClick));
					disableMessage1.append(disableMessage2);
					disableMessage1.appendString(" to disable these notifications.");
					player.sendMessage(disableMessage1, Util.DUMMY_UUID);
				}
			} else if(cInv){
				player.sendMessage(new StringTextComponent("You have the latest version of 5zig-HUD."),Util.DUMMY_UUID);
			}
			stream.close();
		} catch (IOException e) {
			if(cInv) {
				player.sendMessage(new StringTextComponent("Could not check for updates."), Util.DUMMY_UUID);
			}
		}
	}
}
