package com.github.ipecter.rtuserver.lib.bukkit.api.util.platform;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SystemEnviroment {

    public static String getOS() {
        return System.getProperty("os.name");
    }

    public static String getJDKVersion() {
        return System.getProperty("java.version");
    }
}