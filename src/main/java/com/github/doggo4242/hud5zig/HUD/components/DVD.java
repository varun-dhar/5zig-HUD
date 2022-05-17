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
package com.github.doggo4242.hud5zig.HUD.components;

import com.github.doggo4242.hud5zig.HUD5zig;
import com.github.doggo4242.hud5zig.HUD.HUDComponent;
import com.github.doggo4242.hud5zig.HUD.HUDComponentImage;

public class DVD extends HUDComponent {
	private int xDir = 1;
	private int yDir = 1;

	public DVD() {
		super();
		componentImages = new HUDComponentImage[1];
		componentImages[0] = new HUDComponentImage(HUD5zig.MODID + ":dvd.png", 30, 30, 0, 0);
	}

	@Override
	public void updateComponent() {
		if ((disabled = Math.abs(HUD5zig.settings.get(HUD5zig.Options.DVD_DISABLED)) == 1)) {
			return;
		}
		final int inc = 2;
		int xBound = componentImages[0].x + inc * xDir + componentImages[0].width * ((xDir == -1) ? 0 : 1);
		int yBound = componentImages[0].y + inc * yDir + componentImages[0].height * ((yDir == -1) ? 0 : 1);
		if (xBound > scrWidth || xBound < 0) {
			xDir = -xDir;
		}
		if (yBound > scrHeight || yBound < 0) {
			yDir = -yDir;
		}
		componentImages[0].x += inc * xDir;
		componentImages[0].y += inc * yDir;
	}
}
