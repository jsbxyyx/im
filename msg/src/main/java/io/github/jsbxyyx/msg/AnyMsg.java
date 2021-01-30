package io.github.jsbxyyx.msg;

import java.util.Date;

/**
 * @author
 * @since
 */
public interface AnyMsg extends MsgBody {

    String getId();

    void setId(String id);

    String getFrom();

    void setFrom(String from);

    String getTo();

    void setTo(String to);

    String getToType();

    void setToType(String toType);

    Date getCreateTime();

    void setCreateTime(Date createTime);

    int getMsgType();
}
