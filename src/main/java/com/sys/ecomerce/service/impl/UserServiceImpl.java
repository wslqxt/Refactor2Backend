package com.sys.ecomerce.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sys.ecomerce.config.AdminAssignableModules;
import com.sys.ecomerce.entity.ContactRequest;
import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.entity.UserContact;
import com.sys.ecomerce.enums.ContactRequestStatus;
import com.sys.ecomerce.enums.UserRole;
import com.sys.ecomerce.mapper.ContactRequestMapper;
import com.sys.ecomerce.mapper.UserAddressMapper;
import com.sys.ecomerce.mapper.UserContactMapper;
import com.sys.ecomerce.mapper.UserCouponMapper;
import com.sys.ecomerce.mapper.UserMapper;
import com.sys.ecomerce.model.ContactRequestView;
import com.sys.ecomerce.model.LoginRequest;
import com.sys.ecomerce.model.UserPublicProfile;
import com.sys.ecomerce.service.UserService;
import commons.security.RsaUtils;
import commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int AVATAR_URL_MAX_LENGTH = 2048;
    private static final long AVATAR_MAX_BYTES = 5L * 1024L * 1024L;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final RsaUtils rsaUtils;
    private final UserMapper userMapper;
    private final UserContactMapper userContactMapper;
    private final ContactRequestMapper contactRequestMapper;
    private final UserAddressMapper userAddressMapper;
    private final UserCouponMapper userCouponMapper;
    private final ObjectMapper objectMapper;

    @Override
    public User login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!rsaUtils.decrypt(user.getPassword()).equals(request.getPassword())) {
            throw new BusinessException("密码错误");
        }

        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public List<User> getAllUsersForStaff(Long operatorId) {
        User operator = getUserById(operatorId);
        if (operator.getRole() != UserRole.ADMIN && operator.getRole() != UserRole.SUPER_ADMIN) {
            throw new BusinessException("无权查看用户列表");
        }
        return userMapper.findAll();
    }

    @Override
    public List<User> getAdminUsers() {
        return userMapper.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN || u.getRole() == UserRole.SUPER_ADMIN)
                .toList();
    }

    @Override
    public List<User> listAdminsForModuleConfig(Long operatorId) {
        assertSuperAdmin(operatorId);
        return userMapper.findByRole(UserRole.ADMIN);
    }

    @Override
    public User updateProfile(Long id, String username, String email, String avatar, String bio) {
        User user = getUserById(id);
        if (!user.getUsername().equals(username) && userMapper.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
        if (!user.getEmail().equals(email) && userMapper.existsByEmail(email)) {
            throw new BusinessException("邮箱已被注册");
        }
        user.setUsername(username);
        user.setEmail(email);
        if (avatar != null) {
            user.setAvatar(normalizeAvatar(avatar));
        }
        if (bio != null) {
            user.setBio(bio.isBlank() ? null : bio.trim());
        }
        userMapper.update(user);
        return getUserById(id);
    }

    // ========== 以下所有方法保持原样，不需要修改 ==========

    private String normalizeAvatar(String avatar) {
        String normalized = avatar.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        if (normalized.regionMatches(true, 0, "data:image", 0, "data:image".length())) {
            return persistBase64Avatar(normalized);
        }
        if (normalized.length() > AVATAR_URL_MAX_LENGTH) {
            throw new BusinessException("头像地址过长，请使用站内上传地址");
        }
        return normalized;
    }

    private String persistBase64Avatar(String dataUri) {
        int commaIndex = dataUri.indexOf(',');
        if (commaIndex <= 0) {
            throw new BusinessException("头像数据格式错误");
        }
        String header = dataUri.substring(0, commaIndex).toLowerCase();
        String payload = dataUri.substring(commaIndex + 1);
        if (!header.contains(";base64")) {
            throw new BusinessException("头像数据格式错误");
        }
        String ext = detectImageExtension(header);
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(payload);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("头像数据解码失败");
        }
        if (bytes.length == 0) {
            throw new BusinessException("头像内容为空");
        }
        if (bytes.length > AVATAR_MAX_BYTES) {
            throw new BusinessException("头像大小不能超过5MB");
        }

        try {
            Path dirPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            Path filePath = dirPath.resolve(fileName);
            Files.write(filePath, bytes);
            return "/uploads/avatars/" + fileName;
        } catch (IOException e) {
            throw new BusinessException("头像保存失败: " + e.getMessage());
        }
    }

    private String detectImageExtension(String header) {
        if (header.startsWith("data:image/png")) {
            return ".png";
        }
        if (header.startsWith("data:image/jpeg") || header.startsWith("data:image/jpg")) {
            return ".jpg";
        }
        if (header.startsWith("data:image/gif")) {
            return ".gif";
        }
        if (header.startsWith("data:image/webp")) {
            return ".webp";
        }
        if (header.startsWith("data:image/bmp")) {
            return ".bmp";
        }
        throw new BusinessException("头像图片格式不支持");
    }

    @Override
    @Transactional
    public User updateRole(Long operatorId, Long targetId, String roleStr) {
        assertSuperAdmin(operatorId);
        User target = getUserById(targetId);
        UserRole newRole;
        try {
            newRole = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的角色: " + roleStr);
        }
        if (newRole == UserRole.SUPER_ADMIN) {
            for (User user : userMapper.findByRole(UserRole.SUPER_ADMIN)) {
                if (!user.getId().equals(targetId)) {
                    user.setRole(UserRole.ADMIN);
                    userMapper.update(user);
                }
            }
            target.setRole(UserRole.SUPER_ADMIN);
            userMapper.update(target);
            return getUserById(targetId);
        }
        if (target.getRole() == UserRole.SUPER_ADMIN && userMapper.countByRole(UserRole.SUPER_ADMIN) <= 1) {
            throw new BusinessException("须先指定另一名超级管理员并转让角色后，当前账号才可降级");
        }
        target.setRole(newRole);
        if (newRole != UserRole.ADMIN) {
            target.setAdminMenuKeys(null);
        }
        userMapper.update(target);
        return getUserById(targetId);
    }

    @Override
    @Transactional
    public User updateAdminModules(Long operatorId, Long adminUserId, List<String> moduleKeys) {
        assertSuperAdmin(operatorId);
        User admin = getUserById(adminUserId);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new BusinessException("只能为普通管理员配置可访问模块");
        }
        if (moduleKeys == null) {
            admin.setAdminMenuKeys(null);
            userMapper.update(admin);
            return getUserById(adminUserId);
        }
        LinkedHashSet<String> ok = new LinkedHashSet<>();
        for (String key : moduleKeys) {
            if (key != null && AdminAssignableModules.KEYS.contains(key.trim())) {
                ok.add(key.trim());
            }
        }
        try {
            admin.setAdminMenuKeys(objectMapper.writeValueAsString(new ArrayList<>(ok)));
        } catch (JsonProcessingException e) {
            throw new BusinessException("模块列表序列化失败");
        }
        userMapper.update(admin);
        return getUserById(adminUserId);
    }

    @Override
    public void deleteUser(Long operatorId, Long targetId) {
        // 暂未实现
    }

    @Override
    public List<String> listAssignableModuleKeys(Long operatorId) {
        assertSuperAdmin(operatorId);
        return new ArrayList<>(AdminAssignableModules.KEYS);
    }

    @Override
    public UserPublicProfile getPublicProfile(Long userId) {
        return toPublicProfile(getUserById(userId));
    }

    @Override
    public List<UserPublicProfile> searchPeerUsers(Long currentUserId, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String keyword = query.trim();
        if (keyword.length() > 50) {
            keyword = keyword.substring(0, 50);
        }
        return userMapper.findTop30ByRoleAndIdNotAndUsernameContainingIgnoreCaseOrderByUsernameAsc(
                        UserRole.USER, currentUserId, keyword)
                .stream()
                .map(this::toPublicProfile)
                .toList();
    }

    @Override
    public ContactRequestView sendContactRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new BusinessException("不能向自己发送申请");
        }
        getUserById(fromUserId);
        User to = getUserById(toUserId);
        if (to.getRole() != UserRole.USER) {
            throw new BusinessException("仅可向其他普通用户发送申请");
        }
        if (areMutualContacts(fromUserId, toUserId)) {
            throw new BusinessException("你们已是联系人");
        }
        ContactRequest reverse = contactRequestMapper.findByFromUserIdAndToUserIdAndStatus(
                toUserId, fromUserId, ContactRequestStatus.PENDING);
        if (reverse != null) {
            acceptPendingRequest(reverse);
            ContactRequest current = contactRequestMapper.findByFromUserIdAndToUserIdAndStatus(
                    fromUserId, toUserId, ContactRequestStatus.PENDING);
            if (current != null) {
                finalizeAcceptedDuplicate(current);
            }
            return new ContactRequestView(reverse.getId(), toPublicProfile(to), ContactRequestStatus.ACCEPTED.name(), reverse.getCreatedAt());
        }
        if (contactRequestMapper.findByFromUserIdAndToUserIdAndStatus(fromUserId, toUserId, ContactRequestStatus.PENDING) != null) {
            throw new BusinessException("已发送申请，请等待对方同意");
        }
        ContactRequest req = new ContactRequest();
        req.setFromUserId(fromUserId);
        req.setToUserId(toUserId);
        req.setStatus(ContactRequestStatus.PENDING);
        req.setCreatedAt(LocalDateTime.now());
        contactRequestMapper.insert(req);
        return new ContactRequestView(req.getId(), toPublicProfile(to), ContactRequestStatus.PENDING.name(), req.getCreatedAt());
    }

    @Override
    public void acceptContactRequest(Long requestId, Long accepterUserId) {
        ContactRequest req = getContactRequest(requestId);
        if (req.getStatus() != ContactRequestStatus.PENDING) {
            throw new BusinessException("该申请已处理");
        }
        if (!req.getToUserId().equals(accepterUserId)) {
            throw new BusinessException("无权处理该申请");
        }
        acceptPendingRequest(req);
        ContactRequest reverse = contactRequestMapper.findByFromUserIdAndToUserIdAndStatus(
                req.getToUserId(), req.getFromUserId(), ContactRequestStatus.PENDING);
        if (reverse != null) {
            finalizeAcceptedDuplicate(reverse);
        }
    }

    @Override
    public void rejectContactRequest(Long requestId, Long accepterUserId) {
        ContactRequest req = getContactRequest(requestId);
        if (req.getStatus() != ContactRequestStatus.PENDING) {
            throw new BusinessException("该申请已处理");
        }
        if (!req.getToUserId().equals(accepterUserId)) {
            throw new BusinessException("无权处理该申请");
        }
        req.setStatus(ContactRequestStatus.REJECTED);
        req.setRespondedAt(LocalDateTime.now());
        contactRequestMapper.update(req);
    }

    @Override
    public List<ContactRequestView> listIncomingContactRequests(Long userId) {
        getUserById(userId);
        return contactRequestMapper.findByToUserIdAndStatusOrderByCreatedAtDesc(userId, ContactRequestStatus.PENDING)
                .stream()
                .map(r -> new ContactRequestView(r.getId(), toPublicProfile(getUserById(r.getFromUserId())), r.getStatus().name(), r.getCreatedAt()))
                .toList();
    }

    @Override
    public List<ContactRequestView> listOutgoingContactRequests(Long userId) {
        getUserById(userId);
        return contactRequestMapper.findByFromUserIdAndStatusOrderByCreatedAtDesc(userId, ContactRequestStatus.PENDING)
                .stream()
                .map(r -> new ContactRequestView(r.getId(), toPublicProfile(getUserById(r.getToUserId())), r.getStatus().name(), r.getCreatedAt()))
                .toList();
    }

    @Override
    @Transactional
    public void removePeerContact(Long ownerId, Long peerId) {
        if (ownerId.equals(peerId)) {
            throw new BusinessException("无效操作");
        }
        getUserById(ownerId);
        User peer = getUserById(peerId);
        if (peer.getRole() == UserRole.ADMIN || peer.getRole() == UserRole.SUPER_ADMIN) {
            throw new BusinessException("管理员不可从列表中删除");
        }
        boolean had = userContactMapper.existsByOwnerIdAndContactId(ownerId, peerId)
                || userContactMapper.existsByOwnerIdAndContactId(peerId, ownerId);
        if (!had) {
            throw new BusinessException("联系人关系不存在");
        }
        userContactMapper.deleteByOwnerIdAndContactId(ownerId, peerId);
        userContactMapper.deleteByOwnerIdAndContactId(peerId, ownerId);
    }

    @Override
    public List<UserPublicProfile> listMyContacts(Long ownerId) {
        getUserById(ownerId);
        return userContactMapper.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(uc -> userMapper.findById(uc.getContactId()))
                .filter(Objects::nonNull)
                .map(this::toPublicProfile)
                .toList();
    }

    @Override
    public List<User> listMessageContactsForUser(Long ownerId) {
        getUserById(ownerId);
        LinkedHashSet<Long> seen = new LinkedHashSet<>();
        List<User> out = new ArrayList<>();
        for (User user : getAdminUsers()) {
            if (!user.getId().equals(ownerId) && seen.add(user.getId())) {
                out.add(user);
            }
        }
        for (UserContact contact : userContactMapper.findByOwnerIdOrderByCreatedAtDesc(ownerId)) {
            User peer = userMapper.findById(contact.getContactId());
            if (peer != null && !peer.getId().equals(ownerId) && seen.add(peer.getId())) {
                out.add(peer);
            }
        }
        return out;
    }

    private void assertSuperAdmin(Long operatorId) {
        User operator = getUserById(operatorId);
        if (operator.getRole() != UserRole.SUPER_ADMIN) {
            throw new BusinessException("仅超级管理员可操作");
        }
    }

    private void acceptPendingRequest(ContactRequest req) {
        req.setStatus(ContactRequestStatus.ACCEPTED);
        req.setRespondedAt(LocalDateTime.now());
        contactRequestMapper.update(req);
        addBidirectionalContacts(req.getFromUserId(), req.getToUserId());
    }

    private void finalizeAcceptedDuplicate(ContactRequest req) {
        if (req.getStatus() == ContactRequestStatus.PENDING) {
            req.setStatus(ContactRequestStatus.ACCEPTED);
            req.setRespondedAt(LocalDateTime.now());
            contactRequestMapper.update(req);
        }
    }

    private boolean areMutualContacts(Long a, Long b) {
        return userContactMapper.existsByOwnerIdAndContactId(a, b)
                || userContactMapper.existsByOwnerIdAndContactId(b, a);
    }

    private void addBidirectionalContacts(Long userIdA, Long userIdB) {
        addOneWayContact(userIdA, userIdB);
        addOneWayContact(userIdB, userIdA);
    }

    private void addOneWayContact(Long ownerId, Long contactId) {
        if (ownerId.equals(contactId)) {
            return;
        }
        if (!userContactMapper.existsByOwnerIdAndContactId(ownerId, contactId)) {
            userContactMapper.insert(new UserContact(null, ownerId, contactId, LocalDateTime.now()));
        }
    }

    private ContactRequest getContactRequest(Long requestId) {
        ContactRequest req = contactRequestMapper.findById(requestId);
        if (req == null) {
            throw new BusinessException("申请不存在");
        }
        return req;
    }

    private UserPublicProfile toPublicProfile(User u) {
        return new UserPublicProfile(
                u.getId(),
                u.getUsername(),
                u.getAvatar(),
                u.getRole().name(),
                u.getBio(),
                u.getCreatedAt());
    }
}