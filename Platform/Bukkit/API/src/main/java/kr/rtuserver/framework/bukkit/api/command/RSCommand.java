package kr.rtuserver.framework.bukkit.api.command;

import kr.rtuserver.cdi.LightDI;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.impl.TranslationConfiguration;
import kr.rtuserver.framework.bukkit.api.core.Framework;
import kr.rtuserver.framework.bukkit.api.core.config.CommonTranslation;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class RSCommand<T extends RSPlugin> extends Command {

    private final T plugin;
    private final TranslationConfiguration message;
    private final TranslationConfiguration command;
    private final String name;
    private final boolean useReload;
    private final Framework framework = LightDI.getBean(Framework.class);
    private final CommonTranslation common = framework.getCommonTranslation();
    private CommandSender sender;
    private Audience audience;

    public RSCommand(T plugin, @NotNull List<String> name) {
        this(plugin, name, false);
    }

    public RSCommand(T plugin, @NotNull String name, boolean useReload) {
        this(plugin, List.of(name), useReload);
    }

    public RSCommand(T plugin, List<String> names, boolean useReload) {
        super(names.get(0));
        this.plugin = plugin;
        this.message = plugin.getConfigurations().getMessage();
        this.command = plugin.getConfigurations().getCommand();
        this.name = names.get(0);
        if (names.size() > 1) setAliases(names);
        this.useReload = useReload;
    }

    public Player getPlayer() {
        if (sender instanceof Player player) return player;
        return null;
    }

    public boolean isOp() {
        return sender.isOp();
    }

    public boolean hasPermission(String node) {
        return sender.hasPermission(node);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        PlayerChat chat = PlayerChat.of(plugin);
        if (sender instanceof Player player) {
            Map<UUID, Integer> cooldownMap = framework.getCommandLimit().getExecuteLimit();
            int cooldown = framework.getModules().getCommandModule().getExecuteLimit();
            if (cooldown > 0) {
                if (cooldownMap.getOrDefault(player.getUniqueId(), 0) <= 0)
                    cooldownMap.put(player.getUniqueId(), cooldown);
            } else {
                chat.announce(player, framework.getCommonTranslation().getMessage("error.cooldown"));
                return true;
            }
        }
        this.sender = sender;
        this.audience = plugin.getAdventure().sender(sender);
        RSCommandData data = new RSCommandData(args);
        if (useReload) {
            if (data.equals(0, common.getCommand(sender, "reload"))) {
                if (hasPermission(plugin.getName() + ".reload")) {
                    plugin.getConfigurations().reload();
                    reload(data);
                    chat.announce(sender, common.getMessage(sender, "reload"));
                } else chat.announce(sender, common.getMessage(sender, "noPermission"));
                return true;
            }
        }
        if (!execute(data)) {
            chat.announce(sender, common.getMessage(sender, "wrongUsage"));
            if (hasPermission(plugin.getName() + ".reload"))
                chat.send(sender, String.format("<gray> - </gray>/%s %s", getName(), common.getCommand(sender, "reload")));
            wrongUsage(data);
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        this.sender = sender;
        this.audience = plugin.getAdventure().sender(sender);
        RSCommandData data = new RSCommandData(args);
        List<String> list = new ArrayList<>(tabComplete(data));
        if (data.length(1)) {
            if (useReload && hasPermission(plugin.getName() + ".reload")) list.add(common.getCommand(sender, "reload"));
        }
        return list;
    }

    protected boolean execute(RSCommandData data) {
        return false;
    }

    protected List<String> tabComplete(RSCommandData data) {
        return List.of();
    }

    protected void reload(RSCommandData data) {
    }

    protected void wrongUsage(RSCommandData data) {
    }
}
