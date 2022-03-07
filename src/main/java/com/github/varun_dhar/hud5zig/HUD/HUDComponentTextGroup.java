package com.github.varun_dhar.hud5zig.HUD;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class HUDComponentTextGroup implements IHUDSubcomponent {
	public String[] text;
	public int x,y;
	private int ySpacing = 10;
	public boolean alignment;
	private static final Font renderer = Minecraft.getInstance().font;
	public HUDComponentTextGroup(int size){
		text = new String[size];
	}
	public HUDComponentTextGroup(int size,int ySpacing){
		text = new String[size];
		this.ySpacing = ySpacing;
	}
	public void render(PoseStack matrixStack){
		if(alignment) {
			int tmpY = y;
			for(String s : text) {
				renderer.drawShadow(matrixStack,s,x,tmpY,0xFFFFFF);
				tmpY+=ySpacing;
			}
		}else{
			renderer.drawShadow(matrixStack,String.join(" ",text),x,y,0xFFFFFF);
		}
	}
}
