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

package com.github.varun_dhar;

import com.github.varun_dhar.commands.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: add actions

public class CommandParser {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command{
		String help();
		String alias();
	}

	public static HashMap<String, Function<String[], IFormattableTextComponent>> commands = new HashMap<>();
	public static HashMap<String,String> commandHelp = new HashMap<>();
	public static HashMap<String,String> commandAliases = new HashMap<>();
	private final static String prefix = "5h";
	private static final Pattern commandPattern = Pattern.compile("/?"+prefix+" (\\w+)( .+)?");

	public CommandParser(){
		registerClassCommands(GeneralCommands.class);
		registerClassCommands(HUDCommands.class);
		registerClassCommands(NavCommands.class);
		registerClassCommands(new MacroCommands());
		registerClassCommands(new CoordinateCommands());
		registerClassCommands(new ActionCommands());
	}

	public void registerClassCommands(Object o){
		registerClassCommands(o.getClass());
	}
	@SuppressWarnings("unchecked")
	public void registerClassCommands(Class<?> c){
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodType methodType = MethodType.methodType(IFormattableTextComponent.class,String[].class);
		for(Method method : c.getDeclaredMethods()){
			if(method.isAnnotationPresent(Command.class)){
				Annotation annotation = method.getAnnotation(Command.class);
				if(annotation != null) {
					try {
						MethodHandle handle = lookup.unreflect(method);
						if(handle.type().equals(methodType)) {
							Class<? extends Annotation> type = annotation.annotationType();
							for (Method property : type.getDeclaredMethods()) {
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
									"apply", MethodType.methodType(Function.class), handle.type().generic(), handle,handle.type());
							MethodHandle invoker = site.getTarget();
							Function<String[],IFormattableTextComponent> fn = (Function<String[], IFormattableTextComponent>)invoker.invoke();
							commands.put(method.getName(), fn);
						}
					} catch (Throwable t){
						t.printStackTrace();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void parseChat(ClientChatEvent event)
	{
		//find and run commands/macros
		String msg = event.getMessage();
		String[] args = msg.split(" ");
		if(ActionCommands.actions.containsKey(msg)){
			event.setCanceled(true);
			String[] actions = ActionCommands.actions.get(msg);
			ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
			if(connection == null){
				return;
			}
			for(String action : actions){
				if(commandPattern.matcher(action).matches() || MacroCommands.macros.containsKey(action) ||
						ActionCommands.actions.containsKey(action)){
					MinecraftForge.EVENT_BUS.post(new ClientChatEvent(action));
				}else{
					connection.sendPacket(new CChatMessagePacket(action));
				}
			}
			return;
		}
		if(MacroCommands.macros.containsKey(args[0]))
		{
			event.setMessage(MacroCommands.macros.get(args[0]+msg.substring(args[0].length())));
			msg = event.getMessage();
		}
		Matcher matcher = commandPattern.matcher(msg);
		if(matcher.matches())
		{
			event.setCanceled(true);
			Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
			String cmd = matcher.group(1);
			if(commandAliases.containsKey(cmd)){
				cmd = commandAliases.get(cmd);
			}
			String tmp = matcher.group(2);
			if(tmp != null) {
				args = tmp.trim().split(" ");
			}else{
				args = null;
			}

			ClientPlayerEntity player = Minecraft.getInstance().player;
			if(player == null){
				return;
			}
			Function<String[],IFormattableTextComponent> command = commands.get(cmd);
			if(command == null)
			{
				player.sendMessage(new StringTextComponent("Unknown command or invalid argument. Run /5h help for a list of commands"), Util.DUMMY_UUID);
				return;
			}
			IFormattableTextComponent component = command.apply(args);
			if(component != null){
				player.sendMessage(component,Util.DUMMY_UUID);
			}
			if(args != null)
				player.sendMessage(new StringTextComponent("cmd: "+cmd+" "+String.join(" ",args)),Util.DUMMY_UUID);
		}
	}
}