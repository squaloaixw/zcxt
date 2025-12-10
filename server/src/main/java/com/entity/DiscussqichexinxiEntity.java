package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

@TableName("discussqichexinxi")
public class DiscussqichexinxiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    private Long refid;
    private Long userid;
    private String nickname;
    private String avatarurl;
    private String content;
    private Long parentid; // 新增字段：父评论ID

    // 此处省略 standard Getters and Setters...
    // 请务必使用IDE自动生成或手动添加所有字段的get/set方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
    public Long getRefid() { return refid; }
    public void setRefid(Long refid) { this.refid = refid; }
    public Long getUserid() { return userid; }
    public void setUserid(Long userid) { this.userid = userid; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatarurl() { return avatarurl; }
    public void setAvatarurl(String avatarurl) { this.avatarurl = avatarurl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getParentid() { return parentid; }
    public void setParentid(Long parentid) { this.parentid = parentid; }
}
