package io.github.jsbxyyx.msg;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author
 * @since
 */
public class AnyMsg implements MsgBody {

    private String id; // id
    private String from; // 来自谁
    private String to; // 发给谁
    private String toType; // to的类型 1：组
    private Date createTime; // 创建时间
    private byte[] content; // 内容
    private int type; // 1文本 2图片 3文件

    public AnyMsg() {
    }

    public AnyMsg(String id, String from, String to, String toType, Date createTime, byte[] content, int type) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.toType = toType;
        this.createTime = createTime;
        this.content = content;
        this.type = type;
    }

    @Override
    public int getMsgType() {
        return MsgType.Any;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getText() {
        if (type == AnyMsgType.Text) {
            return new String(content, StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException("type : " + type + " is invalid");
    }

    public byte[] getImage() {
        if (type == AnyMsgType.Image) {
            return content;
        }
        throw new IllegalArgumentException("type : " + type + " is invalid");
    }
}
