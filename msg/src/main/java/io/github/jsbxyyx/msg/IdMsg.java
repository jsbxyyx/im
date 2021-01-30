package io.github.jsbxyyx.msg;

/**
 * @author
 * @since
 */
public class IdMsg implements MsgBody {

    private String id;

    public IdMsg() {
    }

    public IdMsg(String id) {
        this.id = id;
    }

    @Override
    public int getMsgType() {
        return MsgType.Id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
