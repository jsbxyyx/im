package io.github.jsbxyyx.pcclient;

import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
import io.github.jsbxyyx.pcclient.netty.NettyClientConfig;
import io.github.jsbxyyx.pcclient.service.ConfigService;
import io.github.jsbxyyx.pcclient.ui.LoginUI;
import io.netty.channel.Channel;

import javax.swing.*;
import java.net.InetSocketAddress;

/**
 * @author
 * @since
 */
public class PcClientMain {

    public static void main(String[] args) throws Exception {
        ConfigService.init();

        NettyClient nettyClient = new NettyClient(new NettyClientConfig());
        nettyClient.start();

        String host = ConfigService.getValue("host");
        String[] split = host.split(":");

        Channel newChannel = nettyClient.getNewChannel(new InetSocketAddress(split[0], Integer.parseInt(split[1])));

        ApplicationContext.setNettyClient(nettyClient);
        ApplicationContext.setChannel(newChannel);

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                new LoginUI().launch();
            }
        });

    }

}
