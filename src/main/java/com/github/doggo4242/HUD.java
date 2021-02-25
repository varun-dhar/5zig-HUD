package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

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
	public enum set{HUDX,HUDY,HUDAL,HUDEN,ARX,ARY,ARAL,AREN,DETIX,DETIY,DETIAL,DETIEN,NAVX,NAVY}
	public static boolean dead = false;
	public static int[] deathCoords = new int[3];
	private static long deathTime = 0;
	public static String navLoc = null;
	/**
	 * Overlay triggers upon render event
	 * @param event Contains render event info
	 */
	@SubscribeEvent
	public void overlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.ALL)//prevent hunger armor bar and health bar glitching
		{
			return;
		}
		int width = this.mc.getMainWindow().getScaledWidth();
		int height = this.mc.getMainWindow().getScaledHeight();
		FontRenderer renderer = mc.fontRenderer;
		Date d = new Date();
		//ill figure out how color works one day...
		//red = TextFormatting.fromColorIndex(15).toString();
		if(navLoc != null)//enable navigation system if string is not null
		{
			//extract x and z values
			String coords = navLoc;
			coords = coords.substring(coords.indexOf('X')+3);
			int x = Integer.parseInt(coords.substring(0,coords.indexOf(' ')));
			coords = coords.substring(coords.indexOf('Z')+3);
			int z = Integer.parseInt(coords);
			double angle = getAngle(x,z);//get angle between coordinates
			//set x and y of compass and degree info
			int compX = (HUD5zig.settings[set.NAVX.ordinal()]==-1)?width/2+110:HUD5zig.settings[set.NAVX.ordinal()];
			int compY = (HUD5zig.settings[set.NAVY.ordinal()]==-1)?height-40:HUD5zig.settings[set.NAVY.ordinal()];
			renderer.drawStringWithShadow(event.getMatrixStack(),(((int)angle>180)?(int)angle-360:(int)angle)+"\u00b0",compX+5,compY+30,0xFFFFFF);
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
		}
		if(dead)//if dead record death time
		{
			deathTime = d.getTime();
			dead = false;
		}
		if((d.getTime()-deathTime) < 300000 && Math.abs(HUD5zig.settings[set.DETIEN.ordinal()]) == 1)//5m till despawn
		{
			//get seconds without minutes (gonna be honest i forgot what 60000 and 1000 mean)
			String sec = String.valueOf((int)((300000-(d.getTime()-deathTime))%60000/1000));
			//add a 0 if its less than 10
			sec = (Integer.parseInt(sec)<10)?'0'+sec:sec;
			//set alignment and position
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
		if(Math.abs(HUD5zig.settings[set.HUDEN.ordinal()]) == 1)//if hud enabled, draw it
		{
			//set alignment and position
			int[] xy = {HUD5zig.settings[set.HUDX.ordinal()],HUD5zig.settings[set.HUDY.ordinal()]};
			boolean align = (Math.abs(HUD5zig.settings[set.HUDAL.ordinal()])==1);
			xy[0]=(xy[0]==-1 || ((xy[0]+50)>width && !align))?3:xy[0];
			xy[1]=(xy[1]==-1 || ((xy[1]+50)>height && align))?3:xy[1];
			String[] disp = HUDOverlay();//get the required info
			for (String s : disp) {//draw the strings
				renderer.drawStringWithShadow(event.getMatrixStack(), s, xy[0], xy[1], 0xFFFFFF);
				xy[(align)?1:0]+=(align)?10:45;//if -1 or 1, set vertical alignment, else horizontal
			}
		}
		if(Math.abs(HUD5zig.settings[set.AREN.ordinal()]) == 1)//if armor pane enabled, draw it
		{
			//set alignment and position
			int[] xy = {HUD5zig.settings[set.ARX.ordinal()],HUD5zig.settings[set.ARY.ordinal()]};
			boolean align = (Math.abs(HUD5zig.settings[set.ARAL.ordinal()])==1);
			xy[0]=(xy[0]==-1 || ((xy[0]+50)>width && !align))?3:xy[0];
			xy[1]=(xy[1]==-1 || ((xy[1]+50)>height && align))?66:xy[1];
			String[] disp = armorOverlay();//get required information
			renderer.drawStringWithShadow(event.getMatrixStack(), TextFormatting.RED.toString()+TextFormatting.UNDERLINE.toString()+"Armor:",3,53,0xFFFFFF);
			for(String s : disp)//draw the strings
			{
				renderer.drawStringWithShadow(event.getMatrixStack(), s, xy[0], xy[1], 0xFFFFFF);
				xy[(align)?1:0]+=(align)?10:75;//if -1 or 1, set vertical alignment, else horizontal
			}
		}

	}
	private String[] HUDOverlay()
	{
		int posX = (int)mc.player.getPosX();//getting positions
		int posY = (int)mc.player.getPosY();
		int posZ = (int)mc.player.getPosZ();
		int dir = (int)mc.player.rotationYaw;//getting direction
		dir=(dir%360+360+22)%360;//prevent negatives, get 0-359 angle, add magic number
		dir/=45;//determine direction
		//direction formatting
		//hardcoded array v2, wish me luck
		String[] face = {"S "+gray+"+Z","SW "+gray+"-X +Z","W "+gray+"-X","NW "+ gray+"-X -Z",
				"N "+gray+"-Z","NE "+gray+"+X -Z","E "+gray+"+X","SE "+gray+"+X +Z"};
		String[] hud = {red+"X"+white+"> "+posX,red+"Y"+white+"> "+posY,red+"Z"+white+"> "+posZ
		,red+"FPS"+white+"> "+Minecraft.debugFPS,red+"F"+white+"> "+face[dir]};
		//System.out.println(dir);
		return hud;
	}
	private String[] armorOverlay()
	{
		//get armor item stacks
		ItemStack[] armor = {mc.player.getItemStackFromSlot(EquipmentSlotType.HEAD),mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST),mc.player.getItemStackFromSlot(EquipmentSlotType.LEGS),mc.player.getItemStackFromSlot(EquipmentSlotType.FEET)};
		String[] dura = new String[4];
		char[] armorLetters = {'H','C','L','B'};
		String[] armorStr = {"helmet","chestplate","leggings","boots"};
		for(int i = 0;i<4;i++)
		{
			//check for empty slots and calculate damage in percent, otherwise say no armor
			int t = (!armor[i].isEmpty()&&armor[i].getMaxDamage()!=0)?(int)Math.ceil(100-(((float)armor[i].getDamage()/armor[i].getMaxDamage())*100)):0;
			dura[i] = (t!=0)?red+armorLetters[i]+white+"> "+t+"%":"No "+armorStr[i];
		}
		return dura;
	}
	private double getAngle(int x, int z)
	{
		//get the angle between the two points
		double a = Math.abs(Math.abs(mc.player.getPosX())-Math.abs(x));
		double b = Math.abs(Math.abs(mc.player.getPosZ())-Math.abs(z));
		double angle = Math.toDegrees(Math.atan(b/a));
		//find quadrant
		//use to determine exact angle
		//use nice little state machine to do so
		//theres probably a better way to do this
		double[] quads = {270+angle/*+x+z 270+*/,90-angle/*-x+z 90-*/,/*+x-z270-*/270-angle,/*-x-z 90+*/90+angle};
		int q = (mc.player.getPosX()>x)?1:0;//if -x
		q+=(mc.player.getPosZ()>z)?2:0;//if -z
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Dir nq: "+angle,100,130,0xFFFFFF);
		angle = quads[q];
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Deg: "+Math.abs((mc.player.rotationYaw%360)),100,100,0xFFFFFF);
		//renderer.drawStringWithShadow(event.getMatrixStack(),"Dir: "+angle,100,110,0xFFFFFF);
		angle = ((mc.player.rotationYaw-angle)%360+360)%360;//get difference between calculated angle and player angle. takes care of negatives with (x%360+360)%360
		return angle;
	}
}
