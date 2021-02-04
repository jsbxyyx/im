package io.github.jsbxyyx.server;

import io.github.jsbxyyx.server.handler.OfflineHandler;
import io.github.jsbxyyx.server.netty.NettyServer;
import io.github.jsbxyyx.server.netty.NettyServerConfig;
import io.github.jsbxyyx.server.service.UserService;

/**
 * @author
 */
public class ServerMain {

    public static void main(String[] args) {
        UserService.init();

        NettyServerConfig config = new NettyServerConfig();
        NettyServer server = new NettyServer(config, new OfflineHandler());
        server.start();
    }

}
