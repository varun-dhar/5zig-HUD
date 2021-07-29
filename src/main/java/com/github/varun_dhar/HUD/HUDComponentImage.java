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

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class HUDComponentImage implements IHUDSubcomponent{
	public ResourceLocation image;
	public int height,width,x,y;
	private static final TextureManager textureManager = Minecraft.getInstance().getTextureManager();

	public HUDComponentImage(){
		image = null;
		height = width = x = y = 0;
	}

	public HUDComponentImage(String path, int height, int width, int x, int y) {
		this(new ResourceLocation(path),height,width,x,y);
	}

	public HUDComponentImage(ResourceLocation image, int height, int width, int x, int y) {
		this.image = image;
		this.height = height;
		this.width = width;
		this.x = x;
		this.y = y;
	}

	@Override
	public void render(MatrixStack matrixStack) {
		textureManager.bindTexture(image);
		GuiUtils.drawInscribedRect(matrixStack, x, y, width, height, width, height);
	}
}