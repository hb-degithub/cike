package com.cike.backend.mapper;

import com.cike.backend.entity.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentMapper {

    private final JdbcTemplate jdbc;

    public CommentMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Comment> rowMapper = (rs, rowNum) -> {
        Comment c = new Comment();
        c.setId(rs.getLong("id"));
        c.setNoteId(rs.getLong("note_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setParentId(rs.getLong("parent_id"));
        c.setReplyUserId(rs.getLong("reply_user_id"));
        c.setContent(rs.getString("content"));
        c.setCreateTime(rs.getTimestamp("create_time") == null ? null : rs.getTimestamp("create_time").toLocalDateTime());
        return c;
    };

    public int insert(Comment c) {
        return jdbc.update(
            "INSERT INTO t_comment(note_id, user_id, parent_id, reply_user_id, content) VALUES(?,?,?,?,?)",
            c.getNoteId(), c.getUserId(), c.getParentId() == null ? 0 : c.getParentId(),
            c.getReplyUserId() == null ? 0 : c.getReplyUserId(), c.getContent());
    }

    public List<Comment> listByNote(Long noteId) {
        return jdbc.query(
            "SELECT c.*, u.nickname, u.avatar FROM t_comment c LEFT JOIN t_user u ON c.user_id = u.id " +
            "WHERE c.note_id = ? AND c.is_deleted = 0 ORDER BY c.create_time DESC",
            (rs, rowNum) -> {
                Comment c = rowMapper.mapRow(rs, rowNum);
                c.setNickname(rs.getString("nickname"));
                c.setAvatar(rs.getString("avatar"));
                return c;
            }, noteId);
    }

    public int softDelete(Long id, Long userId) {
        return jdbc.update("UPDATE t_comment SET is_deleted = 1 WHERE id = ? AND user_id = ?", id, userId);
    }

    // 点赞
    public int like(Long userId, Long noteId) {
        return jdbc.update("INSERT IGNORE INTO t_user_like_note(user_id, note_id) VALUES(?,?)", userId, noteId);
    }
    public int unlike(Long userId, Long noteId) {
        return jdbc.update("DELETE FROM t_user_like_note WHERE user_id = ? AND note_id = ?", userId, noteId);
    }
    public boolean isLiked(Long userId, Long noteId) {
        Integer cnt = jdbc.queryForObject(
            "SELECT COUNT(*) FROM t_user_like_note WHERE user_id = ? AND note_id = ?", Integer.class, userId, noteId);
        return cnt != null && cnt > 0;
    }

    // 收藏
    public int collect(Long userId, Long noteId) {
        return jdbc.update("INSERT IGNORE INTO t_user_collect_note(user_id, note_id) VALUES(?,?)", userId, noteId);
    }
    public int uncollect(Long userId, Long noteId) {
        return jdbc.update("DELETE FROM t_user_collect_note WHERE user_id = ? AND note_id = ?", userId, noteId);
    }
    public boolean isCollected(Long userId, Long noteId) {
        Integer cnt = jdbc.queryForObject(
            "SELECT COUNT(*) FROM t_user_collect_note WHERE user_id = ? AND note_id = ?", Integer.class, userId, noteId);
        return cnt != null && cnt > 0;
    }
    public List<Long> listCollectedNoteIds(Long userId) {
        return jdbc.queryForList(
            "SELECT note_id FROM t_user_collect_note WHERE user_id = ? ORDER BY create_time DESC", Long.class, userId);
    }
    public List<Long> listLikedNoteIds(Long userId) {
        return jdbc.queryForList(
            "SELECT note_id FROM t_user_like_note WHERE user_id = ? ORDER BY create_time DESC", Long.class, userId);
    }
}
