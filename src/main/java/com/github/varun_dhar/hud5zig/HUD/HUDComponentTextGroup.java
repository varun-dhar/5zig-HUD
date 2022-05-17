/*
   Copyright 2022 Varun Dhar

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
