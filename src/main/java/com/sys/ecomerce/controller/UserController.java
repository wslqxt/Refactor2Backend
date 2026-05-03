package com.sys.ecomerce.controller;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.model.ContactRequestView;
import com.sys.ecomerce.model.LoginRequest;
import com.sys.ecomerce.model.RegisterRequest;
import com.sys.ecomerce.model.ResetPasswordRequest;
import com.sys.ecomerce.model.UserPublicProfile;
import com.sys.ecomerce.service.UserService;
import commons.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        return Result.success("注册成功", userService.register(request));
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        log.warn("登录失败: {}", request.getUsername());
        return Result.success("登录成功", userService.login(request));
    }

    @PostMapping("/reset-password")
    public Result<User> resetPassword(@RequestBody ResetPasswordRequest request) {
        return Result.success("密码重置成功", userService.resetPassword(request));
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }

    @GetMapping("/list")
    public Result<List<User>> getAllUsers(@RequestParam Long operatorId) {
        return Result.success(userService.getAllUsersForStaff(operatorId));
    }

    @GetMapping("/admins/module-config")
    public Result<List<User>> listAdminsForModuleConfig(@RequestParam Long operatorId) {
        return Result.success(userService.listAdminsForModuleConfig(operatorId));
    }

    @GetMapping("/admin/assignable-modules")
    public Result<List<String>> assignableModules(@RequestParam Long operatorId) {
        return Result.success(userService.listAssignableModuleKeys(operatorId));
    }

    @GetMapping("/admins")
    public Result<List<User>> getAdminUsers() {
        return Result.success(userService.getAdminUsers());
    }

    @GetMapping("/message-contacts/{ownerId}")
    public Result<List<User>> messageContacts(@PathVariable Long ownerId) {
        return Result.success(userService.listMessageContactsForUser(ownerId));
    }

    @GetMapping("/profile-public/{id}")
    public Result<UserPublicProfile> publicProfile(@PathVariable Long id) {
        return Result.success(userService.getPublicProfile(id));
    }

    @GetMapping("/peers/search")
    public Result<List<UserPublicProfile>> searchPeers(@RequestParam Long currentUserId, @RequestParam String q) {
        return Result.success(userService.searchPeerUsers(currentUserId, q));
    }

    @GetMapping("/my-contacts/{ownerId}")
    public Result<List<UserPublicProfile>> myContacts(@PathVariable Long ownerId) {
        return Result.success(userService.listMyContacts(ownerId));
    }

    @PostMapping("/contact-requests")
    public Result<ContactRequestView> sendContactRequest(@RequestBody Map<String, Long> body) {
        Long fromUserId = body.get("fromUserId");
        Long toUserId = body.get("toUserId");
        if (fromUserId == null || toUserId == null) {
            return Result.error("缺少 fromUserId 或 toUserId");
        }
        ContactRequestView view = userService.sendContactRequest(fromUserId, toUserId);
        boolean accepted = "ACCEPTED".equals(view.getStatus());
        return Result.success(accepted ? "对方曾向你发起申请，已自动成为联系人" : "已发送好友申请，等待对方同意", view);
    }

    @GetMapping("/contact-requests/incoming/{userId}")
    public Result<List<ContactRequestView>> incomingContactRequests(@PathVariable Long userId) {
        return Result.success(userService.listIncomingContactRequests(userId));
    }

    @GetMapping("/contact-requests/outgoing/{userId}")
    public Result<List<ContactRequestView>> outgoingContactRequests(@PathVariable Long userId) {
        return Result.success(userService.listOutgoingContactRequests(userId));
    }

    @PostMapping("/contact-requests/{id}/accept")
    public Result<Void> acceptContactRequest(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        if (userId == null) {
            return Result.error("缺少 userId");
        }
        userService.acceptContactRequest(id, userId);
        return Result.success("已同意，双方已成为联系人", null);
    }

    @PostMapping("/contact-requests/{id}/reject")
    public Result<Void> rejectContactRequest(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        if (userId == null) {
            return Result.error("缺少 userId");
        }
        userService.rejectContactRequest(id, userId);
        return Result.success("已拒绝该申请", null);
    }

    @DeleteMapping("/contacts/{ownerId}/peer/{peerId}")
    public Result<Void> removePeerContact(@PathVariable Long ownerId, @PathVariable Long peerId) {
        userService.removePeerContact(ownerId, peerId);
        return Result.success("已从联系人中移除", null);
    }

    @PutMapping("/profile/{id}")
    public Result<User> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userService.updateProfile(id, body.get("username"), body.get("email"), body.get("avatar"), body.get("bio"));
        return Result.success("个人信息更新成功", user);
    }

    @PutMapping("/password/{id}")
    public Result<User> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return Result.success("密码修改成功", user);
    }

    @PutMapping("/role/{id}")
    public Result<User> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long operatorId = parseLong(body.get("operatorId"));
        User user = userService.updateRole(operatorId, id, body.get("role"));
        return Result.success("角色更新成功", user);
    }

    @PutMapping("/{id}/admin-modules")
    public Result<User> updateAdminModules(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long operatorId = parseLongObj(body.get("operatorId"));
        @SuppressWarnings("unchecked")
        List<String> keys = (List<String>) body.get("moduleKeys");
        User user = userService.updateAdminModules(operatorId, id, keys);
        return Result.success("模块权限已更新", user);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id, @RequestParam Long operatorId) {
        userService.deleteUser(operatorId, id);
        return Result.success("用户已删除", null);
    }

    private static Long parseLong(String s) {
        if (s == null || s.isBlank()) {
            throw new RuntimeException("缺少 operatorId");
        }
        try {
            return Long.parseLong(s.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("operatorId 无效");
        }
    }

    private static Long parseLongObj(Object o) {
        if (o == null) {
            throw new RuntimeException("缺少 operatorId");
        }
        if (o instanceof Number number) {
            return number.longValue();
        }
        return parseLong(String.valueOf(o));
    }
}
