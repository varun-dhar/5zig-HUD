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

package com.github.varun_dhar.hud5zig.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command {
		String help();

		String alias();
	}

	private static final HashMap<String, Function<String[], MutableComponent>> commands = new HashMap<>();
	public static final HashMap<String, String> commandHelp = new HashMap<>();
	public static final HashMap<String, String> commandAliases = new HashMap<>();
	private static final String prefix = "5h";
	private static final Pattern commandPattern = Pattern.compile("/?" + prefix + " (\\w+)( .+)?");

	public CommandParser() {
		registerClassCommands(GeneralCommands.class);
		registerClassCommands(HUDCommands.class);
		registerClassCommands(NavCommands.class);
		registerClassCommands(MacroCommands.class);
		registerClassCommands(CoordinateCommands.class);
		registerClassCommands(ActionCommands.class);
	}

	public void registerClassCommands(Object o) {
		registerClassCommands(o.getClass());
	}

	@SuppressWarnings("unchecked")
	public void registerClassCommands(Class<?> c) {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(MutableComponent.class, String[].class);
		for (var method : c.getDeclaredMethods()) {
			Annotation annotation;
			if ((annotation = method.getAnnotation(Command.class)) != null) {
				try {
					MethodHandle handle = lookup.unreflect(method);
					if (handle.type().equals(methodType)) {
						var type = annotation.annotationType();
						for (var property : type.getDeclaredMethods()) {
							if (property.getName().equals("help")) {
								try {
									commandHelp.put(method.getName(), (String) property.invoke(annotation, (Object[]) (null)));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (property.getName().equals("alias")) {
								try {
									commandAliases.put((String) property.invoke(annotation, (Object[]) (null)), method.getName());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}

						CallSite site = LambdaMetafactory.metafactory(lookup,
								"apply", MethodType.methodType(Function.class), handle.type().generic(), handle, handle.type());
						MethodHandle invoker = site.getTarget();
						Function<String[], MutableComponent> fn = (Function<String[], MutableComponent>) invoker.invoke();
						commands.put(method.getName(), fn);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public void parseChat(ClientChatEvent event) {
		//find and run commands/macros
		String msg = event.getMessage();
		String arg = StringUtils.substringBefore(msg, " ");
		if (ActionCommands.actions.containsKey(msg)) {
			event.setCanceled(true);
			String[] actions = ActionCommands.actions.get(msg);
			ClientPacketListener connection = Minecraft.getInstance().getConnection();
			if (connection == null) {
				return;
			}
			ChatComponent chatGui = Minecraft.getInstance().gui.getChat();
			for (var action : actions) {
				if (commandPattern.matcher(action).matches() ||
						MacroCommands.macros.containsKey(StringUtils.substringBefore(action, " ")) ||
						ActionCommands.actions.containsKey(action)) {
					MinecraftForge.EVENT_BUS.post(new ClientChatEvent(action));
				} else {
					connection.send(new ServerboundChatPacket(action));
					chatGui.addRecentChat(action);
				}
			}
			return;
		}
		if (MacroCommands.macros.containsKey(arg)) {
			event.setMessage(MacroCommands.macros.get(arg) + msg.substring(arg.length()));
			msg = event.getMessage();
		}
		Matcher matcher = commandPattern.matcher(msg);
		if (matcher.matches()) {
			event.setCanceled(true);
			Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
			String cmd = matcher.group(1);
			if (commandAliases.containsKey(cmd)) {
				cmd = commandAliases.get(cmd);
			}
			String tmp = matcher.group(2);
			String[] args = null;
			if (tmp != null) {
				args = StringUtils.split(tmp.trim());
			}

			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null) {
				return;
			}
			Function<String[], MutableComponent> command = commands.get(cmd);
			if (command == null) {
				player.sendMessage(new TextComponent("Unknown command or invalid argument. Run /5h help for a list of commands"), Util.NIL_UUID);
				return;
			}
			MutableComponent component = command.apply(args);
			if (component != null) {
				player.sendMessage(component, Util.NIL_UUID);
			}
		}
	}
}
