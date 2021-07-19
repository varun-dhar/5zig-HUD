package com.github.varun_dhar.components;

import com.github.varun_dhar.HUD5zig;
import com.github.varun_dhar.HUDComponent;
import com.github.varun_dhar.HUDComponentImage;

public class DVD extends HUDComponent {
	private int xDir = 1;
	private int yDir = 1;
	private final int inc = 2;
	public DVD(){
		componentImages = new HUDComponentImage[1];
		componentImages[0] = new HUDComponentImage(HUD5zig.MODID+":dvd.png",30,30,0,0);
	}

	@Override
	public void updateComponent() {
		if((disabled = HUD5zig.settings.get("DVDEnabled") == 0)){
			return;
		}
		int xBound = componentImages[0].x+inc*xDir+componentImages[0].width*((xDir==-1)?0:1);
		int yBound = componentImages[0].y+inc*yDir+componentImages[0].height*((yDir==-1)?0:1);
		if(xBound > scrWidth || xBound < 0){
			xDir = -xDir;
		}
		if(yBound > scrHeight || yBound < 0){
			yDir = -yDir;
		}
		componentImages[0].x += inc*xDir;
		componentImages[0].y += inc*yDir;
	}
}