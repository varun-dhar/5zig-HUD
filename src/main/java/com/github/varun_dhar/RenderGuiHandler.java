package com.github.varun_dhar;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles render events and sends them to overlay
 */
public class RenderGuiHandler
{
	@SubscribeEvent
	public void onRenderGui(RenderGameOverlayEvent.Post event){
		HUD hud = new HUD();
		hud.overlay(event);
	}
}