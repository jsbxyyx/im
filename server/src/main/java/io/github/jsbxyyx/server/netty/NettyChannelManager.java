package io.github.jsbxyyx.server.netty;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 */
public class NettyChannelManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelManager.class);

    private static Map<String, Channel> map = new ConcurrentHashMap<>(1280);

    public static void add(Channel channel) {
        String id = channel.id().asLongText();
        map.put(id, channel);
    }

    public static Channel get(String clientId) {
        return map.get(clientId);
    }

    public static void remove(Channel channel) {
        map.remove(channel.id().asLongText());
    }

}
