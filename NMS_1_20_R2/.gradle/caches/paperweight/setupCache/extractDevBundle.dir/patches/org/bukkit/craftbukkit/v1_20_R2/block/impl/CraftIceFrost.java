/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.v1_20_R2.block.impl;

public final class CraftIceFrost extends org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData implements org.bukkit.block.data.Ageable {

    public CraftIceFrost() {
        super();
    }

    public CraftIceFrost(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.v1_20_R2.block.data.CraftAgeable

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty AGE = getInteger(net.minecraft.world.level.block.FrostedIceBlock.class, "age");

    @Override
    public int getAge() {
        return this.get(CraftIceFrost.AGE);
    }

    @Override
    public void setAge(int age) {
        this.set(CraftIceFrost.AGE, age);
    }

    @Override
    public int getMaximumAge() {
        return getMax(CraftIceFrost.AGE);
    }
}