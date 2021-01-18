package io.github.jsbxyyx.pcclient;

import io.github.jsbxyyx.pcclient.context.ApplicationContext;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
import io.github.jsbxyyx.pcclient.netty.NettyClientConfig;
import io.github.jsbxyyx.pcclient.service.ConfigService;
import io.github.jsbxyyx.pcclient.ui.FontUtil;
import io.github.jsbxyyx.pcclient.ui.LoginUI;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.net.InetSocketAddress;

/**
 * @author
 * @since
 */
public class PcClientMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PcClientMain.class);

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
                FontUtil.initGlobalFont(new Font("Serif", Font.PLAIN, 12));
                new LoginUI().launch();
            }
        });

    }

}
