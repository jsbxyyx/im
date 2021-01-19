package io.github.jsbxyyx.pcclient.context;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.msg.TextMsg;
import io.github.jsbxyyx.pcclient.netty.Global;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
import io.github.jsbxyyx.pcclient.netty.NettyClientConfig;
import io.github.jsbxyyx.pcclient.service.ConfigService;
import io.github.jsbxyyx.pcclient.ui.MainUI;
import io.netty.channel.Channel;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @since
 */
public class ApplicationContext {

    private static NettyClient nettyClient;
    private static Channel channel;
    private static JFrame mainUI;

    public static void init() {
        ConfigService.init();
    }

    public static void setNettyClient(NettyClient nc) {
        nettyClient = nc;
    }

    public static void setChannel(Channel ch) {
        channel = ch;
    }

    public static void setMainUI(JFrame mainUI) {
        ApplicationContext.mainUI = mainUI;
    }

    public static Msg sendSync(MsgBody msgBody) {
        checkNettyClient();
        Map<String, String> headMap = new HashMap<>();
        if (Global.getToken() != null) {
            headMap.put("token", Global.getToken());
        }
        Msg msg = Msg.build(IdGenerator.getInstance().incrementAndGet(), Constants.SYNC, headMap, msgBody);
        try {
            Msg rsp = (Msg) nettyClient.sendSync(channel, msg);
            return rsp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendAsync(MsgBody msgBody) {
        checkNettyClient();
        Map<String, String> headMap = new HashMap<>();
        if (Global.getToken() != null) {
            headMap.put("token", Global.getToken());
        }
        Msg msg = Msg.build(IdGenerator.getInstance().incrementAndGet(), Constants.ASYNC, headMap, msgBody);
        nettyClient.sendAsync(channel, msg);
    }

    public static void appendMsg(TextMsg textMsg) {
        if (mainUI == null) {
            throw new IllegalStateException("mainUI is null");
        }
        int oldValue = ((MainUI) mainUI).getScrollValue();
        ((MainUI) mainUI).appendMsg(textMsg);
        ((MainUI) mainUI).scrollBottom(oldValue);
        mainUI.setVisible(true);
    }

    private static void checkNettyClient() {
        if (nettyClient == null || channel == null) {
            throw new IllegalStateException("nettyClient or channel is null");
        }
    }

    public static void shutdown() {
        if (nettyClient != null) {
            nettyClient.shutdown();
        }
        Global.clear();
    }

    public static void connect() {
        NettyClient nettyClient = new NettyClient(new NettyClientConfig());
        nettyClient.start();

        String host = ConfigService.getValue("host");
        String[] split = host.split(":");

        Channel newChannel = nettyClient.getNewChannel(new InetSocketAddress(split[0], Integer.parseInt(split[1])));

        ApplicationContext.setNettyClient(nettyClient);
        ApplicationContext.setChannel(newChannel);
    }

    public static void setNetworkStatus(boolean status) {
        if (mainUI.getTitle().contains("......")) {
            mainUI.setTitle(mainUI.getTitle().replace("在线", status ? "在线" : "离线")
                    .replace("离线", status ? "在线" : "离线"));
        } else {
            mainUI.setTitle(mainUI.getTitle() + "......" + (status ? "在线" : "离线"));
        }
    }

}
