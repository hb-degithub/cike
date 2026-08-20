package com.cike.backend.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private String bio;
    private Integer noteCount;
    private Integer likeTotal;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public Integer getNoteCount() { return noteCount; }
    public void setNoteCount(Integer noteCount) { this.noteCount = noteCount; }
    public Integer getLikeTotal() { return likeTotal; }
    public void setLikeTotal(Integer likeTotal) { this.likeTotal = likeTotal; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
