package com.github.doggo4242;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker {
	public static boolean firstRun = true;
	@SubscribeEvent
	public void updateCheck(ClientPlayerNetworkEvent.LoggedInEvent event)
	{
		if(firstRun && Math.abs(HUD5zig.settings[12]) == 1) {//run only
			firstRun = false;
			updateNotif(false);
		}
	}
	public static void updateNotif(boolean cInv)
	{
		try {
			URL url = new URL("https://raw.githubusercontent.com/doggo4242/5zig-HUD/main/version.json");
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(new InputStreamReader(url.openStream()));
			JsonObject rootObj = root.getAsJsonObject();
			if(rootObj.get("version").getAsInt() > HUD5zig.version)
			{
				ClickEvent dlPage = new ClickEvent(ClickEvent.Action.OPEN_URL,"https://www.curseforge.com/minecraft/mc-mods/the5zig-hud/files");
				StringTextComponent updateMsg = new StringTextComponent("An update is available for 5zig-HUD. Click ");
				StringTextComponent updateMsg2 = new StringTextComponent("here");
				updateMsg2.setStyle(Style.EMPTY.setUnderlined(true));
				updateMsg2.setStyle(updateMsg2.getStyle().setClickEvent(dlPage));
				updateMsg.append(updateMsg2);
				updateMsg.appendString(" to download the new version.");
				Minecraft.getInstance().player.sendMessage(updateMsg, Util.DUMMY_UUID);
				if(!cInv)
				{
					ClickEvent disableClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND,"5h uos");
					StringTextComponent disableMessage1 = new StringTextComponent("Click ");
					StringTextComponent disableMessage2 = new StringTextComponent("here");
					disableMessage2.setStyle(Style.EMPTY.setUnderlined(true));
					disableMessage2.setStyle(disableMessage2.getStyle().setClickEvent(disableClick));
					disableMessage1.append(disableMessage2);
					disableMessage1.appendString(" to disable these notifications.");
					Minecraft.getInstance().player.sendMessage(disableMessage1, Util.DUMMY_UUID);
				}
			}
			else if(cInv){
				Minecraft.getInstance().player.sendMessage(new StringTextComponent("You have the latest version of 5zig-HUD."),Util.DUMMY_UUID);
			}
			url.openStream().close();
		} catch (IOException e) {
			System.out.println("uh oh crash");
			if(cInv) {
				Minecraft.getInstance().player.sendMessage(new StringTextComponent("Could not check for updates."), Util.DUMMY_UUID);
			}
		}
	}
}
