package com.github.varun_dhar;

import com.github.varun_dhar.commands.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatEvent;
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

// TODO: add actions

public class CommandParser {
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Command{
		String help();
		String alias();
	}

	public static HashMap<String, Function<String[], IFormattableTextComponent>> commands = new HashMap<>();
	public static HashMap<String,String> commandHelp = new HashMap<>();

	private final static String prefix = "5h";

	public CommandParser(){
		registerClassCommands(GeneralCommands.class);
		registerClassCommands(HUDCommands.class);
		registerClassCommands(NavCommands.class);
		registerClassCommands(new MacroCommands());
		registerClassCommands(new CoordinateCommands());
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
				Class<? extends Annotation> type = annotation.annotationType();
				for(Method property : type.getDeclaredMethods()){
					if(property.getName().equals("help")){
						try {
							commandHelp.put(method.getName(), (String)property.invoke(annotation, (Object[]) (null)));
						}catch(Exception e){
							e.printStackTrace();
						}
					}else if(property.getName().equals("alias")){
						try {
							MacroCommands.macros.put((String)property.invoke(annotation, (Object[]) (null)),prefix+" "+method.getName());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				try {
					MethodHandle handle = lookup.unreflect(method);
					if(handle.type().equals(methodType)) {
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

	@SubscribeEvent
	public void parseChat(ClientChatEvent event)
	{
		//find and run commands/macros
		String msg = event.getMessage();
		if(ActionCommands.actions.containsKey(msg)){
			event.setCanceled(true);
			String[] actions = ActionCommands.actions.get(msg);
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if(player == null){
				return;
			}
			for(String action : actions){
				player.sendMessage(new StringTextComponent(action),PlayerEntity.getUUID(player.getGameProfile()));
			}
			return;
		}
		if(MacroCommands.macros.containsKey(msg))
		{
			String macro = msg.split(" ")[0];
			event.setMessage(MacroCommands.macros.get(macro)+msg.substring(msg.indexOf(macro)+macro.length()));
			msg = event.getMessage();
			System.out.println(event.getMessage());
		}
		msg = (msg.charAt(0) == '/')?msg.substring(1):msg;
		if(msg.startsWith(prefix))
		{
			event.setCanceled(true);
			Minecraft.getInstance().ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
			msg = (msg.length()>=3)?msg.substring(3):msg;//remove "5h "
			String[] args = msg.split(" ");
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if(player == null){
				return;
			}
			player.sendMessage(new StringTextComponent(String.join(" ",commands.keySet())),Util.DUMMY_UUID);
			Function<String[],IFormattableTextComponent> command = commands.get(args[0]);
			if(command == null)
			{
				player.sendMessage(new StringTextComponent("Unknown command or invalid argument. Run /5h help for a list of commands"), Util.DUMMY_UUID);
				return;
			}
			IFormattableTextComponent component = command.apply(args);
			if(component != null){
				player.sendMessage(component,Util.DUMMY_UUID);
			}
		}
	}
}
