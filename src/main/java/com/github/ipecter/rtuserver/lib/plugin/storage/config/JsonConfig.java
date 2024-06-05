package com.github.ipecter.rtuserver.lib.plugin.storage.config;

import com.github.ipecter.rtuserver.lib.plugin.RSPlugin;
import com.github.ipecter.rtuserver.lib.plugin.config.RSConfiguration;
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