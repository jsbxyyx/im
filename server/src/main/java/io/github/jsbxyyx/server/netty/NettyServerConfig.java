package io.github.jsbxyyx.server.netty;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.epoll.Epoll;
import io.netty.util.internal.PlatformDependent;

/**
 * @author
 */
public class NettyServerConfig {

    public static final PooledByteBufAllocator DIRECT_BYTE_BUF_ALLOCATOR =
            new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());

    public static final String PROPERTY_PREFIX = "netty.server";

    private static final int DEFAULT_LISTEN_PORT = 20669;
    private static final String DEFAULT_BOSS_THREAD_PREFIX = "NettyBoss";
    private static final String EPOLL_SELECTOR_THREAD_PREFIX = "NettyServerEPollSelector";
    private static final String NIO_SELECTOR_THREAD_PREFIX = "NettyServerNIOSelector";
    private static final String DEFAULT_EXECUTOR_THREAD_PREFIX = "NettyServerBizHandler";
    private static final int DEFAULT_BOSS_THREAD_SIZE = 1;

    private boolean log = true;
    private int serverSelectorThreads = 8;
    private int serverSocketSendBufSize = 153600;
    private int serverSocketResvBufSize = 153600;
    private int serverWorkerThreads = 16 + Runtime.getRuntime().availableProcessors() * 2;
    private int soBackLogSize = 1024;
    private int writeBufferHighWaterMark = 67108864;
    private int writeBufferLowWaterMark = 1048576;
    private boolean enableServerPooledByteBufAllocator = true;
    private int port = DEFAULT_LISTEN_PORT;


    public static boolean enableEpoll() {
        return Epoll.isAvailable();
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public String getBossThreadPrefix() {
        return DEFAULT_BOSS_THREAD_PREFIX;
    }

    public int getBossThreadSize() {
        return DEFAULT_BOSS_THREAD_SIZE;
    }

    public String getSelectorThreadPrefix() {
        return enableEpoll() ? EPOLL_SELECTOR_THREAD_PREFIX : NIO_SELECTOR_THREAD_PREFIX;
    }

    public String getBizThreadPrefix() {
        return DEFAULT_EXECUTOR_THREAD_PREFIX;
    }

    public int getPort() {
        return port;
    }

    public int getServerSelectorThreads() {
        return serverSelectorThreads;
    }

    public void setServerSelectorThreads(int serverSelectorThreads) {
        this.serverSelectorThreads = serverSelectorThreads;
    }

    public int getServerSocketSendBufSize() {
        return serverSocketSendBufSize;
    }

    public void setServerSocketSendBufSize(int serverSocketSendBufSize) {
        this.serverSocketSendBufSize = serverSocketSendBufSize;
    }

    public int getServerSocketResvBufSize() {
        return serverSocketResvBufSize;
    }

    public void setServerSocketResvBufSize(int serverSocketResvBufSize) {
        this.serverSocketResvBufSize = serverSocketResvBufSize;
    }

    public int getServerWorkerThreads() {
        return serverWorkerThreads;
    }

    public void setServerWorkerThreads(int serverWorkerThreads) {
        this.serverWorkerThreads = serverWorkerThreads;
    }

    public int getSoBackLogSize() {
        return soBackLogSize;
    }

    public void setSoBackLogSize(int soBackLogSize) {
        this.soBackLogSize = soBackLogSize;
    }

    public int getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public int getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public boolean isEnableServerPooledByteBufAllocator() {
        return enableServerPooledByteBufAllocator;
    }

    public void setEnableServerPooledByteBufAllocator(boolean enableServerPooledByteBufAllocator) {
        this.enableServerPooledByteBufAllocator = enableServerPooledByteBufAllocator;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
