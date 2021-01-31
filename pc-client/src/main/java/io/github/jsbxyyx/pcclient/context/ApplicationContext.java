package io.github.jsbxyyx.pcclient.context;

import io.github.jsbxyyx.common.Constants;
import io.github.jsbxyyx.common.IdGenerator;
import io.github.jsbxyyx.msg.AnyMsg;
import io.github.jsbxyyx.msg.Msg;
import io.github.jsbxyyx.msg.MsgBody;
import io.github.jsbxyyx.pcclient.netty.Global;
import io.github.jsbxyyx.pcclient.netty.NettyClient;
import io.github.jsbxyyx.pcclient.netty.NettyClientConfig;
import io.github.jsbxyyx.pcclient.service.ConfigService;
import io.github.jsbxyyx.pcclient.ui.MainUI;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * @author
 * @since
 */
public class ApplicationContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    private static NettyClient nettyClient;
    private static Channel channel;
    private static JFrame mainUI;

    private static final Map<String, AnyMsg> MSG_MAP = new ConcurrentHashMap<>();

    private static final DelayQueue<AnyMsg> DELAY_MSG_QUEUE = new DelayQueue<>();

    public static void init() {
        ConfigService.init();
        startConsumerDelayMsg();
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

    public static void appendMsg(AnyMsg anyMsg) {
        if (mainUI == null) {
            throw new IllegalStateException("mainUI is null");
        }
        int oldValue = ((MainUI) mainUI).getScrollValue();
        ((MainUI) mainUI).appendMsg(anyMsg);
        ((MainUI) mainUI).scrollBottom(oldValue);
        mainUI.setVisible(true);
        MSG_MAP.put(anyMsg.getId(), anyMsg);
        DELAY_MSG_QUEUE.offer(anyMsg);
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

    public static AnyMsg getAnyMsgById(String id) {
        AnyMsg anyMsg = MSG_MAP.get(id);
        return anyMsg;
    }

    public static void removeMsg(String id) {
        ((MainUI) mainUI).removeMsg(id);
        MSG_MAP.remove(id);
    }

    public static void removeMsg(JComponent comp) {
        ((MainUI) mainUI).removeMsg(comp);
        MSG_MAP.remove(comp.getName());
    }

    public static void startConsumerDelayMsg() {
        new Thread(new DelayMsgQueueConsumer(DELAY_MSG_QUEUE)).start();
        LOGGER.info("start consumer delay msg.");
    }

    static class DelayMsgQueueConsumer implements Runnable {

        private final DelayQueue<AnyMsg> delayQueue;

        DelayMsgQueueConsumer(DelayQueue<AnyMsg> delayQueue) {
            this.delayQueue = delayQueue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 从延迟队列的头部获取已经过期的消息
                    // 如果暂时没有过期消息或者队列为空，则take()方法会被阻塞，直到有过期的消息为止
                    AnyMsg anyMsg = delayQueue.take();
                    removeMsg(anyMsg.getId());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
