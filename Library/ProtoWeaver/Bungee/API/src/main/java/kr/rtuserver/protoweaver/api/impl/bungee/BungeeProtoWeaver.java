package kr.rtuserver.protoweaver.api.impl.bungee;

import kr.rtuserver.protoweaver.api.ProtoConnectionHandler;
import kr.rtuserver.protoweaver.api.callback.HandlerCallback;
import kr.rtuserver.protoweaver.api.protocol.Packet;
import kr.rtuserver.protoweaver.api.protocol.Protocol;
import kr.rtuserver.protoweaver.api.proxy.ServerSupplier;
import kr.rtuserver.protoweaver.api.util.ProtoLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;

import java.nio.file.Path;

public interface BungeeProtoWeaver extends Listener, ProtoLogger.IProtoLogger, ServerSupplier {

    ProxyServer getServer();

    Protocol.Builder getProtocol();

    Path getDir();

    void disable();

    void registerProtocol(String namespace, String key, Packet packet, Class<? extends ProtoConnectionHandler> protocolHandler, HandlerCallback callback);
}