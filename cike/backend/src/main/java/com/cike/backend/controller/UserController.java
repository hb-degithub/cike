package com.cike.backend.controller;

import com.cike.backend.common.BizException;
import com.cike.backend.common.Result;
import com.cike.backend.dto.LoginDTO;
import com.cike.backend.dto.RegisterDTO;
import com.cike.backend.entity.User;
import com.cike.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().isEmpty()) throw new BizException("手机号不能为空");
        if (dto.getPassword() == null || dto.getPassword().length() < 6) throw new BizException("密码至少 6 位");
        if (dto.getNickname() == null || dto.getNickname().isEmpty()) throw new BizException("昵称不能为空");
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        if (dto.getPhone() == null || dto.getPhone().isEmpty()) throw new BizException("手机号不能为空");
        return Result.success(userService.login(dto));
    }

    @GetMapping("/info")
    public Result<User> info(@RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = requireLogin(token);
        return Result.success(userService.getById(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestBody Map<String, String> body) {
        Long userId = requireLogin(token);
        userService.updateProfile(userId, body.get("nickname"), body.getOrDefault("avatar", ""), body.getOrDefault("bio", ""));
        return Result.success();
    }

    private Long requireLogin(String token) {
        Long userId = userService.getUserIdByToken(token);
        if (userId == null) throw new BizException(401, "请先登录");
        return userId;
    }
}
