package com.sys.ecomerce.service;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.model.ContactRequestView;
import com.sys.ecomerce.model.LoginRequest;
import com.sys.ecomerce.model.RegisterRequest;
import com.sys.ecomerce.model.ResetPasswordRequest;
import com.sys.ecomerce.model.UserPublicProfile;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    User login(LoginRequest request);

    User resetPassword(ResetPasswordRequest request);

    User getUserById(Long id);

    List<User> getAllUsersForStaff(Long operatorId);

    List<User> getAdminUsers();

    List<User> listAdminsForModuleConfig(Long operatorId);

    User updateProfile(Long id, String username, String email, String avatar, String bio);

    User changePassword(Long id, String oldPassword, String newPassword);

    User updateRole(Long operatorId, Long targetId, String roleStr);

    User updateAdminModules(Long operatorId, Long adminUserId, List<String> moduleKeys);

    void deleteUser(Long operatorId, Long targetId);

    List<String> listAssignableModuleKeys(Long operatorId);

    UserPublicProfile getPublicProfile(Long userId);

    List<UserPublicProfile> searchPeerUsers(Long currentUserId, String query);

    ContactRequestView sendContactRequest(Long fromUserId, Long toUserId);

    void acceptContactRequest(Long requestId, Long accepterUserId);

    void rejectContactRequest(Long requestId, Long accepterUserId);

    List<ContactRequestView> listIncomingContactRequests(Long userId);

    List<ContactRequestView> listOutgoingContactRequests(Long userId);

    void removePeerContact(Long ownerId, Long peerId);

    List<UserPublicProfile> listMyContacts(Long ownerId);

    List<User> listMessageContactsForUser(Long ownerId);
}
