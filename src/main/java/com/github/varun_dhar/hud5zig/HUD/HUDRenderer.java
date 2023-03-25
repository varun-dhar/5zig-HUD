/*
   Copyright 2023 Varun Dhar

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

package com.github.varun_dhar.hud5zig.HUD;

import com.github.varun_dhar.hud5zig.HUD.components.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

// TODO add custom colors

public class HUDRenderer {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final ArrayList<HUDComponent> components;

	static {
		components = new ArrayList<>();
		components.add(new HUD());
		components.add(new ArmorPane());
		components.add(new DeathTimer());
		components.add(new Navigation());
		components.add(new DVD());
	}

	/**
	 * Overlay triggers upon render event
	 * @param event Contains render event info
	 */
	@SubscribeEvent
	public static void overlay(RenderGuiOverlayEvent.Post event) {
		//prevent hunger armor bar and health bar glitching, disable HUD when chat/debugger is open
		if (event.getPhase() != EventPriority.NORMAL || mc.player == null
				|| mc.screen instanceof ChatScreen || mc.options.renderDebug) {
			return;
		}

		for (HUDComponent component : components) {
			component.initComponent();
			if (component.disabled) {
				continue;
			}
			for(IHUDSubcomponent[] subcomponents : component.getSubcomponents()){
				if(subcomponents == null){
					continue;
				}
				for(IHUDSubcomponent subcomponent : subcomponents){
					subcomponent.render(event.getPoseStack());
				}
			}
		}
	}
}
