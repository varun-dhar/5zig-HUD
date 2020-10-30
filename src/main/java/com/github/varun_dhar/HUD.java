/*
Copyright 2020 Varun Dhar

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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.wrapper.EntityEquipmentInvWrapper;

/**
 * @TODO add custom colors, options to show/hide data in config file
 */

public class HUD{
	private final Minecraft mc = Minecraft.getInstance();

	/**
	 * Overlay triggers upon render event
	 * @param event Contains render event info
	 */
	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		ItemStack helmet = mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD);
		ItemStack chestplate = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
		ItemStack leggings = mc.player.getItemStackFromSlot(EquipmentSlotType.LEGS);
		ItemStack boots = mc.player.getItemStackFromSlot(EquipmentSlotType.FEET);
		int hd,cd,ld,bd;
		System.out.println(boots.getDamage()+" "+boots.getMaxDamage());
		hd = (!helmet.isEmpty()&&helmet.getMaxDamage()!=0)?(int)Math.ceil(100-(((float)helmet.getDamage()/helmet.getMaxDamage())*100)):0;
		cd = (!chestplate.isEmpty()&&chestplate.getMaxDamage()!=0)?(int)Math.ceil(100-(((float)chestplate.getDamage()/chestplate.getMaxDamage())*100)):0;
		ld = (!leggings.isEmpty()&&leggings.getMaxDamage()!=0)?(int)Math.ceil(100-(((float)leggings.getDamage()/leggings.getMaxDamage())*100)):0;
		bd = (!boots.isEmpty()&&boots.getMaxDamage()!=0)?(int)Math.ceil(100-(((float)boots.getDamage()/boots.getMaxDamage())*100)):0;
//		int width = this.mc.getMainWindow().getScaledWidth();
//		int height = this.mc.getMainWindow().getScaledHeight();
		FontRenderer renderer = mc.fontRenderer;
		String red = TextFormatting.RED.toString();
		//red = TextFormatting.fromColorIndex(15).toString();
		String white = TextFormatting.WHITE.toString();
		String gray = TextFormatting.GRAY.toString();
		int posX = (int)mc.player.lastTickPosX;//getting positions
		int posY = (int)mc.player.lastTickPosY;
		int posZ = (int)mc.player.lastTickPosZ;
		int dir = (int)mc.player.rotationYaw;//getting direction
		dir+=(dir<0)?382:22;
		dir%=360;
		dir/=45;
		//direction formatting
		//will fix intermediate directions later
		//String[] face = {"S "+gray+"+Z","SW "+gray+"-X +Z","W "+gray+"-X","NW "+gray+"-X -Z","N "+gray+"-Z","NE "+gray+"+X -Z","E "+gray+"+X","SE "+gray+"+X +Z"};
		//System.out.println(dir);
		String tx = red+"X"+white+"> "+posX;//formatting stuff
		String ty = red+"Y"+white+"> "+posY;
		String tz = red+"Z"+white+"> "+posZ;
		String tfps = red+"FPS"+white+"> "+Minecraft.debugFPS;
		String thd = (hd!=0)?red+"H"+white+"> "+hd+"%":"None";
		String tcd = (cd!=0)?red+"C"+white+"> "+cd+"%":"None";
		String tld = (ld!=0)?red+"L"+white+"> "+ld+"%":"None";
		String tbd = (bd!=0)?red+"B"+white+"> "+bd+"%":"None";
		String tface = red+"F"+white+"> ";
		//why the hardcoded array didn't work, i dont know. at least this does.
		switch (dir)
		{
			case 0:
				tface += "S "+gray+"+Z";
				break;
			case 1:
				tface += "SW "+gray+"-X +Z";
				break;
			case 2:
				tface += "W "+gray+"-X";
				break;
			case 3:
				tface += "NW "+gray+"-X -Z";
				break;
			case 4:
				tface += "N "+gray+"-Z";
				break;
			case 5:
				tface += "NE "+gray+"+X -Z";
				break;
			case 6:
				tface += "E "+gray+"+X";
				break;
			case 7:
				tface += "SE "+gray+"+X +Z";
				break;
			default:
				tface = "";
				break;
		}
		renderer.drawStringWithShadow(event.getMatrixStack(),tx,3,3,0xFFFFFF);//display the text
		renderer.drawStringWithShadow(event.getMatrixStack(),ty,3,13,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tz,3,23,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tfps,3,33,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tface,3,43,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(), TextFormatting.RED.toString()+TextFormatting.UNDERLINE.toString()+"Armor:",3,53,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),thd,3,63,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tcd,3,73,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tld,3,83,0xFFFFFF);
		renderer.drawStringWithShadow(event.getMatrixStack(),tbd,3,93,0xFFFFFF);
	}

}
