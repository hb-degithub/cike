package com.cike.backend.entity;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Long noteId;
    private Long userId;
    private Long parentId;
    private Long replyUserId;
    private String content;
    private LocalDateTime createTime;

    // 展示用
    private String nickname;
    private String avatar;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getNoteId() { return noteId; }
    public void setNoteId(Long noteId) { this.noteId = noteId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Long getReplyUserId() { return replyUserId; }
    public void setReplyUserId(Long replyUserId) { this.replyUserId = replyUserId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}
