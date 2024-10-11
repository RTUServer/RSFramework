package kr.rtuserver.lib.bukkit.api.utility.format;

import kr.rtuserver.lib.bukkit.api.core.RSFramework;
import kr.rtuserver.lib.common.api.cdi.LightDI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentFormatter {

    static RSFramework framework;

    static RSFramework framework() {
        if (framework == null) framework = LightDI.getBean(RSFramework.class);
        return framework;
    }

    public static Component mini(String miniMessage) {
        return MiniMessage.miniMessage().deserialize(miniMessage);
    }

    public static Component parse(String msg) {
        return parse(null, msg);
    }

    public static Component parse(CommandSender sender, String miniMessage) {
        return mini(framework().isEnabledDependency("PlaceholderAPI") ? PlaceholderAPI.setPlaceholders((sender instanceof Player player) ? player : null, miniMessage) : miniMessage);
    }

    public static String legacy(Component component) {
        return legacy(LegacyComponentSerializer.SECTION_CHAR, component);
    }

    public static String legacy(char legacyCharacter, Component component) {
        return LegacyComponentSerializer.legacy(legacyCharacter).serialize(component);
    }

    public static Component system(CommandSender sender, String miniMessage) {
        Component lore = parse(sender, framework().getModules().getThemeModule().getSystemMessage());
        return parse(miniMessage).hoverEvent(HoverEvent.showText(lore));
    }

    public static Component system(CommandSender sender, Component component) {
        Component lore = parse(sender, framework().getModules().getThemeModule().getSystemMessage());
        return component.hoverEvent(HoverEvent.showText(lore));
    }


}
