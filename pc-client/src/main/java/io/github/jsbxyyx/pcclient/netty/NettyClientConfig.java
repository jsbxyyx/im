package io.github.jsbxyyx.pcclient.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author
 * @since
 */
public class NettyClientConfig {

    private int clientWorkerThreads = 4;
    private String clientWorkerThreadPrefix = "NettyClientSelector";
    private Class<? extends Channel> clientChannelClazz = NioSocketChannel.class;
    private int channelMaxWriteIdleSeconds = 0;

    public int getClientSelectorThreadSize() {
        return 1;
    }

    public String getClientSelectorThreadPrefix() {
        return clientWorkerThreadPrefix;
    }

    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }

    public String getClientWorkerThreadPrefix() {
        return clientWorkerThreadPrefix;
    }

    public Class<? extends Channel> getClientChannelClazz() {
        return clientChannelClazz;
    }

    public int getConnectTimeoutMillis() {
        return 10000;
    }

    public int getClientSocketSndBufSize() {
        return 153600;
    }

    public int getClientSocketRcvBufSize() {
        return 153600;
    }

    public int getChannelMaxReadIdleSeconds() {
        return 15;
    }

    public int getChannelMaxWriteIdleSeconds() {
        return channelMaxWriteIdleSeconds;
    }

    public int getChannelMaxAllIdleSeconds() {
        return channelMaxWriteIdleSeconds;
    }
}
