package org.bukkit.craftbukkit.v1_19_R2.block.data;

import org.bukkit.block.data.Hangable;

public abstract class CraftHangable extends CraftBlockData implements Hangable {

    private static final net.minecraft.world.level.block.state.properties.BooleanProperty HANGING = getBoolean("hanging");

    @Override
    public boolean isHanging() {
        return get(CraftHangable.HANGING);
    }

    @Override
    public void setHanging(boolean hanging) {
        set(CraftHangable.HANGING, hanging);
    }
}