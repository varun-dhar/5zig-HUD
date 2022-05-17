/*
   Copyright 2022 doggo4242 Development

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

package com.github.doggo4242.hud5zig.HUD;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class HUDComponentText implements IHUDSubcomponent {
	public int x,y;
	public String text;
	private static final Font renderer = Minecraft.getInstance().font;

	@Override
	public void render(PoseStack matrixStack) {
		renderer.drawShadow(matrixStack,text,x,y,0xFFFFFF);
	}
}
