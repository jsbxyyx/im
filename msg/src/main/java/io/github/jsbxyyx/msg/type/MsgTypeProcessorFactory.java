package io.github.jsbxyyx.msg.type;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author
 */
public class MsgTypeProcessorFactory {

    private static final Map<Integer, MsgTypeProcessor> SERVER_MAP = new HashMap<>();

    static {
        ServiceLoader<MsgTypeProcessor> load = ServiceLoader.load(MsgTypeProcessor.class);
        Iterator<MsgTypeProcessor> iterator = load.iterator();
        while (iterator.hasNext()) {
            MsgTypeProcessor next = iterator.next();
            SERVER_MAP.put(next.type(), next);
        }
    }

    public static MsgTypeProcessor get(int type) {
        MsgTypeProcessor processor = SERVER_MAP.get(type);
        if (processor != null) {
            return processor;
        }
        throw new IllegalArgumentException("[" + type + "] not found.");
    }

}
