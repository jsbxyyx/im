package io.github.jsbxyyx.msg;

import java.util.Date;

/**
 * @author
 * @since
 */
public class TextMsg implements MsgBody {

    private String from; // 来自谁
    private String to; // 发给谁
    private String toType; // to的类型 1：组
    private String text; // 文本内容
    private Date createTime; // 创建时间

    public TextMsg() {
    }

    public TextMsg(String from, String to, String toType, String text, Date createTime) {
        this.from = from;
        this.to = to;
        this.toType = toType;
        this.text = text;
        this.createTime = createTime;
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

    @Override
    public int getMsgType() {
        return MsgType.Text;
    }

    public String getToType() {
        return toType;
    }

    public void setToType(String toType) {
        this.toType = toType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
