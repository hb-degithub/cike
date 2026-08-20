package com.cike.backend.service;

import com.cike.backend.common.BizException;
import com.cike.backend.dto.LoginDTO;
import com.cike.backend.dto.RegisterDTO;
import com.cike.backend.entity.User;
import com.cike.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserMapper userMapper;
    // 简单内存 token 存储（演示用，生产应放 Redis）
    private final Map<String, Long> tokenStore = new ConcurrentHashMap<>();

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, Object> register(RegisterDTO dto) {
        if (userMapper.findByPhone(dto.getPhone()) != null) {
            throw new BizException("该手机号已注册");
        }
        User u = new User();
        u.setPhone(dto.getPhone());
        u.setPassword(md5(dto.getPassword()));
        u.setNickname(dto.getNickname());
        u.setAvatar("");
        u.setBio("");
        userMapper.insert(u);
        return loginByPhone(dto.getPhone());
    }

    public Map<String, Object> login(LoginDTO dto) {
        User u = userMapper.findByPhone(dto.getPhone());
        if (u == null) {
            throw new BizException("手机号未注册");
        }
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (!u.getPassword().equals(md5(dto.getPassword()))) {
                throw new BizException("密码错误");
            }
        }
        // 验证码登录：演示环境验证码固定 123456，由前端控制
        return loginByPhone(dto.getPhone());
    }

    private Map<String, Object> loginByPhone(String phone) {
        User u = userMapper.findByPhone(phone);
        String token = UUID.randomUUID().toString().replace("-", "");
        tokenStore.put(token, u.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        u.setPassword(null);
        result.put("user", u);
        return result;
    }

    public Long getUserIdByToken(String token) {
        return token == null ? null : tokenStore.get(token);
    }

    public User getById(Long id) {
        User u = userMapper.findById(id);
        if (u != null) u.setPassword(null);
        return u;
    }

    public void updateProfile(Long userId, String nickname, String avatar, String bio) {
        userMapper.updateProfile(userId, nickname, avatar, bio);
    }
}
