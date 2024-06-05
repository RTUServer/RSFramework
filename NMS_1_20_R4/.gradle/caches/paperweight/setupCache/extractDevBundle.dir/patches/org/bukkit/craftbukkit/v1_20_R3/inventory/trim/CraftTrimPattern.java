package org.bukkit.craftbukkit.v1_20_R3.inventory.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.util.Handleable;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

public class CraftTrimPattern implements TrimPattern, Handleable<net.minecraft.world.item.armortrim.TrimPattern> {

    public static TrimPattern minecraftToBukkit(net.minecraft.world.item.armortrim.TrimPattern minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.TRIM_PATTERN, Registry.TRIM_PATTERN);
    }

    public static net.minecraft.world.item.armortrim.TrimPattern bukkitToMinecraft(TrimPattern bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    private final NamespacedKey key;
    private final net.minecraft.world.item.armortrim.TrimPattern handle;

    public CraftTrimPattern(NamespacedKey key, net.minecraft.world.item.armortrim.TrimPattern handle) {
        this.key = key;
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.item.armortrim.TrimPattern getHandle() {
        return this.handle;
    }

    @Override
    @NotNull
    public NamespacedKey getKey() {
        if (true) return java.util.Objects.requireNonNull(org.bukkit.Registry.TRIM_PATTERN.getKey(this), () -> this + " doesn't have a key"); // Paper
        return this.key;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        if (!(this.handle.description().getContents() instanceof TranslatableContents)) throw new UnsupportedOperationException("Description isn't translatable!"); // Paper
        return ((TranslatableContents) this.handle.description().getContents()).getKey();
    }

    // Paper start - adventure
    @Override
    public net.kyori.adventure.text.Component description() {
        return io.papermc.paper.adventure.PaperAdventure.asAdventure(this.handle.description());
    }
    // Paper end - adventure
}