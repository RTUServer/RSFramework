package kr.rtuserver.framework.bukkit.api.inventory;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.impl.SettingConfiguration;
import kr.rtuserver.framework.bukkit.api.config.impl.TranslationConfiguration;
import kr.rtuserver.framework.bukkit.api.utility.format.ComponentFormatter;
import kr.rtuserver.framework.bukkit.api.utility.platform.MinecraftVersion;
import kr.rtuserver.framework.bukkit.api.utility.player.PlayerChat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


public abstract class RSInventory<T extends RSPlugin> implements InventoryHolder {

    @Getter
    protected final T plugin;
    protected final SettingConfiguration setting;
    protected final TranslationConfiguration message;
    protected final TranslationConfiguration command;
    protected final PlayerChat chat;

    public RSInventory(T plugin) {
        this.plugin = plugin;
        this.setting = plugin.getConfigurations().getSetting();
        this.message = plugin.getConfigurations().getMessage();
        this.command = plugin.getConfigurations().getCommand();
        this.chat = PlayerChat.of(plugin);
    }

    protected Inventory createInventory(InventoryType type, Component title) {
        if (MinecraftVersion.isPaper()) return Bukkit.createInventory(this, type, title);
        else return Bukkit.createInventory(this, type, ComponentFormatter.legacy(title));
    }

    protected Inventory createInventory(int size, Component component) {
        if (MinecraftVersion.isPaper()) return Bukkit.createInventory(this, size, component);
        else return Bukkit.createInventory(this, size, ComponentFormatter.legacy(component));
    }

    public boolean onClick(Event<InventoryClickEvent> event, Click click) {
        return true;
    }

    public boolean onDrag(Event<InventoryDragEvent> event, Drag drag) {
        return true;
    }

    public void onClose(Event<InventoryCloseEvent> event, Close close) {
    }

    public record Event<T extends InventoryEvent>(T event, Inventory inventory, Player player, boolean isInventory) {
    }

    public record Drag(Map<Integer, ItemStack> items, ItemStack cursor, ItemStack oldCursor, DragType type) {
    }

    public record Click(int slot, InventoryType.SlotType slotType, ClickType type) {
    }

    public record Close(InventoryCloseEvent.Reason reason) {
    }
}