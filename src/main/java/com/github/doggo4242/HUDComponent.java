/*
   Copyright 2021 doggo4242 Development

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

package com.github.doggo4242;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.TextFormatting;

public abstract class HUDComponent {
	protected HUDComponentText[] componentText;
	protected HUDComponentImage[] componentImages;
	protected boolean disabled = false;
	protected final int defYSpacing = 10;
	protected final int defXPos = 3;
	protected static int scrWidth;
	protected static int scrHeight;
	protected static final Minecraft mc = Minecraft.getInstance();
	protected static ClientPlayerEntity player;
	protected static final String white = TextFormatting.WHITE.toString();
	protected static final String gray = TextFormatting.GRAY.toString();
	protected static final String red = TextFormatting.RED.toString();

	public HUDComponentImage[] getComponentImages() {
		return componentImages;
	}

	public HUDComponentText[] getComponentText() {
		return componentText;
	}

	public void initComponent(){
		scrWidth = mc.getMainWindow().getScaledWidth();
		scrHeight = mc.getMainWindow().getScaledHeight();
		player = mc.player;
		updateComponent();
	}
	public abstract void updateComponent();
}
