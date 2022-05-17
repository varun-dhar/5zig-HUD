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

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.ChatFormatting;

public abstract class HUDComponent {
	protected HUDComponentText[] componentText;
	protected HUDComponentTextGroup[] componentTextGroups;
	protected HUDComponentImage[] componentImages;
	protected IHUDSubcomponent[][] subcomponents;
	protected boolean disabled = false;
	protected final int defYSpacing = 10;
	protected final int defXPos = 3;
	protected static int scrWidth;
	protected static int scrHeight;
	protected static final Minecraft mc = Minecraft.getInstance();
	protected static LocalPlayer player;
	protected static final String white = ChatFormatting.WHITE.toString();
	protected static final String gray = ChatFormatting.GRAY.toString();
	protected static final String red = ChatFormatting.RED.toString();

	public IHUDSubcomponent[][] getSubcomponents(){
		return subcomponents;
	}

	public HUDComponent(){
		subcomponents = new IHUDSubcomponent[3][];
	}

	public void initComponent(){
		scrWidth = mc.getWindow().getGuiScaledWidth();
		scrHeight = mc.getWindow().getGuiScaledHeight();
		player = mc.player;
		subcomponents[0] = componentText;
		subcomponents[1] = componentTextGroups;
		subcomponents[2] = componentImages;
		updateComponent();
	}
	public abstract void updateComponent();
}
