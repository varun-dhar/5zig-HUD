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

package com.github.varun_dhar.HUD;

import com.github.varun_dhar.HUD.components.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;

// TODO add custom colors

public class HUDRenderer {
	private static final Minecraft mc = Minecraft.getInstance();
	private static final FontRenderer renderer = mc.fontRenderer;
	private final ArrayList<HUDComponent> components;

	public HUDRenderer(){
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
	public void overlay(RenderGameOverlayEvent.Post event) {
		//prevent hunger armor bar and health bar glitching, disable HUD when chat/debugger is open
		if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null
				|| mc.currentScreen instanceof ChatScreen || mc.gameSettings.showDebugInfo) {
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
					subcomponent.render(event.getMatrixStack());
				}
			}
		}
	}
}
