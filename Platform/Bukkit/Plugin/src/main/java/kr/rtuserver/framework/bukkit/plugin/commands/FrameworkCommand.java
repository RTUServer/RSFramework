package kr.rtuserver.framework.bukkit.plugin.commands;

import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import kr.rtuserver.framework.bukkit.plugin.RSFramework;
import kr.rtuserver.framework.bukkit.plugin.commands.framework.BroadcastCommand;
import kr.rtuserver.framework.bukkit.plugin.commands.framework.InfoCommand;

public class FrameworkCommand extends RSCommand<RSFramework> {


    public FrameworkCommand(RSFramework plugin) {
        super(plugin, "rsf");
        registerCommand(new BroadcastCommand(plugin));
        registerCommand(new InfoCommand(plugin));
    }

    @Override
    public void reload(RSCommandData data) {
        getFramework().getModules().reload();
    }
}
