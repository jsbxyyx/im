package io.github.jsbxyyx.pcclient.netty;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.MsgFuture;
import io.github.jsbxyyx.common.ThreadFactoryImpl;
import io.github.jsbxyyx.msg.HeartbeatMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.type.MsgTypeProcessorFactory;
import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author
 * @since
 */
public class NettyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    protected final ConcurrentHashMap<Integer, MsgFuture> futures = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private final NettyClientConfig nettyClientConfig;
    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker;
    private EventExecutorGroup defaultEventExecutorGroup;

    public NettyClient(NettyClientConfig nettyClientConfig) {
        this.nettyClientConfig = nettyClientConfig;
        int selectorThreadSizeThreadSize = this.nettyClientConfig.getClientSelectorThreadSize();
        this.eventLoopGroupWorker = new NioEventLoopGroup(selectorThreadSizeThreadSize,
                new ThreadFactoryImpl(this.nettyClientConfig.getClientSelectorThreadPrefix()));
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(nettyClientConfig.getClientWorkerThreads(),
                new ThreadFactoryImpl(nettyClientConfig.getClientWorkerThreadPrefix()));
    }

    public void start() {
        this.bootstrap.group(this.eventLoopGroupWorker).channel(
                nettyClientConfig.getClientChannelClazz()).option(
                ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true).option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS, nettyClientConfig.getConnectTimeoutMillis()).option(
                ChannelOption.SO_SNDBUF, nettyClientConfig.getClientSocketSndBufSize()).option(ChannelOption.SO_RCVBUF,
                nettyClientConfig.getClientSocketRcvBufSize());

        bootstrap.handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                new IdleStateHandler(nettyClientConfig.getChannelMaxReadIdleSeconds(),
                                        nettyClientConfig.getChannelMaxWriteIdleSeconds(),
                                        nettyClientConfig.getChannelMaxAllIdleSeconds()))
                                .addLast(new NettyClientDecoder())
                                .addLast(new NettyClientEncoder())
                                .addLast(new ClientHandler());
                    }
                });
    }


    public Channel getNewChannel(InetSocketAddress address) {
        Channel channel;
        ChannelFuture f = this.bootstrap.connect(address);
        try {
            f.await(this.nettyClientConfig.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS);
            if (f.isCancelled()) {
                throw new RuntimeException("connect cancelled, can not connect to server.", f.cause());
            } else if (!f.isSuccess()) {
                throw new RuntimeException("connect failed, can not connect to server.", f.cause());
            } else {
                channel = f.channel();
            }
        } catch (Exception e) {
            throw new RuntimeException("can not connect to server.", e);
        }
        return channel;
    }

    public void shutdown() {
        try {
            this.eventLoopGroupWorker.shutdownGracefully();
            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
        } catch (Exception exx) {
            LOGGER.error("Failed to shutdown: {}", exx.getMessage());
        }
    }

    public Object sendSync(Channel channel, Msg msg) throws TimeoutException {
        if (channel == null) {
            LOGGER.warn("sendSync nothing, caused by null channel.");
            return null;
        }

        MsgFuture messageFuture = new MsgFuture();
        messageFuture.setMsg(msg);
        messageFuture.setTimeout(Constants.TIMEOUT);
        futures.put(msg.getId(), messageFuture);

        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                MsgFuture mf = futures.remove(msg.getId());
                if (mf != null) {
                    mf.setResultMsg(future.cause());
                }
            }
        });

        try {
            return messageFuture.get(Constants.TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (Exception exx) {
            LOGGER.error("wait response error:{},ip:{},request:{}", exx.getMessage(), channel.remoteAddress(),
                    msg.getBody());
            if (exx instanceof TimeoutException) {
                throw (TimeoutException) exx;
            } else {
                throw new RuntimeException(exx);
            }
        }
    }

    public void sendAsync(Channel channel, Msg msg) {
        channel.writeAndFlush(msg).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
            }
        });
    }


    class ClientHandler extends ChannelDuplexHandler {

        @Override
        public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
            if (!(msg instanceof Msg)) {
                return;
            }
            processMessage(ctx, (Msg) msg);
        }

        private void processMessage(ChannelHandlerContext ctx, Msg msg) {
            MsgFuture msgFuture = futures.remove(msg.getId());
            if (msgFuture != null) {
                msgFuture.setResultMsg(msg);
            } else {
                int msgType = msg.getBody().getMsgType();
                MsgTypeProcessorFactory.get(msgType).handle(ctx, msg);
            }
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) {
            synchronized (lock) {
                if (ctx.channel().isWritable()) {
                    lock.notifyAll();
                }
            }
            ctx.fireChannelWritabilityChanged();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                if (idleStateEvent.state() == IdleState.READER_IDLE) {
                }
                if (idleStateEvent == IdleStateEvent.WRITER_IDLE_STATE_EVENT) {
                    try {
                        HeartbeatMsg msg = new HeartbeatMsg();
                        msg.setPing(true);
                        ApplicationContext.sendAsync(msg);
                    } catch (Throwable throwable) {
                        LOGGER.error("send request error: {}", throwable.getMessage(), throwable);
                    }
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            super.close(ctx, future);
        }
    }

}
