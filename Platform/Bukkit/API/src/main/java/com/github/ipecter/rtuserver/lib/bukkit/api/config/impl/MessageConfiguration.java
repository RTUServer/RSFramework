package com.github.ipecter.rtuserver.lib.bukkit.api.config.impl;

import com.github.ipecter.rtuserver.lib.bukkit.api.RSPlugin;
import com.github.ipecter.rtuserver.lib.bukkit.api.config.RSConfiguration;
import com.github.ipecter.rtuserver.lib.bukkit.api.core.RSFramework;
import com.github.ipecter.rtuserver.lib.bukkit.api.utility.format.ComponentFormatter;
import com.google.inject.Inject;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MessageConfiguration extends RSConfiguration {

    private final Map<String, Object> map = new HashMap<>();
    @Inject
    private RSFramework framework;

    public MessageConfiguration(RSPlugin plugin, String file) {
        super(plugin, "Translations/Message", "Locale_" + file + ".yml", null);
        setup(this);
    }

    private void init() {
        getString("prefix", "<gradient:#00f260:#057eff>Festival » </gradient>");
        for (String key : getConfig().getKeys(true)) {
            if (getConfig().isString(key)) {
                map.put(key, getString(key, ""));
            }
        }
    }

    public Component getPrefix() {
        String prefix = get("prefix");
        if (prefix.isEmpty()) return getPlugin().getPrefix();
        else return ComponentFormatter.mini(prefix);
    }

    public String get(String key) {
        return map.getOrDefault(key, "").toString();
    }

    public String getCommon(String key) {
        return framework.getCommonTranslation().getMessage(key);
    }
}