package kr.rtuserver.framework.bukkit.core.modules;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import lombok.Getter;

@Getter
public class Modules implements kr.rtuserver.framework.bukkit.api.core.modules.Modules {

    private final CommandModule commandModule;
    private final ThemeModule themeModule;

    public Modules(RSPlugin plugin) {
        commandModule = new CommandModule(plugin);
        themeModule = new ThemeModule(plugin);
    }

    public void reload() {
        commandModule.reload();
        themeModule.reload();
    }
}