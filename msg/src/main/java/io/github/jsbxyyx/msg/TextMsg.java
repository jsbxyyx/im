package io.github.jsbxyyx.msg;

import java.util.Date;

/**
 * @author
 * @since
 */
public class TextMsg implements AnyMsg {

    private String id; // id
    private String from; // 来自谁
    private String to; // 发给谁
    private String toType; // to的类型 1：组
    private Date createTime; // 创建时间
    private String text; // 文本内容

    public TextMsg() {
    }

    public TextMsg(String id, String from, String to, String toType, String text, Date createTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.toType = toType;
        this.text = text;
        this.createTime = createTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public int getMsgType() {
        return MsgType.Text;
    }

    @Override
    public String getToType() {
        return toType;
    }

    @Override
    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
