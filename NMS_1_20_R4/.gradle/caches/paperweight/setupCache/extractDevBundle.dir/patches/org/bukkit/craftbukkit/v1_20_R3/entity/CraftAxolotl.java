package org.bukkit.craftbukkit.v1_20_R3.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.entity.Axolotl;

public class CraftAxolotl extends CraftAnimals implements Axolotl, io.papermc.paper.entity.PaperBucketable { // Paper - Bucketable API

    public CraftAxolotl(CraftServer server, net.minecraft.world.entity.animal.axolotl.Axolotl entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.axolotl.Axolotl getHandle() {
        return (net.minecraft.world.entity.animal.axolotl.Axolotl) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftAxolotl";
    }

    @Override
    public boolean isPlayingDead() {
        return this.getHandle().isPlayingDead();
    }

    @Override
    public void setPlayingDead(boolean playingDead) {
        this.getHandle().setPlayingDead(playingDead);
    }

    @Override
    public Variant getVariant() {
        return Variant.values()[this.getHandle().getVariant().ordinal()];
    }

    @Override
    public void setVariant(Variant variant) {
        Preconditions.checkArgument(variant != null, "variant");

        this.getHandle().setVariant(net.minecraft.world.entity.animal.axolotl.Axolotl.Variant.byId(variant.ordinal()));
    }
}