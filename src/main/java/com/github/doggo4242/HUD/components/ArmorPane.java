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

package com.github.doggo4242.HUD.components;

import com.github.doggo4242.HUD.HUDComponentTextGroup;
import com.github.doggo4242.HUD5zig;
import com.github.doggo4242.HUD.HUDComponent;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ArmorPane extends HUDComponent {
	private static final char[] armorLetters = {'H','C','L','B'};
	private static final String[] armorStr = {"helmet","chestplate","leggings","boots"};
	private static final int defVerYPos = 66;
	private static final int defHorYPos = 23;
	private final HUDComponentTextGroup textGroup;

	public ArmorPane(){
		super();
		componentImages = null;
		componentTextGroups = new HUDComponentTextGroup[1];
		componentTextGroups[0] = new HUDComponentTextGroup(5,defYSpacing);
		textGroup = componentTextGroups[0];
	}
	@Override
	public void updateComponent() {
		if((disabled = player == null || Math.abs(HUD5zig.settings.get("ArmorEnabled")) != 1)){
			return;
		}
		//set alignment and position
		textGroup.alignment = Math.abs(HUD5zig.settings.get("ArmorAlignment")) == 1;
		int x = HUD5zig.settings.get("ArmorX");
		int y = HUD5zig.settings.get("ArmorY");
		x = (x == -1 || ((x + defYSpacing * textGroup.text.length) > scrWidth && textGroup.alignment) || (x > scrWidth && !textGroup.alignment)) ? defXPos : x;
		if(textGroup.alignment && (y == -1 || (y + defYSpacing * textGroup.text.length) > scrHeight)){
			y = defVerYPos;
		}else if(!textGroup.alignment && (y == -1 || y > scrHeight)){
			y = defHorYPos;
		}
		textGroup.x = x;
		textGroup.y = y;
		ItemStack[] armor = {player.getItemStackFromSlot(EquipmentSlotType.HEAD),
				player.getItemStackFromSlot(EquipmentSlotType.CHEST),player.getItemStackFromSlot(EquipmentSlotType.LEGS),
				player.getItemStackFromSlot(EquipmentSlotType.FEET)};
		textGroup.text[0] = red+TextFormatting.UNDERLINE+"Armor:"+TextFormatting.RESET;
		for(int i = 0;i<armor.length;i++) {
			//check for empty slots and calculate percent damage, otherwise say no armor
			int t = (!armor[i].isEmpty()&&armor[i].getMaxDamage()!=0)?(int)Math.ceil(100-(((float)armor[i].getDamage()/armor[i].getMaxDamage())*100)):0;
			textGroup.text[i+1] = (t>0)?red+armorLetters[i]+white+"> "+t+"%":"No "+armorStr[i];
		}
	}
}
