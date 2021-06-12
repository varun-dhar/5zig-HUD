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

package com.github.doggo4242.components;

import com.github.doggo4242.HUD5zig;
import com.github.doggo4242.HUDComponent;
import com.github.doggo4242.HUDComponentText;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ArmorPane extends HUDComponent {
	private static final char[] armorLetters = {'H','C','L','B'};
	private static final String[] armorStr = {"helmet","chestplate","leggings","boots"};
	private static final int defVerYPos = 66;
	private static final int defHorYPos = 23;

	public ArmorPane(){
		componentImages = null;
		componentText = new HUDComponentText[5];
	}
	@Override
	public void updateComponent() {
		if(player == null){
			return;
		}
		//set alignment and position
		alignment = Math.abs(HUD5zig.settings.get("ArmorAlignment")) == 1;
		int x = HUD5zig.settings.get("ArmorX");
		int y = HUD5zig.settings.get("ArmorY");
		ItemStack[] armor = {player.getItemStackFromSlot(EquipmentSlotType.HEAD),
				player.getItemStackFromSlot(EquipmentSlotType.CHEST),player.getItemStackFromSlot(EquipmentSlotType.LEGS),
				player.getItemStackFromSlot(EquipmentSlotType.FEET)};
		componentText[0].text = red+TextFormatting.UNDERLINE+"Armor:";
		for(int i = 1;i<componentText.length;i++) {
			//check for empty slots and calculate damage in percent, otherwise say no armor
			int t = (!armor[i].isEmpty()&&armor[i].getMaxDamage()!=0)?(int)Math.ceil(100-(((float)armor[i].getDamage()/armor[i].getMaxDamage())*100)):0;
			componentText[i].text = (t>0)?red+armorLetters[i]+white+"> "+t+"%":"No "+armorStr[i];
		}
		x = (x == -1 || ((x + defYSpacing * componentText.length) > scrWidth && alignment) || (x > scrWidth && !alignment)) ? defXPos : x;
		if(alignment && (y == -1 || (y + defYSpacing * componentText.length) > scrHeight)){
			y = defVerYPos;
		}else if(!alignment && (y == -1 || y > scrHeight)){
			y = defHorYPos;
		}
		for (HUDComponentText text : componentText) {
			text.x = x;
			text.y = y;
			if(alignment) {
				y += defYSpacing;
			}
		}
	}
}
