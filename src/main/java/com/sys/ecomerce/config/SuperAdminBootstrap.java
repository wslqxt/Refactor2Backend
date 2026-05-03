package com.sys.ecomerce.config;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.enums.UserRole;
import com.sys.ecomerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperAdminBootstrap implements CommandLineRunner {

    private final UserMapper userMapper;

    @Value("${app.super-admin-username:}")
    private String superAdminUsername;

    @Override
    public void run(String... args) {
        if (superAdminUsername == null || superAdminUsername.isBlank()) {
            return;
        }
        if (userMapper.countByRole(UserRole.SUPER_ADMIN) > 0) {
            return;
        }
        String name = superAdminUsername.trim();
        User user = userMapper.findByUsername(name);
        if (user == null) {
            log.warn("配置了 app.super-admin-username={} 但该用户不存在，跳过超管初始化", name);
            return;
        }
        user.setRole(UserRole.SUPER_ADMIN);
        userMapper.update(user);
        log.info("已提升用户 [{}] 为超级管理员（首次初始化）", name);
    }
}
