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

import com.github.varun_dhar.components.ArmorPane;
import com.github.varun_dhar.components.DeathTimer;
import com.github.varun_dhar.components.HUD;
import com.github.varun_dhar.components.Navigation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

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
	}

	/**
	 * Overlay triggers upon render event
	 * @param event Contains render event info
	 */
	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.ALL || mc.player == null)//prevent hunger armor bar and health bar glitching
		{
			return;
		}

		for(HUDComponent component : components){
			component.initComponent();
			HUDComponentText[] text = component.componentText;
			if(text != null) {
				for (int i = 0; i < text.length; i++) {
					StringBuilder builder = new StringBuilder().append(text[i]);
					for (int j = i + 1; j < text.length && text[j].x == text[i].x && text[j].y == text[i].y; j++) {
						builder.append(" ").append(text[j]);
					}
					renderer.drawStringWithShadow(event.getMatrixStack(), builder.toString(), text[i].x, text[i].y, 0xFFFFFF);
				}
			}
			HUDComponentImage[] images = component.componentImages;
			if(images != null){
				for(HUDComponentImage image : images){
					mc.getTextureManager().bindTexture(image.image);
					GuiUtils.drawInscribedRect(event.getMatrixStack(), image.x,image.y,image.width,image.height,image.width,image.height);
				}
			}
		}

		/*
		int width = mc.getMainWindow().getScaledWidth();
		int height = mc.getMainWindow().getScaledHeight();
		Date d = new Date();
		//ill figure out how color works one day...
		//red = TextFormatting.fromColorIndex(15).toString();
		if(navLoc != null)//enable navigation system if string is not null
		{
			//extract x and z values
			Matcher m = navCoords.matcher(navLoc);
			if(m.matches()){
				final int defNavX = width/2+110;
				final int defNavY = height-40;
				int x = Integer.parseInt(m.group(1));
				int z = Integer.parseInt(m.group(2));
				double angle = getAngle(x,z);//get angle between coordinates
				//set x and y of compass and degree info
				int compX = (HUD5zig.settings.get("NavX")==-1)?defNavX:HUD5zig.settings.get("NavX");
				int compY = (HUD5zig.settings.get("NavY")==-1)?defNavY:HUD5zig.settings.get("NavY");
				renderer.drawStringWithShadow(event.getMatrixStack(),
						(((int)angle>180)?(int)angle-360:(int)angle)+"\u00b0",
						compX+5,compY+30,0xFFFFFF);
				//invert degrees because minecraft's weird
				angle=((angle-180)%360+360)%360;
				//set compass picture to use depending on which angle is closest
				angle=Math.round(angle/(360/27.0));
				//get the compass image
				mc.getTextureManager().bindTexture(new ResourceLocation(HUD5zig.MODID+":"+(int)angle+".png"));
				//GuiUtils.drawTexturedModalRect(event.getMatrixStack(),width-30,0,98,98,30,30,0);
				//draw the compass image
				GuiUtils.drawInscribedRect(event.getMatrixStack(),compX,compY,30,30,30,30);
				//check if close enough to stop navigation
				if(Math.abs(x-mc.player.getPosX()) < 10 && Math.abs(z-mc.player.getPosZ()) < 10)
				{
					mc.player.sendMessage(new StringTextComponent("Arrived."), Util.DUMMY_UUID);
					navLoc = null;
				}
			}else{
				mc.player.sendMessage(new StringTextComponent("Error parsing coordinates. Please re-enter them and try again."),Util.DUMMY_UUID);
				navLoc = null;
			}
		}
		if(dead)//if dead record death time
		{
			deathTime = d.getTime();
		}
		if(!dead && (d.getTime()-deathTime) < tUnitMs.convert(5,TimeUnit.MINUTES)
				&& Math.abs(HUD5zig.settings.get("DeathTimerEnabled")) == 1)//5m till despawn
		{
			//get seconds without minutes
			int sec = (int)((tUnitMs.convert(5,TimeUnit.MINUTES)-
					(d.getTime()-deathTime))%tUnitMs.convert(1, TimeUnit.MINUTES)/
					tUnitMs.convert(1,TimeUnit.SECONDS));
			int min = (int)((tUnitMs.convert(5,TimeUnit.MINUTES)-(d.getTime()-deathTime))/
					tUnitMs.convert(1,TimeUnit.MINUTES));
			//set alignment and position
			int x = HUD5zig.settings.get("DeathTimerX");
			int y = HUD5zig.settings.get("DeathTimerY");
			x = (x == -1 || (x + 20) > width) ? defXPos : x;
			if(Math.abs(HUD5zig.settings.get("DeathTimerAlignment"))==1) {
				final int defDTY = 126;
				y = (y == -1 || (y + 20) > height) ? defDTY : y;
				renderer.drawStringWithShadow(event.getMatrixStack(),
						String.format("%sItems despawn in> %s %d:%02d", red, white, min, sec), x, y, 0xFFFFFF);
				y += defYSpacing;
				renderer.drawStringWithShadow(event.getMatrixStack(),
						String.format("%sDied at> X: %s%d%s Y: %s%d%s Z: %s%d",
								red, white, deathCoords[0], red, white, deathCoords[1], red, white, deathCoords[2]),
						x, y, 0xFFFFFF);
			}else{
				final int defDTY = 43;
				y = (y == -1 || (y + 20) > height) ? defDTY : y;
				renderer.drawStringWithShadow(event.getMatrixStack(),
						String.format("%sItems despawn in> %s %d:%02d %sDied at> X: %s%d%s Y: %s%d%s Z: %s%d",
								red, white, min, sec,red, white, deathCoords[0], red, white, deathCoords[1], red, white, deathCoords[2]),
						x,y,0xFFFFFF);
			}
		}
		if(Math.abs(HUD5zig.settings.get("HUD-Enabled")) == 1)//if hud enabled, draw it
		{
			final int defHUDY = 3;
			//set alignment and position
			int x = HUD5zig.settings.get("HUD-X");
			int y = HUD5zig.settings.get("HUD-Y");
			String[] disp = HUDOverlay();//get the required info
			x = (x == -1 || (x + defYSpacing*disp.length) > width) ? defXPos : x;
			y = (y == -1 || (y + defYSpacing*disp.length) > height) ? defHUDY : y;
			if(Math.abs(HUD5zig.settings.get("HUD-Alignment"))==1) {
				for (String s : disp) {//draw the strings
					renderer.drawStringWithShadow(event.getMatrixStack(), s, x, y, 0xFFFFFF);
					y+=defYSpacing;
				}
			}else{
				renderer.drawStringWithShadow(event.getMatrixStack(),String.join(" ",disp),x,y,0xFFFFFF);
			}
		}
		if(Math.abs(HUD5zig.settings.get("ArmorEnabled")) == 1)//if armor pane enabled, draw it
		{
			//set alignment and position
			int x = HUD5zig.settings.get("ArmorX");
			int y = HUD5zig.settings.get("ArmorY");
			String[] disp = armorOverlay();//get required information
			x = (x == -1 || (x + defYSpacing * disp.length) > width) ? defXPos : x;
			final boolean inYRange = y == -1 || (y + defYSpacing * disp.length) > height;
			if(Math.abs(HUD5zig.settings.get("ArmorAlignment"))==1) {
				final int defArmorY = 66;
				y = inYRange ? defArmorY : y;
				renderer.drawStringWithShadow(event.getMatrixStack(), red + TextFormatting.UNDERLINE + "Armor:", x, y, 0xFFFFFF);
				y += 13;
				for (String s : disp)//draw the strings
				{
					renderer.drawStringWithShadow(event.getMatrixStack(), s, x, y, 0xFFFFFF);
					y += defYSpacing;
				}
			}else{
				final int defArmorY = 23;
				y = inYRange ? defArmorY : y;
				renderer.drawStringWithShadow(event.getMatrixStack(),
						String.format("%s%sArmor:%s %s",red,TextFormatting.UNDERLINE,TextFormatting.RESET,String.join(" ",disp)),
						x,y,0xFFFFFF);
			}
		}*/

	}
	/*
	private String[] HUDOverlay()
	{
		int posX = (int)mc.player.getPosX();//getting positions
		int posY = (int)mc.player.getPosY();
		int posZ = (int)mc.player.getPosZ();
		int dir = (int)mc.player.rotationYaw;//getting direction
		dir=(dir%360+360+22)%360;//prevent negatives, get 0-359 angle, add magic number
		dir/=45;//determine direction
		//direction formatting
		final String[] face = {"S "+gray+"+Z","SW "+gray+"-X +Z","W "+gray+"-X","NW "+ gray+"-X -Z",
				"N "+gray+"-Z","NE "+gray+"+X -Z","E "+gray+"+X","SE "+gray+"+X +Z"};
		return new String[]{red+"X"+white+"> "+posX,red+"Y"+white+"> "+posY,red+"Z"+white+"> "+posZ
		,red+"FPS"+white+"> "+ Minecraft.debugFPS,red+"F"+white+"> "+face[dir]};
	}
	private String[] armorOverlay()
	{
		//get armor item stacks
		ItemStack[] armor = {mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD),mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST),mc.player.getItemStackFromSlot(EquipmentSlotType.LEGS),mc.player.getItemStackFromSlot(EquipmentSlotType.FEET)};
		String[] dura = new String[4];
		char[] armorLetters = {'H','C','L','B'};
		final String[] armorStr = {"helmet","chestplate","leggings","boots"};
		for(int i = 0;i<4;i++)
		{
			//check for empty slots and calculate damage in percent, otherwise say no armor
			int t = (!armor[i].isEmpty()&&armor[i].getMaxDamage()!=0)?(int)Math.ceil(100-(((float)armor[i].getDamage()/armor[i].getMaxDamage())*100)):0;
			dura[i] = (t>0)?red+armorLetters[i]+white+"> "+t+"%":"No "+armorStr[i];
		}
		return dura;
	}
	private double getAngle(int x, int z)
	{
		//get the angle between the two points
		double a = Math.abs(mc.player.getPosX()-x);
		double b = Math.abs(mc.player.getPosZ()-z);
		double angle = Math.toDegrees(Math.atan(b/a));
		//find quadrant
		//use to determine exact angle
		//270+angle +x, +z
		//90-angle -x, +z
		//270-angle +x, -z
		//90+angle -x, -z
		int q = (mc.player.getPosX()>x)?-90:270;//if -x
		q*=(mc.player.getPosZ()>z)?-1:1;//if -z
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Dir nq: "+angle,100,130,0xFFFFFF);
		angle = (q<0)?-(angle+q):angle+q;
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Deg: "+Math.abs((mc.player.rotationYaw%360)),100,100,0xFFFFFF);
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Dir: "+angle,100,110,0xFFFFFF);
		angle = ((mc.player.rotationYaw-angle)%360+360)%360;//get difference between calculated angle and player angle. takes care of negatives with (x%360+360)%360
		return angle;
	}
	 */
}
