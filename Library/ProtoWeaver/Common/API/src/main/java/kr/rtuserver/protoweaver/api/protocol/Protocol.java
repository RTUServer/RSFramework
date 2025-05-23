package kr.rtuserver.protoweaver.api.protocol;

import kr.rtuserver.protoweaver.api.ProtoConnectionHandler;
import kr.rtuserver.protoweaver.api.ProtoWeaver;
import kr.rtuserver.protoweaver.api.auth.ClientAuthHandler;
import kr.rtuserver.protoweaver.api.auth.ServerAuthHandler;
import kr.rtuserver.protoweaver.api.callback.HandlerCallback;
import kr.rtuserver.protoweaver.api.netty.ProtoConnection;
import kr.rtuserver.protoweaver.api.protocol.internal.CustomPacket;
import kr.rtuserver.protoweaver.api.protocol.internal.GlobalPacket;
import kr.rtuserver.protoweaver.api.util.ObjectSerializer;
import kr.rtuserver.protoweaver.api.util.ProtoLogger;
import lombok.*;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 * Stores all the registered packets, settings and additional configuration of a {@link ProtoWeaver} protocol.
 */
@EqualsAndHashCode
public class Protocol {

    @EqualsAndHashCode.Exclude
    private final ObjectSerializer serializer = new ObjectSerializer();
    @Getter
    private final MessageDigest packetMD = MessageDigest.getInstance("SHA-1");
    @Getter
    private final String namespace;
    @Getter
    private final String key;
    @Getter
    private final Set<Packet> packets = new HashSet<>();
    @Getter
    private CompressionType compression = CompressionType.NONE;
    @Getter
    private int compressionLevel = -37;
    @Getter
    private int maxPacketSize = 16384;
    @Getter
    private int maxConnections = -1;
    @Getter
    private Level loggingLevel = Level.ALL;

    @EqualsAndHashCode.Exclude
    private Class<? extends ProtoConnectionHandler> serverConnectionHandler;
    @EqualsAndHashCode.Exclude
    private Class<? extends ProtoConnectionHandler> clientConnectionHandler;
    @EqualsAndHashCode.Exclude
    private Class<? extends ServerAuthHandler> serverAuthHandler;
    @EqualsAndHashCode.Exclude
    private Class<? extends ClientAuthHandler> clientAuthHandler;
    @EqualsAndHashCode.Exclude
    private HandlerCallback serverHandlerCallable;
    @EqualsAndHashCode.Exclude
    private HandlerCallback clientHandlerCallable;

    private Protocol(String namespace, String name) throws NoSuchAlgorithmException {
        this.namespace = namespace;
        this.key = name;
    }

    /**
     * <p>Creates a new protocol builder. A good rule of thumb for naming that ensures maximum compatibility is to use
     * your mod id or project id for the namespace and to give the name something unique.</p>
     * <br>For example: "protoweaver:proto-message"</br>
     *
     * @param namespace Usually should be set to your mod id or project id
     * @param name      The name of your protocol.
     */
    @SneakyThrows
    public static Builder create(@NonNull String namespace, @NonNull String name) {
        return new Builder(new Protocol(namespace, name));
    }

    public String getNamespaceKey() {
        return namespace + ":" + key;
    }

    public boolean isGlobal(Object packet) {
        return packet instanceof GlobalPacket;
    }

    /**
     * Allows you to create modify an existing {@link Protocol}. The {@link Protocol} object returned from
     * {@link Builder#build()} will be the same object as the one that this method was called on (not a copy). In
     * theory this means you can modify a protocol without reloading it, or while its currently active. Here be dragons,
     * so use with caution.
     */
    public Builder modify() {
        return new Builder(this);
    }

    @SneakyThrows
    public ProtoConnectionHandler newConnectionHandler(Side side) {
        return switch (side) {
            case CLIENT -> {
                if (clientConnectionHandler == null)
                    throw new RuntimeException("No client connection handler set for protocol: " + this);
                if (clientHandlerCallable == null) yield clientConnectionHandler.getDeclaredConstructor().newInstance();
                else
                    yield clientConnectionHandler.getDeclaredConstructor(HandlerCallback.class).newInstance(clientHandlerCallable);
            }
            case SERVER -> {
                if (serverConnectionHandler == null)
                    throw new RuntimeException("No server connection handler set for protocol: " + this);
                if (serverHandlerCallable == null) yield serverConnectionHandler.getDeclaredConstructor().newInstance();
                else
                    yield serverConnectionHandler.getDeclaredConstructor(HandlerCallback.class).newInstance(serverHandlerCallable);
            }
        };
    }

    @SneakyThrows
    public ServerAuthHandler newServerAuthHandler() {
        if (serverAuthHandler == null) throw new RuntimeException("No server auth handler set for protocol: " + this);
        return serverAuthHandler.getDeclaredConstructor().newInstance();
    }

    @SneakyThrows
    public ClientAuthHandler newClientAuthHandler() {
        if (clientAuthHandler == null) throw new RuntimeException("No client auth handler set for protocol: " + this);
        return clientAuthHandler.getDeclaredConstructor().newInstance();
    }

    public byte[] serialize(@NonNull Object packet, ProtoConnectionHandler handler) throws IllegalArgumentException {
        return serializer.serialize(packet, handler);
    }

    public Object deserialize(byte @NonNull [] packet) throws IllegalArgumentException {
        return serializer.deserialize(packet);
    }

    @SneakyThrows
    public byte[] getSHA1() {
        MessageDigest md = (MessageDigest) this.packetMD.clone();
        md.update(toString().getBytes(StandardCharsets.UTF_8));
        md.update(ByteBuffer.allocate(12)
                .putInt(compressionLevel)
                .putInt(compression.ordinal())
                .putInt(maxPacketSize)
                .array());
        return md.digest();
    }

    /**
     * @return The number of connected clients this protocol is currently serving.
     */
    public int getConnections() {
        return ProtoConnection.getConnectionCount(this);
    }

    /**
     * Determine if a side requires auth by checking to see if an auth handler was set for the given side.
     *
     * @param side The {@link Side} to check for an auth handler.
     */
    public boolean requiresAuth(@NonNull Side side) {
        if (side.equals(Side.CLIENT)) return clientAuthHandler != null;
        return serverAuthHandler != null;
    }

    public void logInfo(@NonNull String message) {
        if (loggingLevel.intValue() <= Level.INFO.intValue()) ProtoLogger.info("[" + this + "] " + message);
    }

    public void logWarn(@NonNull String message) {
        if (loggingLevel.intValue() <= Level.WARNING.intValue()) ProtoLogger.warn("[" + this + "] " + message);
    }

    public void logErr(@NonNull String message) {
        if (loggingLevel.intValue() <= Level.SEVERE.intValue()) ProtoLogger.err("[" + this + "] " + message);
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {

        private final Protocol protocol;

        /**
         * Set the packets handler that the server will use to process inbound packets.
         *
         * @param handler The class of the packets handler.
         */
        @SneakyThrows
        public Builder setServerHandler(Class<? extends ProtoConnectionHandler> handler) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor().getParameterCount() != 0)
                throw new IllegalArgumentException("Handler class must have a zero arg constructor: " + handler);
            protocol.serverConnectionHandler = handler;
            return this;
        }

        /**
         * Set the packets handler that the server will use to process inbound packets.
         *
         * @param handler The class of the packets handler.
         */
        @SneakyThrows
        public Builder setServerHandler(Class<? extends ProtoConnectionHandler> handler, HandlerCallback callable) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor(HandlerCallback.class).getParameterCount() != 1)
                throw new IllegalArgumentException("Handler class must have a zero arg constructor: " + handler);
            protocol.serverConnectionHandler = handler;
            protocol.serverHandlerCallable = callable;
            return this;
        }

        /**
         * Set the packets handler that the client will use to process inbound packets.
         *
         * @param handler The class of the packets handler.
         */
        @SneakyThrows
        public Builder setClientHandler(Class<? extends ProtoConnectionHandler> handler) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor().getParameterCount() != 0)
                throw new IllegalArgumentException("Handler class must have a zero arg constructor: " + handler);
            protocol.clientConnectionHandler = handler;
            return this;
        }

        /**
         * Set the packets handler that the client will use to process inbound packets.
         *
         * @param handler The class of the packets handler.
         */
        @SneakyThrows
        public Builder setClientHandler(Class<? extends ProtoConnectionHandler> handler, HandlerCallback callable) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor(HandlerCallback.class).getParameterCount() != 1)
                throw new IllegalArgumentException("Handler class must have a one arg constructor: " + handler);
            protocol.clientConnectionHandler = handler;
            protocol.clientHandlerCallable = callable;
            return this;
        }


        /**
         * Set the auth handler that the server will use to process inbound client secrets.
         *
         * @param handler The class of the auth handler.
         */
        @SneakyThrows
        public Builder setServerAuthHandler(Class<? extends ServerAuthHandler> handler) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor().getParameterCount() != 0)
                throw new IllegalArgumentException("Handler class must have a zero arg constructor: " + handler);
            protocol.serverAuthHandler = handler;
            return this;
        }

        /**
         * Set the auth handler that the client will use to get the secret that will be sent to the server.
         *
         * @param handler The class of the auth handler.
         */
        @SneakyThrows
        public Builder setClientAuthHandler(Class<? extends ClientAuthHandler> handler) {
            if (Modifier.isAbstract(handler.getModifiers()))
                throw new IllegalArgumentException("Handler class cannot be abstract: " + handler);
            if (handler.getDeclaredConstructor().getParameterCount() != 0)
                throw new IllegalArgumentException("Handler class must have a zero arg constructor: " + handler);
            protocol.clientAuthHandler = handler;
            return this;
        }


        /**
         * Register a class to the {@link Protocol} with proxy protocol. Does nothing if the class has already been registered.
         *
         * @param packet The packets to register.
         */
        public Builder addPacket(@NonNull Class<?> packet) {
            return addPacket(packet, true);
        }

        /**
         * Register a class to the {@link Protocol}. Does nothing if the class has already been registered.
         *
         * @param packet The packets to register.
         */
        public Builder addPacket(@NonNull Class<?> packet, boolean isBothSide) {
            protocol.packets.add(Packet.of(packet, isBothSide));
            protocol.serializer.register(packet, isBothSide);
            String type = isBothSide ? packet.getName() : CustomPacket.class.getName();
            protocol.packetMD.update(type.getBytes(StandardCharsets.UTF_8));
            return this;
        }

        /**
         * Enables compression on the {@link Protocol}. The compression type by defaults is set to {@link CompressionType#NONE}.
         *
         * @param type The type of compression to enable.
         */
        public Builder setCompression(@NonNull CompressionType type) {
            protocol.compression = type;
            return this;
        }

        /**
         * Set the compression level if compression is enabled. Be sure to check the supported level for each type of
         * compression online.
         *
         * @param level The compression level to set.
         */
        public Builder setCompressionLevel(int level) {
            protocol.compressionLevel = level;
            return this;
        }

        /**
         * Set the maximum packets size this {@link Protocol} can handle. The higher the value, the more ram will be
         * allocated when sending and receiving packets. The maximum packets size defaults to 16kb.
         *
         * @param maxPacketSize The maximum size a packets can be in bytes.
         */
        public Builder setMaxPacketSize(int maxPacketSize) {
            protocol.maxPacketSize = maxPacketSize;
            return this;
        }

        /**
         * Set the number of maximum concurrent connections this {@link Protocol} will allow. Any connections over this limit
         * will be disconnected. The maximum connections defaults to -1 and allows any number of connections.
         *
         * @param maxConnections The maximum concurrent connections.
         */
        public Builder setMaxConnections(int maxConnections) {
            protocol.maxConnections = maxConnections;
            return this;
        }

        /**
         * Sets the logging level for this {@link Protocol}.
         */
        public Builder setLoggingLevel(Level level) {
            protocol.loggingLevel = level;
            return this;
        }

        /**
         * Build the {@link Protocol}.
         *
         * @return A finished protocol that can be loaded using {@link ProtoWeaver#load(Protocol)}.
         */
        public Protocol build() {
            if (protocol.compression != CompressionType.NONE && protocol.compressionLevel == -37)
                protocol.compressionLevel = protocol.compression.getDefaultLevel();
            return protocol;
        }

        /**
         * Equivalent to calling {@link Builder#build()} and {@link ProtoWeaver#load(Protocol)}.
         *
         * @return The {@link Protocol} that was built and loaded.
         */
        public Protocol load() {
            ProtoWeaver.load(build());
            return protocol;
        }
    }
}