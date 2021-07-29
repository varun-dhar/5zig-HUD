package com.github.varun_dhar.HUD;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class HUDComponentTextGroup implements IHUDSubcomponent {
	public String[] text;
	public int x,y;
	private int ySpacing = 10;
	public boolean alignment;
	private static final FontRenderer renderer = Minecraft.getInstance().fontRenderer;
	public HUDComponentTextGroup(int size){
		text = new String[size];
	}
	public HUDComponentTextGroup(int size,int ySpacing){
		text = new String[size];
		this.ySpacing = ySpacing;
	}
	public void render(MatrixStack matrixStack){
		if(alignment) {
			int tmpY = y;
			for(String s : text) {
				renderer.drawStringWithShadow(matrixStack,s,x,tmpY,0xFFFFFF);
				tmpY+=ySpacing;
			}
		}else{
			renderer.drawStringWithShadow(matrixStack,String.join(" ",text),x,y,0xFFFFFF);
		}
	}
}
