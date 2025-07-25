package kr.rtuserver.protoweaver.bukkit.core;

import kr.rtuserver.protoweaver.api.ProtoConnectionHandler;
import kr.rtuserver.protoweaver.api.callback.HandlerCallback;
import kr.rtuserver.protoweaver.api.netty.ProtoConnection;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "RSF/ProtoHandler")
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class BukkitProtoHandler implements ProtoConnectionHandler {

    private static ProtoConnection proxy;
    private final HandlerCallback callable;

    public static ProtoConnection getProxy() {
        if (proxy == null || !proxy.isOpen()) return null;
        return proxy;
    }

    @Override
    public void onReady(ProtoConnection protoConnection) {
        log.info("Connected to Proxy");
        log.info("┠ Address: {}", protoConnection.getRemoteAddress());
        log.info("┖ Protocol: {}", protoConnection.getProtocol().getNamespaceKey());
        if (callable != null) callable.onReady(protoConnection);
        proxy = protoConnection;
    }

    @Override
    public void handlePacket(ProtoConnection protoConnection, Object packet) {
        if (callable != null) callable.handlePacket(protoConnection, packet);
    }

}
