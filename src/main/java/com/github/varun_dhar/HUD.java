package com.github.varun_dhar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Date;

/**
 * @TODO add custom colors
 */

public class HUD{
	private final Minecraft mc = Minecraft.getInstance();
	private final String white = TextFormatting.WHITE.toString();
	private final String gray = TextFormatting.GRAY.toString();
	private final String red = TextFormatting.RED.toString();
	//ew java enums
	private enum set{HUDX,HUDY,HUDAL,HUDEN,ARX,ARY,ARAL,AREN,DETIX,DETIY,DETIAL}
	public static boolean dead = false;
	public static int[] deathCoords = new int[3];
	private static long deathTime = 0;
	/**
	 * Overlay triggers upon render event
	 * @param event Contains render event info
	 */
	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.ALL)
		{
			return;
		}
		int width = this.mc.getMainWindow().getScaledWidth();
		int height = this.mc.getMainWindow().getScaledHeight();
		FontRenderer renderer = mc.fontRenderer;
		Date d = new Date();
		//ill figure out how color works one day...
		//red = TextFormatting.fromColorIndex(15).toString();
		if(dead)
		{
			deathTime = d.getTime();
			dead = false;
		}
		if((d.getTime()-deathTime) < 300000)//5m till despawn
		{
			String sec = String.valueOf((int)((300000-(d.getTime()-deathTime))%60000/1000));
			sec = (Integer.parseInt(sec)<10)?'0'+sec:sec;
			//god i miss c's enums
			int[] xy = {HUD5zig.settings[set.DETIX.ordinal()],HUD5zig.settings[set.DETIY.ordinal()]};
			boolean align = (Math.abs(HUD5zig.settings[set.DETIAL.ordinal()])==1);
			xy[0]=(xy[0]==-1 || ((xy[0]+20)>width && !align))?3:xy[0];
			xy[1]=(xy[1]==-1 || ((xy[1]+20)>height && align))?106:xy[1];
			renderer.drawStringWithShadow(event.getMatrixStack(), red+"Items despawn in> "+white+((300000-(d.getTime()-deathTime))/60000)+":"+sec, xy[0], xy[1], 0xFFFFFF);
			//come to think of it, i also miss boolean algebra
			xy[(align)?1:0]+=(align)?10:120;//if -1 or 1, set vertical alignment, else horizontal
			renderer.drawStringWithShadow(event.getMatrixStack(),red+"Died at> X: "+white+deathCoords[0]+red+" Y: "+white+deathCoords[1]+red+" Z: "+white+deathCoords[2],xy[0],xy[1],0xFFFFFF);
		}
		if(Math.abs(HUD5zig.settings[set.HUDEN.ordinal()]) == 1)
		{
			int[] xy = {HUD5zig.settings[set.HUDX.ordinal()],HUD5zig.settings[set.HUDY.ordinal()]};
			boolean align = (Math.abs(HUD5zig.settings[set.HUDAL.ordinal()])==1);
			xy[0]=(xy[0]==-1 || ((xy[0]+50)>width && !align))?3:xy[0];
			xy[1]=(xy[1]==-1 || ((xy[1]+50)>height && align))?3:xy[1];
			String[] disp = HUDOverlay();
			for (String s : disp) {
				renderer.drawStringWithShadow(event.getMatrixStack(), s, xy[0], xy[1], 0xFFFFFF);
				xy[(align)?1:0]+=(align)?10:45;//if -1 or 1, set vertical alignment, else horizontal
			}
		}
		if(Math.abs(HUD5zig.settings[set.AREN.ordinal()]) == 1)
		{
			int[] xy = {HUD5zig.settings[set.ARX.ordinal()],HUD5zig.settings[set.ARY.ordinal()]};
			boolean align = (Math.abs(HUD5zig.settings[set.ARAL.ordinal()])==1);
			xy[0]=(xy[0]==-1 || ((xy[0]+50)>width && !align))?3:xy[0];
			xy[1]=(xy[1]==-1 || ((xy[1]+50)>height && align))?66:xy[1];
			String[] disp = armorOverlay();
			renderer.drawStringWithShadow(event.getMatrixStack(), TextFormatting.RED.toString()+TextFormatting.UNDERLINE.toString()+"Armor:",3,53,0xFFFFFF);
			for(String s : disp)
			{
				renderer.drawStringWithShadow(event.getMatrixStack(), s, xy[0], xy[1], 0xFFFFFF);
				xy[(align)?1:0]+=(align)?10:75;//if -1 or 1, set vertical alignment, else horizontal
			}
		}

	}
	public String[] HUDOverlay()
	{
		int posX = (int)mc.player.lastTickPosX;//getting positions
		int posY = (int)mc.player.lastTickPosY;
		int posZ = (int)mc.player.lastTickPosZ;
		int dir = (int)mc.player.rotationYaw;//getting direction
		dir+=(dir<0)?382:22;
		dir%=360;
		dir/=45;
		//direction formatting
		//hardcoded array v2, wish me luck
		String[] face = {"S "+gray+"+Z","SW "+gray+"-X +Z","W "+gray+"-X","NW "+ gray+"-X -Z",
				"N "+gray+"-Z","NE "+gray+"+X -Z","E "+gray+"+X","SE "+gray+"+X +Z"};
		dir=(dir<0||dir>=face.length)?0:dir;
		String[] hud = {red+"X"+white+"> "+posX,red+"Y"+white+"> "+posY,red+"Z"+white+"> "+posZ
		,red+"FPS"+white+"> "+Minecraft.debugFPS,red+"F"+white+"> "+face[dir]};
		//System.out.println(dir);
		return hud;
	}
	public String[] armorOverlay()
	{
		ItemStack[] armor = {mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD),mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST),mc.player.getItemStackFromSlot(EquipmentSlotType.LEGS),mc.player.getItemStackFromSlot(EquipmentSlotType.FEET)};
		String[] dura = new String[4];
		char[] armorLetters = {'H','C','L','B'};
		String[] armorStr = {"helmet","chestplate","leggings","boots"};
		for(int i = 0;i<4;i++)
		{
			int t = (!armor[i].isEmpty()&&armor[i].getMaxDamage()!=0)?(int)Math.ceil(100-(((float)armor[i].getDamage()/armor[i].getMaxDamage())*100)):0;
			dura[i] = (t!=0)?red+armorLetters[i]+white+"> "+t+"%":"No "+armorStr[i];
		}
		return dura;
	}
}
/* lie here in disgrace until i potentially need you again hahahhaah no i dont lol die
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

 */