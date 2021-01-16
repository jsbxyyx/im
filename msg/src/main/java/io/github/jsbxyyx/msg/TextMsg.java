package io.github.jsbxyyx.msg;

import java.util.Date;

/**
 * @author
 * @since
 */
public class TextMsg {

    public static final byte TYPE = 2;

    private String from; // 来自谁
    private String to; // 发给谁
    private String type; // to的类型 1：组
    private String text; // 文本内容
    private Date createTime; // 创建时间

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
