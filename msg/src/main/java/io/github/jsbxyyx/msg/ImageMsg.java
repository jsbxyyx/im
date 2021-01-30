package io.github.jsbxyyx.msg;

import java.util.Date;

/**
 * @author
 * @since
 */
public class ImageMsg implements AnyMsg {

    private String id;
    private String from;
    private String to;
    private String toType;
    private Date createTime;

    // data:image/png;base64,
    // data:image/jpeg;base64,
    private String image;

    @Override
    public int getMsgType() {
        return MsgType.Image;
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
    public String getToType() {
        return toType;
    }

    @Override
    public void setToType(String toType) {
        this.toType = toType;
    }

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
