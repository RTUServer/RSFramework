package kr.rtuserver.lib.bukkit.nms.v1_21_r1;

import kr.rtuserver.lib.bukkit.api.nms.NMS;
import kr.rtuserver.lib.bukkit.api.nms.NMSBiome;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.craftbukkit.CraftServer;

public class NMS_1_21_R1 implements NMS {

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