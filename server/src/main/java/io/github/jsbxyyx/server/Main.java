package io.github.jsbxyyx.server;

import io.github.jsbxyyx.server.netty.NettyServer;
import io.github.jsbxyyx.server.netty.NettyServerConfig;
import io.github.jsbxyyx.server.service.UserService;

/**
 * @author
 */
public class Main {

    public static void main(String[] args) {
        UserService.init();

        NettyServerConfig config = new NettyServerConfig();
        NettyServer server = new NettyServer(config);
        server.start();
    }

}
