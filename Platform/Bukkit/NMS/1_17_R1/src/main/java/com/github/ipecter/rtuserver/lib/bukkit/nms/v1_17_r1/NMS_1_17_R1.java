package com.github.ipecter.rtuserver.lib.bukkit.nms.v1_17_r1;

import com.github.ipecter.rtuserver.lib.bukkit.api.nms.NMS;
import com.github.ipecter.rtuserver.lib.bukkit.api.nms.NMSBiome;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

public class NMS_1_17_R1 implements NMS {

    private final NMSBiome biome = new Biome();

    @Override
    public NMSBiome biome() {
        return biome;
    }

    @Override
    public CommandMap commandMap() {
        return ((CraftServer) Bukkit.getServer()).getCommandMap();
    }
}
