package com.sys.ecomerce.service.impl;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.enums.UserRole;
import com.sys.ecomerce.mapper.UserMapper;
import com.sys.ecomerce.model.ChangePasswordRequest;
import com.sys.ecomerce.model.RegisterRequest;
import com.sys.ecomerce.model.ResetPasswordRequest;
import com.sys.ecomerce.service.AuthService;
import commons.security.RsaUtils;
import commons.exception.BusinessException;
import commons.result.ResultCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final RsaUtils rsaUtils;

    @Override
    public User register(RegisterRequest request) {
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(rsaUtils.encrypt(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return findById(user.getId());
    }

    @Override
    public User resetPassword(ResetPasswordRequest request) {
        User user = userMapper.findByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(ResultCode.EMAIL_NOT_REGISTERED);
        }
        user.setPassword(rsaUtils.encrypt(request.getNewPassword()));
        userMapper.update(user);
        return findById(user.getId());
    }

    @Override
    public User changePassword(Long id, ChangePasswordRequest request) {
        User user = findById(id);
        if (!rsaUtils.decrypt(user.getPassword()).equals(request.getOldPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }
        user.setPassword(rsaUtils.encrypt(request.getNewPassword()));
        userMapper.update(user);
        return findById(id);
    }

    private User findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }
}
