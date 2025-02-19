package kr.rtuserver.framework.bukkit.api.command;

import kr.rtuserver.cdi.LightDI;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.impl.TranslationConfiguration;
import kr.rtuserver.framework.bukkit.api.core.Framework;
import kr.rtuserver.framework.bukkit.api.core.config.CommonTranslation;
import kr.rtuserver.framework.bukkit.api.core.modules.ThemeModule;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@ToString
public abstract class RSCommand<T extends RSPlugin> extends Command {

    private final T plugin;
    private final TranslationConfiguration message;
    private final TranslationConfiguration command;

    private final Framework framework = LightDI.getBean(Framework.class);
    private final CommonTranslation common = framework.getCommonTranslation();
    private final PlayerChat chat;
    private final Map<String, RSCommand<? extends RSPlugin>> commands = new HashMap<>();
    private CommandSender sender;
    private Audience audience;

    @Setter(AccessLevel.PRIVATE)
    private RSCommand<? extends RSPlugin> parent = null;

    public RSCommand(T plugin, @NotNull String name) {
        this(plugin, List.of(name), PermissionDefault.TRUE);
    }

    public RSCommand(T plugin, @NotNull List<String> names) {
        this(plugin, names, PermissionDefault.TRUE);
    }

    public RSCommand(T plugin, @NotNull String name, PermissionDefault permission) {
        this(plugin, List.of(name), permission);
    }

    public RSCommand(T plugin, List<String> names, PermissionDefault permission) {
        super(names.getFirst());
        this.plugin = plugin;
        this.message = plugin.getConfigurations().getMessage();
        this.command = plugin.getConfigurations().getCommand();
        this.chat = PlayerChat.of(plugin);
        Permission perm = new Permission(getPlugin().getName() + ".command." + getName(), permission);
        Bukkit.getPluginManager().addPermission(perm);
        super.setPermission(perm.getName());
        if (names.size() > 1) super.setAliases(names.subList(1, names.size()));
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
        RSCommand<? extends RSPlugin> sub = findCommand(data.args(0));
        if (sub == null) {
            if (hasPermission(getPermission())) {
                if (!execute(data)) wrongUsage();
            } else chat.announce(sender, common.getMessage(getPlayer(), "noPermission"));
        } else {
            if (sub.getName().equalsIgnoreCase("reload")) reload(data);
            if (hasPermission(sub.getPermission())) {
                if (!sub.execute(sender, commandLabel, args)) sub.wrongUsage();
            } else chat.announce(sender, common.getMessage(getPlayer(), "noPermission"));
        }
        return true;
    }

    private void wrongUsage() {
        chat.announce(sender, common.getMessage(getPlayer(), "wrongUsage"));
        ThemeModule module = framework.getModules().getThemeModule();
        List<RSCommand<? extends RSPlugin>> list = new ArrayList<>(commands.values());
        if (list.isEmpty()) return;
        StringBuilder builder = new StringBuilder("<gradient:" + module.getGradientStart() + ":" + module.getGradientEnd() + ">");
        for (int i = 0; i < list.size(); i++) {
            RSCommand<? extends RSPlugin> cmd = list.get(i);
            if (!hasPermission(cmd.getPermission())) continue;
            String usage = cmd.getLocalizedUsage(getPlayer());
            if (usage.isEmpty()) usage = "/" + cmd.getLocalizedCommand(getPlayer());
            if (i > 0) builder.append("\n");
            String description = cmd.getLocalizedDescription(getPlayer());

            builder.append(" ⏵ <white>").append(usage).append("</white>");
            if (!description.isEmpty())
                builder.append("\n    ┗ ").append("<gray>").append(description).append("</gray>");
        }
        builder.append("</gradient>");
        chat.send(sender, builder.toString());
    }

    public void registerCommand(RSCommand<? extends RSPlugin> command) {
        command.setParent(this);
        commands.put(command.getName(), command);
    }

    private RSCommand<? extends RSPlugin> findCommand(String name) {
        if (name.isEmpty()) return null;
        for (RSCommand<? extends RSPlugin> sub : commands.values()) {
            if (sub.getLocalizedName(getPlayer()).equals(name)) return sub;
        }
        return null;
    }

    protected String getLocalizedName(Player player) {
        return getCommand().get(player, getName() + ".name");
    }

    protected String getLocalizedDescription(Player player) {
        return getCommand().get(player, getDescription());
    }

    protected String getLocalizedUsage(Player player) {
        return getCommand().get(player, getUsage());
    }

    private String getLocalizedCommand(Player player) {
        if (parent == null) return getName();
        else return parent.getLocalizedCommand(player) + " " + getLocalizedName(player);
    }

    @NotNull
    @Override
    public String getDescription() {
        return getName() + ".description";
    }

    @NotNull
    @Override
    public String getUsage() {
        return getName() + ".usage";
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        this.sender = sender;
        this.audience = plugin.getAdventure().sender(sender);
        RSCommandData data = new RSCommandData(args);
        List<String> list = new ArrayList<>();

        if (data.length(1)) for (RSCommand<? extends RSPlugin> cmd : commands.values()) {
            if (hasPermission(cmd.getPermission())) list.add(cmd.getLocalizedName(getPlayer()));
        }
        RSCommand<? extends RSPlugin> sub = findCommand(data.args(0));
        if (sub == null) {
            if (hasPermission(getPermission())) list.addAll(tabComplete(data));
        } else if (hasPermission(sub.getPermission())) list.addAll(sub.tabComplete(sender, alias, args));
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
}
