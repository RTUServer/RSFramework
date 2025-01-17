package kr.rtuserver.framework.bukkit.api.nms;

import org.bukkit.Location;

import java.util.List;

public interface NMSBiome {

    /***
     * Get All Server Biome as String from Location
     * @param location
     * @return
     */
    String getBiomeKey(Location location);

    /***
     * Get All Server Biome as String
     * @return
     */
    List<String> getBiomesAsString();

    /***
     * Get All Server Biome as String using BiomeTag ( Support Versions: 1.18.1 < )
     * Available Tags: is_badlands, is_beach, is_deep_ocean, is_end, is_forest, is_hill, is_jungle, is_jungle, is_mountain, is_nether, is_ocean, is_overworld, is_savanna, is_taiga, is_river
     * @param tag check available tags
     * @return
     */
    List<String> getBiomeTag(String tag);

}
