package com.github.ipecter.rtuserver.lib.bukkit.api.storage.config;

import com.github.ipecter.rtuserver.lib.bukkit.api.RSPlugin;
import com.github.ipecter.rtuserver.lib.bukkit.api.config.RSConfiguration;
import lombok.Getter;

@Getter
public class PostgreSQLConfig extends RSConfiguration {
    private String host = "127.0.0.1";
    private String port = "5432";
    private String database = "";
    private String username = "";
    private String password = "";
    private String tablePrefix = getPlugin().getName() + "_";

    public PostgreSQLConfig(RSPlugin plugin) {
        super(plugin, "Configs/Storages", "PostgreSQL.yml", null);
        setup(this);
    }

    private void init() {
        host = getString("host", host);
        port = getString("port", port);
        database = getString("database", database);
        username = getString("username", username);
        password = getString("password", password);
        tablePrefix = getString("tablePrefix", tablePrefix);
    }

}
