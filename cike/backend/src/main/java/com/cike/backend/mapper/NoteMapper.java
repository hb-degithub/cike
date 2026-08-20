package com.cike.backend.mapper;

import com.cike.backend.entity.Note;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class NoteMapper {

    private final JdbcTemplate jdbc;

    public NoteMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Note> rowMapper = (rs, rowNum) -> {
        Note n = new Note();
        n.setId(rs.getLong("id"));
        n.setUserId(rs.getLong("user_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setCoverUrl(rs.getString("cover_url"));
        n.setViewCount(rs.getInt("view_count"));
        n.setLikeCount(rs.getInt("like_count"));
        n.setCollectCount(rs.getInt("collect_count"));
        n.setCommentCount(rs.getInt("comment_count"));
        n.setVisible(rs.getInt("visible"));
        n.setStatus(rs.getInt("status"));
        n.setCreateTime(rs.getTimestamp("create_time") == null ? null : rs.getTimestamp("create_time").toLocalDateTime());
        return n;
    };

    public Long insert(Note n) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO t_note(user_id, title, content, cover_url, visible, status) VALUES(?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, n.getUserId());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getContent());
            ps.setString(4, n.getCoverUrl() == null ? "" : n.getCoverUrl());
            ps.setInt(5, n.getVisible() == null ? 1 : n.getVisible());
            ps.setInt(6, 1); // 演示环境默认审核通过
            return ps;
        }, keyHolder);
        return keyHolder.getKey() == null ? null : keyHolder.getKey().longValue();
    }

    public Note findById(Long id) {
        List<Note> list = jdbc.query("SELECT * FROM t_note WHERE id = ? AND is_deleted = 0", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Note> listPublic(int offset, int size) {
        return jdbc.query(
            "SELECT * FROM t_note WHERE is_deleted = 0 AND visible = 1 AND status = 1 ORDER BY create_time DESC LIMIT ? OFFSET ?",
            rowMapper, size, offset);
    }

    public List<Note> listByUser(Long userId) {
        return jdbc.query(
            "SELECT * FROM t_note WHERE is_deleted = 0 AND user_id = ? ORDER BY create_time DESC",
            rowMapper, userId);
    }

    public List<Note> search(String keyword, int offset, int size) {
        String like = "%" + keyword + "%";
        return jdbc.query(
            "SELECT * FROM t_note WHERE is_deleted = 0 AND visible = 1 AND status = 1 AND (title LIKE ? OR content LIKE ?) ORDER BY create_time DESC LIMIT ? OFFSET ?",
            rowMapper, like, like, size, offset);
    }

    public int insertImage(Long noteId, String url, int sort) {
        return jdbc.update("INSERT INTO t_note_image(note_id, image_url, sort) VALUES(?,?,?)", noteId, url, sort);
    }

    public List<String> listImages(Long noteId) {
        return jdbc.queryForList(
            "SELECT image_url FROM t_note_image WHERE note_id = ? AND is_deleted = 0 ORDER BY sort ASC",
            String.class, noteId);
    }

    public int increaseView(Long id) {
        return jdbc.update("UPDATE t_note SET view_count = view_count + 1 WHERE id = ?", id);
    }

    public int softDelete(Long id, Long userId) {
        return jdbc.update("UPDATE t_note SET is_deleted = 1 WHERE id = ? AND user_id = ?", id, userId);
    }
}
