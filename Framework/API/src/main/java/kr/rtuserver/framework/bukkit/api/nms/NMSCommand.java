package kr.rtuserver.framework.bukkit.api.nms;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.util.Map;

public interface NMSCommand {

    SimpleCommandMap getCommandMap();

    Map<String, Command> getKnownCommands();

    boolean register(RSCommand<? extends RSPlugin> command);

    boolean unregister(RSCommand<? extends RSPlugin> command);

}
