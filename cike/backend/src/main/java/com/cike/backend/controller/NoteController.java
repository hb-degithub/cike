package com.cike.backend.controller;

import com.cike.backend.common.BizException;
import com.cike.backend.common.Result;
import com.cike.backend.dto.NotePublishDTO;
import com.cike.backend.entity.Comment;
import com.cike.backend.service.NoteService;
import com.cike.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/note")
public class NoteController {

    private final NoteService noteService;
    private final UserService userService;

    public NoteController(NoteService noteService, UserService userService) {
        this.noteService = noteService;
        this.userService = userService;
    }

    /** 笔记列表 / 搜索（无需登录） */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(noteService.list(keyword, page, size));
    }

    /** 笔记详情 */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> detail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = userService.getUserIdByToken(token);
        return Result.success(noteService.detail(id, userId));
    }

    /** 发布笔记（需登录） */
    @PostMapping("/publish")
    public Result<Map<String, Long>> publish(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody NotePublishDTO dto) {
        Long userId = requireLogin(token);
        Long noteId = noteService.publish(userId, dto);
        return Result.success(Map.of("id", noteId));
    }

    /** 删除笔记（需登录，仅作者） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        noteService.delete(id, requireLogin(token));
        return Result.success();
    }

    /** 我的作品 */
    @GetMapping("/my")
    public Result<List<Map<String, Object>>> my(
            @RequestHeader(value = "Authorization", required = false) String token) {
        return Result.success(noteService.listByUser(requireLogin(token)));
    }

    /** 点赞/取消点赞 */
    @PostMapping("/{id}/like")
    public Result<Map<String, Boolean>> like(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        boolean liked = noteService.toggleLike(requireLogin(token), id);
        return Result.success(Map.of("liked", liked));
    }

    /** 收藏/取消收藏 */
    @PostMapping("/{id}/collect")
    public Result<Map<String, Boolean>> collect(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        boolean collected = noteService.toggleCollect(requireLogin(token), id);
        return Result.success(Map.of("collected", collected));
    }

    /** 我收藏的笔记 id 列表 */
    @GetMapping("/my/collects")
    public Result<List<Long>> myCollects(@RequestHeader(value = "Authorization", required = false) String token) {
        return Result.success(noteService.myCollectIds(requireLogin(token)));
    }

    /** 我点赞的笔记 id 列表 */
    @GetMapping("/my/likes")
    public Result<List<Long>> myLikes(@RequestHeader(value = "Authorization", required = false) String token) {
        return Result.success(noteService.myLikeIds(requireLogin(token)));
    }

    /** 评论列表 */
    @GetMapping("/{id}/comments")
    public Result<List<Comment>> comments(@PathVariable Long id) {
        return Result.success(noteService.listComments(id));
    }

    /** 发表评论 */
    @PostMapping("/{id}/comment")
    public Result<Void> addComment(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> body) {
        Long userId = requireLogin(token);
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") == null ? 0L : Long.valueOf(body.get("parentId").toString());
        Long replyUserId = body.get("replyUserId") == null ? 0L : Long.valueOf(body.get("replyUserId").toString());
        noteService.addComment(userId, id, content, parentId, replyUserId);
        return Result.success();
    }

    /** 删除评论 */
    @DeleteMapping("/comment/{commentId}")
    public Result<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        noteService.deleteComment(commentId, requireLogin(token));
        return Result.success();
    }

    private Long requireLogin(String token) {
        Long userId = userService.getUserIdByToken(token);
        if (userId == null) throw new BizException(401, "请先登录");
        return userId;
    }
}
