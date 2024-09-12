package me.mrnavastar.protoweaver.impl.bukkit.api.nms;

import me.mrnavastar.protoweaver.api.util.ProtoLogger;

public interface IProtoWeaver extends ProtoLogger.IProtoLogger {
    boolean isModernProxy();

    default boolean isPaper() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
