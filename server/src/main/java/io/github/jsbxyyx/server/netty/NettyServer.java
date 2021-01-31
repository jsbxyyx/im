package io.github.jsbxyyx.server.netty;

import io.github.jsbxyyx.common.ThreadFactoryImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author
 */
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup eventLoopGroupWorker;
    private final EventLoopGroup eventLoopGroupBoss;
    private final NettyServerConfig nettyServerConfig;
    private final Class<? extends ServerChannel> SERVER_CHANNEL_CLAZZ;

    private int listenPort;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public NettyServer(final NettyServerConfig nettyServerConfig) {
        this.serverBootstrap = new ServerBootstrap();
        this.nettyServerConfig = nettyServerConfig;
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyServerConfig.getServerWorkerThreads(),
                new ThreadFactoryImpl(nettyServerConfig.getBizThreadPrefix()));

        if (NettyServerConfig.enableEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(nettyServerConfig.getBossThreadSize(),
                    new ThreadFactoryImpl(nettyServerConfig.getBossThreadPrefix()));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
                    new ThreadFactoryImpl(nettyServerConfig.getSelectorThreadPrefix()));
            SERVER_CHANNEL_CLAZZ = EpollServerSocketChannel.class;
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(nettyServerConfig.getBossThreadSize(),
                    new ThreadFactoryImpl(nettyServerConfig.getBossThreadPrefix()));
            this.eventLoopGroupWorker = new NioEventLoopGroup(nettyServerConfig.getServerSelectorThreads(),
                    new ThreadFactoryImpl(nettyServerConfig.getSelectorThreadPrefix()));
            SERVER_CHANNEL_CLAZZ = NioServerSocketChannel.class;
        }

        listenPort = nettyServerConfig.getPort();
    }

    public void start() {
        if (initialized.get()) {
            LOGGER.warn("The server has started...");
            return;
        }
        startTcp();
        initialized.set(true);
    }

    private void startTcp() {
        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupWorker)
                .channel(SERVER_CHANNEL_CLAZZ)
                .option(ChannelOption.SO_BACKLOG, nettyServerConfig.getSoBackLogSize())
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, nettyServerConfig.getServerSocketSendBufSize())
                .childOption(ChannelOption.SO_RCVBUF, nettyServerConfig.getServerSocketResvBufSize())
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(nettyServerConfig.getWriteBufferLowWaterMark(),
                                nettyServerConfig.getWriteBufferHighWaterMark()))
                .localAddress(new InetSocketAddress(listenPort))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        if (nettyServerConfig.isLog()) {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                        }
                        ch.pipeline().addLast(new IdleStateHandler(60, 20, 0, TimeUnit.SECONDS))
                                .addLast(new HeartbeatHandler())
                                .addLast(new NettyMessageEncoder())
                                .addLast(new NettyMessageDecoder());
                        ch.pipeline().addLast(defaultEventExecutorGroup, new NettyServerHandler());
                    }
                });

        if (nettyServerConfig.isEnableServerPooledByteBufAllocator()) {
            this.serverBootstrap.childOption(ChannelOption.ALLOCATOR, NettyServerConfig.DIRECT_BYTE_BUF_ALLOCATOR);
        }

        try {
            ChannelFuture future = this.serverBootstrap.bind(listenPort).sync();
            LOGGER.info("Server started port:[{}]...", listenPort);
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void shutdown() {
        try {
            if (eventLoopGroupBoss != null) {
                eventLoopGroupBoss.shutdownGracefully();
            }
            if (eventLoopGroupWorker != null) {
                eventLoopGroupWorker.shutdownGracefully();
            }
        } catch (Exception e) {
            LOGGER.error("Server shutdown exception.", e);
        }
    }

}
