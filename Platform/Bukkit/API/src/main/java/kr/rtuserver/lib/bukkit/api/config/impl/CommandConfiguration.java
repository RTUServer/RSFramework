package kr.rtuserver.lib.bukkit.api.config.impl;

import kr.rtuserver.lib.bukkit.api.RSPlugin;
import kr.rtuserver.lib.bukkit.api.config.RSConfiguration;
import kr.rtuserver.lib.bukkit.api.core.RSFramework;
import kr.rtuserver.lib.common.api.cdi.LightDI;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CommandConfiguration extends RSConfiguration {

    private final Map<String, Object> map = new HashMap<>();
    private final RSFramework framework = LightDI.getBean(RSFramework.class);

    public CommandConfiguration(RSPlugin plugin, String file) {
        super(plugin, "Translations/Command", "Locale_" + file + ".yml", null);
        setup(this);
    }

    private void init() {
        for (String key : getConfig().getKeys(true)) {
            if (getConfig().isString(key)) {
                String result = getString(key, "");
                if (result.isEmpty()) continue;
                map.put(key, getString(key, "error"));
            }
        }
    }

    public String get(String key) {
        return map.getOrDefault(key, "null").toString();
    }

    public String getCommon(String key) {
        return framework.getCommonTranslation().getMessage(key);
    }
}
