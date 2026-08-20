package com.cike.backend.service;

import com.cike.backend.common.BizException;
import com.cike.backend.dto.NotePublishDTO;
import com.cike.backend.entity.Comment;
import com.cike.backend.entity.Note;
import com.cike.backend.entity.User;
import com.cike.backend.mapper.CommentMapper;
import com.cike.backend.mapper.NoteMapper;
import com.cike.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoteService {

    private final NoteMapper noteMapper;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    public NoteService(NoteMapper noteMapper, UserMapper userMapper, CommentMapper commentMapper) {
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
        this.commentMapper = commentMapper;
    }

    public Long publish(Long userId, NotePublishDTO dto) {
        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            throw new BizException("请至少上传一张图片");
        }
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new BizException("请填写笔记标题");
        }
        Note n = new Note();
        n.setUserId(userId);
        n.setTitle(dto.getTitle().trim());
        n.setContent(dto.getContent() == null ? "" : dto.getContent());
        n.setCoverUrl(dto.getImages().get(0));
        n.setVisible(dto.getVisible());
        Long noteId = noteMapper.insert(n);
        for (int i = 0; i < dto.getImages().size(); i++) {
            noteMapper.insertImage(noteId, dto.getImages().get(i), i);
        }
        return noteId;
    }

    public Map<String, Object> detail(Long id, Long currentUserId) {
        Note n = noteMapper.findById(id);
        if (n == null) throw new BizException(404, "笔记不存在");
        noteMapper.increaseView(id);
        User author = userMapper.findById(n.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("note", n);
        result.put("images", noteMapper.listImages(id));
        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("id", author == null ? 0 : author.getId());
        authorMap.put("nickname", author == null ? "未知用户" : author.getNickname());
        authorMap.put("avatar", author == null ? "" : author.getAvatar());
        authorMap.put("bio", author == null ? "" : author.getBio());
        result.put("author", authorMap);
        if (currentUserId != null) {
            result.put("liked", commentMapper.isLiked(currentUserId, id));
            result.put("collected", commentMapper.isCollected(currentUserId, id));
            result.put("mine", n.getUserId().equals(currentUserId));
        } else {
            result.put("liked", false);
            result.put("collected", false);
            result.put("mine", false);
        }
        return result;
    }

    public List<Map<String, Object>> list(String keyword, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        List<Note> notes = (keyword == null || keyword.isEmpty())
            ? noteMapper.listPublic(offset, size)
            : noteMapper.search(keyword, offset, size);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Note n : notes) {
            result.add(toCard(n));
        }
        return result;
    }

    public List<Map<String, Object>> listByUser(Long userId) {
        List<Note> notes = noteMapper.listByUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Note n : notes) {
            result.add(toCard(n));
        }
        return result;
    }

    private Map<String, Object> toCard(Note n) {
        User author = userMapper.findById(n.getUserId());
        Map<String, Object> map = new HashMap<>();
        map.put("id", n.getId());
        map.put("title", n.getTitle());
        map.put("coverUrl", n.getCoverUrl());
        map.put("likeCount", n.getLikeCount());
        map.put("commentCount", n.getCommentCount());
        map.put("createTime", n.getCreateTime());
        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("id", author == null ? 0 : author.getId());
        authorMap.put("nickname", author == null ? "未知用户" : author.getNickname());
        authorMap.put("avatar", author == null ? "" : author.getAvatar());
        map.put("author", authorMap);
        return map;
    }

    public void delete(Long id, Long userId) {
        int rows = noteMapper.softDelete(id, userId);
        if (rows == 0) throw new BizException(403, "只能删除自己的笔记");
    }

    // 互动
    public boolean toggleLike(Long userId, Long noteId) {
        if (commentMapper.isLiked(userId, noteId)) {
            commentMapper.unlike(userId, noteId);
            return false;
        }
        commentMapper.like(userId, noteId);
        return true;
    }

    public boolean toggleCollect(Long userId, Long noteId) {
        if (commentMapper.isCollected(userId, noteId)) {
            commentMapper.uncollect(userId, noteId);
            return false;
        }
        commentMapper.collect(userId, noteId);
        return true;
    }

    // 评论
    public void addComment(Long userId, Long noteId, String content, Long parentId, Long replyUserId) {
        if (content == null || content.trim().isEmpty()) throw new BizException("评论内容不能为空");
        Comment c = new Comment();
        c.setNoteId(noteId);
        c.setUserId(userId);
        c.setParentId(parentId);
        c.setReplyUserId(replyUserId);
        c.setContent(content.trim());
        commentMapper.insert(c);
    }

    public List<Comment> listComments(Long noteId) {
        return commentMapper.listByNote(noteId);
    }

    public void deleteComment(Long commentId, Long userId) {
        int rows = commentMapper.softDelete(commentId, userId);
        if (rows == 0) throw new BizException(403, "只能删除自己的评论");
    }

    public List<Long> myCollectIds(Long userId) {
        return commentMapper.listCollectedNoteIds(userId);
    }

    public List<Long> myLikeIds(Long userId) {
        return commentMapper.listLikedNoteIds(userId);
    }
}
