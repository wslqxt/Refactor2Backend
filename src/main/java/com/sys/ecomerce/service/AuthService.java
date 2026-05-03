package com.sys.ecomerce.service;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.model.ChangePasswordRequest;
import com.sys.ecomerce.model.RegisterRequest;
import com.sys.ecomerce.model.ResetPasswordRequest;

public interface AuthService {

    User register(RegisterRequest request);

    User resetPassword(ResetPasswordRequest request);

    User changePassword(Long id, ChangePasswordRequest request);
}
