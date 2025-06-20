package kr.rtuserver.framework.bukkit.api.integration;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import kr.rtuserver.cdi.LightDI;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.configuration.translation.TranslationConfiguration;
import kr.rtuserver.framework.bukkit.api.configuration.translation.command.CommandTranslation;
import kr.rtuserver.framework.bukkit.api.configuration.translation.message.MessageTranslation;
import kr.rtuserver.framework.bukkit.api.core.Framework;
import kr.rtuserver.framework.bukkit.api.player.PlayerChat;
import lombok.Getter;
import org.bukkit.entity.Player;

public class RSPacketListener<T extends RSPlugin> extends PacketAdapter implements Integration {

    @Getter
    private final T plugin;

    private final MessageTranslation message;
    private final CommandTranslation command;
    private final Framework framework = LightDI.getBean(Framework.class);
    private final PlayerChat chat;

    public RSPacketListener(T plugin, AdapterParameteters parameters) {
        super(parameters.plugin(plugin));
        this.plugin = plugin;
        this.message = plugin.getConfigurations().getMessage();
        this.command = plugin.getConfigurations().getCommand();
        this.chat = PlayerChat.of(plugin);
    }

    protected TranslationConfiguration message() {
        return message;
    }

    protected TranslationConfiguration command() {
        return command;
    }

    protected Framework framework() {
        return framework;
    }

    protected PlayerChat chat() {
        return chat;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        if (player != null) chat.setReceiver(player);
        send(event);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (player != null) chat.setReceiver(player);
        receive(event);
    }

    public void send(PacketEvent event) {
    }

    public void receive(PacketEvent event) {
    }

    @Override
    public boolean isAvailable() {
        return plugin.getFramework().isEnabledDependency("ProtocolLib");
    }

    @Override
    public boolean register() {
        if (isAvailable()) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this);
            return true;
        } else return false;
    }

    @Override
    public boolean unregister() {
        if (isAvailable()) {
            ProtocolLibrary.getProtocolManager().removePacketListener(this);
            return true;
        } else return false;
    }

}
