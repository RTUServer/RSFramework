package kr.rtuserver.framework.bukkit.api.storage.config;

import kr.rtuserver.framework.bukkit.api.RSPlugin;
import kr.rtuserver.framework.bukkit.api.config.RSConfiguration;
import lombok.Getter;

@Getter
public class JsonConfig extends RSConfiguration {
    private int savePeriod = 10;

    public JsonConfig(RSPlugin plugin) {
        super(plugin, "Configs/Storages", "Json.yml", null);
        setup(this);
    }

    private void init() {
        savePeriod = getInt("savePeriod", savePeriod);
    }


}
