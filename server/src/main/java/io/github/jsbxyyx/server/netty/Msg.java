package io.github.jsbxyyx.server.netty;

import java.util.Map;

/**
 * @author
 */
public abstract class Msg {

    private int id;
    private byte type;
    private Map<String, String> headMap;
    private Object body;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }

    public void setHeadMap(Map<String, String> headMap) {
        this.headMap = headMap;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public abstract byte[] encode();

    public abstract Object decode(byte[] bs);
}
