package io.github.jsbxyyx.server;

import io.github.jsbxyyx.server.netty.NettyServer;
import io.github.jsbxyyx.server.netty.NettyServerConfig;

/**
 * @author
 */
public class Main {

    public static void main(String[] args) {
        NettyServerConfig config = new NettyServerConfig();
        NettyServer server = new NettyServer(config);
        server.start();
    }

}
