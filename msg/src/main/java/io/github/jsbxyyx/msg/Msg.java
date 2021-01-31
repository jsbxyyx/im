package io.github.jsbxyyx.msg;

import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
public class Msg {

    private int id;
    private byte type;
    private Map<String, String> headMap = new HashMap<>();
    private MsgBody body;

    private transient String username;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public Map<String, String> getHeadMap() {
        return headMap;
    }

    public void setHeadMap(Map<String, String> headMap) {
        this.headMap = headMap;
    }

    public MsgBody getBody() {
        return body;
    }

    public void setBody(MsgBody body) {
        this.body = body;
    }

    public String getToken() {
        return getHeadMap().get("token");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getStatusCode() {
        return getHeadMap().get("statusCode");
    }

    public static Msg build(int id, byte type, Map<String, String> headMap, MsgBody body) {
        return Msg.build(id, type, headMap, body, StatusCode.OK);
    }

    public static Msg build(int id, byte type, Map<String, String> headMap, MsgBody body, StatusCode statusCode) {
        Msg msg = new Msg();
        msg.setId(id);
        msg.setType(type);
        msg.getHeadMap().putAll(headMap);
        if (statusCode != null) {
            msg.getHeadMap().put("statusCode", statusCode.code);
        }
        msg.setBody(body);
        return msg;
    }
}
