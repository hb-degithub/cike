package com.cike.backend.mapper;

import com.cike.backend.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserMapper {

    private final JdbcTemplate jdbc;

    public UserMapper(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setPhone(rs.getString("phone"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));
        u.setAvatar(rs.getString("avatar"));
        u.setBio(rs.getString("bio"));
        u.setNoteCount(rs.getInt("note_count"));
        u.setLikeTotal(rs.getInt("like_total"));
        u.setCreateTime(rs.getTimestamp("create_time") == null ? null : rs.getTimestamp("create_time").toLocalDateTime());
        return u;
    };

    public User findByPhone(String phone) {
        List<User> list = jdbc.query("SELECT * FROM t_user WHERE phone = ? AND is_deleted = 0", rowMapper, phone);
        return list.isEmpty() ? null : list.get(0);
    }

    public User findById(Long id) {
        List<User> list = jdbc.query("SELECT * FROM t_user WHERE id = ? AND is_deleted = 0", rowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int insert(User u) {
        return jdbc.update(
            "INSERT INTO t_user(phone, password, nickname, avatar, bio) VALUES(?,?,?,?,?)",
            u.getPhone(), u.getPassword(), u.getNickname(), u.getAvatar() == null ? "" : u.getAvatar(), u.getBio() == null ? "" : u.getBio()
        );
    }

    public int updateProfile(Long id, String nickname, String avatar, String bio) {
        return jdbc.update("UPDATE t_user SET nickname = ?, avatar = ?, bio = ? WHERE id = ?", nickname, avatar, bio, id);
    }
}
