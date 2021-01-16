package io.github.jsbxyyx.msg.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author
 */
public class MsgProcessorFactory {

    private static final Map<Byte, MsgProcessor> SERVER_MAP = new HashMap<>();

    static {
        ServiceLoader<MsgProcessor> load = ServiceLoader.load(MsgProcessor.class);
        Iterator<MsgProcessor> iterator = load.iterator();
        while (iterator.hasNext()) {
            MsgProcessor next = iterator.next();
            SERVER_MAP.put(next.type(), next);
        }
    }

    public static MsgProcessor get(byte type) {
        MsgProcessor processor = SERVER_MAP.get(type);
        if (processor != null) {
            return processor;
        }
        throw new IllegalArgumentException("[" + type + "] not found.");
    }

}
