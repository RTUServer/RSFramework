package kr.rtuserver.protoweaver.api.impl.velocity;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.proxy.ProxyServer;
import kr.rtuserver.protoweaver.api.ProtoConnectionHandler;
import kr.rtuserver.protoweaver.api.callback.HandlerCallback;
import kr.rtuserver.protoweaver.api.protocol.Packet;
import kr.rtuserver.protoweaver.api.proxy.ServerSupplier;
import kr.rtuserver.protoweaver.api.util.ProtoLogger;

import java.nio.file.Path;

public interface VelocityProtoWeaver extends ProtoLogger.IProtoLogger, ServerSupplier {

    ProxyServer getServer();

    Toml getVelocityConfig();

    Path getDir();

    void registerProtocol(String namespace, String key, Packet packet, Class<? extends ProtoConnectionHandler> protocolHandler, HandlerCallback callback);
}