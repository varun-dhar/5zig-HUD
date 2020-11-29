# 5zig-HUD
A simple HUD mod for 1.16 inspired by [The5zigMod](https://github.com/5zig-reborn/The-5zig-Mod).

Includes coordinates, FPS, direction, armor durability, after-death item despawn timer, and death location coordinates.

# Demo
![HUD Demo](https://github.com/varun-dhar/5zig-HUD/raw/main/demo.png)

# Usage
To use, simply place the mod in the mods folder and launch Forge. After the first run, a configuration file for 5zig-HUD will be created in the mods folder. Using this file, you can customize the features of the mod. To load new changes, the mod must be reloaded by exiting Minecraft and relaunching. Further usage instructions can be found [here](https://github.com/varun-dhar/5zig-HUD/wiki)

# Side notes
The death location coordinates will be overwritten if one dies before the timer elapses. In a later release, I will ensure that the timer must elapse before overwriting the coordinates. In addition, after the timer elapses, the coordinates will disappear, so I'd recommend typing them in chat on the off chance that your items are still at the place of your death after 5 minutes. In a later release, I will add a command to dump coordinates to chat, however, as of now, API limitations prevent me from adding commands due to Mojang switching to Brigadier for command parsing. 

Feature requests can be submitted on the issues page.
Contributions are appreciated.

# TODO:
Add custom colors for HUD
