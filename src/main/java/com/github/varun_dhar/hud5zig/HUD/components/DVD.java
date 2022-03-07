package com.github.varun_dhar.hud5zig.HUD.components;

import com.github.varun_dhar.hud5zig.HUD5zig;
import com.github.varun_dhar.hud5zig.HUD.HUDComponent;
import com.github.varun_dhar.hud5zig.HUD.HUDComponentImage;

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
