/*
   Copyright 2023 Varun Dhar

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

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class CommandParser {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command {
		String help();

		String alias();
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface StringParseGreedy {
	}

	public static final HashMap<String, String> commandHelp = new HashMap<>();
	public static final HashMap<String, String> commandAliases = new HashMap<>();
	private static final String prefix = "5h";
	private static final Map<Class<?>, ArgumentType<?>> argumentTable =
			Map.of(String.class, StringArgumentType.word(), int.class, IntegerArgumentType.integer(),
					boolean.class, BoolArgumentType.bool(), double.class, DoubleArgumentType.doubleArg());
	private static LiteralArgumentBuilder<CommandSourceStack> base;

	static {
		base = Commands.literal(prefix);
	}

	private static ArgumentBuilder<CommandSourceStack, ?> methodToCommand(Parameter[] params, int param, MethodHandle handle) {
		if (param >= params.length) {
			return null;
		}
		ArgumentType<?> argumentType;
		if (params[param].getAnnotation(StringParseGreedy.class) != null) {
			argumentType = StringArgumentType.greedyString();
		} else {
			argumentType = argumentTable.get(params[param].getType());
		}
		var arg = Commands.argument(params[param].getName(), argumentType);
		if (param < params.length - 1) {
			return arg.then(methodToCommand(params, param + 1, handle));
		} else {
			return arg.executes((command) -> {
				Object[] arguments = new Object[params.length];
				for (int i = 0; i < params.length; i++) {
					arguments[i] = command.getArgument(params[i].getName(), params[i].getType());
				}
				try {
					MutableComponent ret = (MutableComponent) handle.invokeWithArguments(arguments);
					if (ret != null) {
						Minecraft.getInstance().player.sendSystemMessage(ret);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				return com.mojang.brigadier.Command.SINGLE_SUCCESS;
			});
		}
	}

	public static void registerClassCommands(Class<?> c) {
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		//MethodType methodType = MethodType.methodType(MutableComponent.class, String[].class);
		for (var method : c.getDeclaredMethods()) {
			Command annotation;
			if ((annotation = method.getAnnotation(Command.class)) != null) {
				try {
					MethodHandle handle = lookup.unreflect(method);
					commandHelp.put(method.getName(), annotation.help());
					commandAliases.put(annotation.alias(), method.getName());

					/*
					CallSite site = LambdaMetafactory.metafactory(lookup,
							"apply", MethodType.methodType(Function.class), handle.type().generic(), handle, handle.type());
					MethodHandle invoker = site.getTarget();
					Function<String[], MutableComponent> fn = (Function<String[], MutableComponent>) invoker.invoke();
					commands.put(method.getName(), fn);*/

					var subcommand = Commands.literal(method.getName());

					if (method.getParameterCount() > 0) {
						subcommand = subcommand.then(methodToCommand(method.getParameters(), 0, handle));
					} else {
						subcommand = subcommand.executes((command) -> {
							try {
								MutableComponent ret = (MutableComponent) handle.invokeExact();
								if (ret != null) {
									Minecraft.getInstance().player.sendSystemMessage(ret);
								}
							} catch (Throwable e) {
								e.printStackTrace();
							}
							return com.mojang.brigadier.Command.SINGLE_SUCCESS;
						});
					}
					var aliasedCommand = Commands.literal(annotation.alias()).requires(subcommand.getRequirement())
							.forward(subcommand.getRedirect(), subcommand.getRedirectModifier(), subcommand.isFork())
							.executes(subcommand.getCommand());
					var subcommandNode = subcommand.build();
					for (var child : subcommandNode.getChildren()) {
						aliasedCommand.then(child);
					}
					base = base.then(subcommandNode);
					base = base.then(aliasedCommand);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void addCommands(RegisterClientCommandsEvent event) {
		registerClassCommands(GeneralCommands.class);
		registerClassCommands(HUDCommands.class);
		registerClassCommands(NavCommands.class);
		registerClassCommands(MacroCommands.class);
		registerClassCommands(CoordinateCommands.class);
		registerClassCommands(ActionCommands.class);


		event.getDispatcher().register(base);
	}

	//@SubscribeEvent
	public void parseChat(ClientChatEvent event) {
		/*
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
					connection.send(new ServerboundChatPacket(new FriendlyByteBuf(Unpooled.copiedBuffer(action.getBytes(Charset.defaultCharset())))));
					chatGui.addRecentChat(action);
				}
			}
			return;
		}
		if (MacroCommands.macros.containsKey(arg)) {
			//event.setMessage(MacroCommands.macros.get(arg) + msg.substring(arg.length()));
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
				player.displayClientMessage(Component.literal("Unknown command or invalid argument. Run /5h help for a list of commands"), true);
				return;
			}
			MutableComponent component = command.apply(args);
			if (component != null) {
				player.displayClientMessage(component, true);
			}
		}
		 */
	}
}
