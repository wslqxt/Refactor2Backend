package com.sys.ecomerce.controller;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.model.ChangePasswordRequest;
import com.sys.ecomerce.model.RegisterRequest;
import com.sys.ecomerce.model.ResetPasswordRequest;
import com.sys.ecomerce.service.AuthService;
import commons.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success("注册成功", authService.register(request));
    }

    @PostMapping("/reset-password")
    public Result<User> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return Result.success("密码重置成功", authService.resetPassword(request));
    }

    @PutMapping("/password/{id}")
    public Result<User> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        return Result.success("密码修改成功", authService.changePassword(id, request));
    }
}
