/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_19_R3.block.impl;

public final class CraftCandleCake extends org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData implements org.bukkit.block.data.Lightable {

    public CraftCandleCake() {
        super();
    }

    public CraftCandleCake(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_19_R3.block.data.CraftLightable

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty LIT = getBoolean(net.minecraft.world.level.block.CandleCakeBlock.class, "lit");

    @Override
    public boolean isLit() {
        return get(CraftCandleCake.LIT);
    }

    @Override
    public void setLit(boolean lit) {
        set(CraftCandleCake.LIT, lit);
    }
}