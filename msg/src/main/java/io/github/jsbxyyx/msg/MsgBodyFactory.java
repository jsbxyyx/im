package io.github.jsbxyyx.msg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author
 * @since
 */
public class MsgBodyFactory {

    private static final Map<Integer, Class> MSG_BODY_MAP = new HashMap<>();

    static {
        ServiceLoader<MsgBody> load = ServiceLoader.load(MsgBody.class);
        Iterator<MsgBody> iterator = load.iterator();
        while (iterator.hasNext()) {
            MsgBody next = iterator.next();
            MSG_BODY_MAP.put(next.getMsgType(), next.getClass());
        }
    }

    public static Class get(int msgType) {
        Class aClass = MSG_BODY_MAP.get(msgType);
        if (aClass == null) {
            throw new IllegalArgumentException("not support msg type. msgType:[" + msgType + "]");
        }
        return aClass;
    }

}
