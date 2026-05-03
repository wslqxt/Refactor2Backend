package com.sys.ecomerce.config;

import commons.security.RsaUtils;
import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class PasswordEncryptMigration implements CommandLineRunner {

    private final UserMapper userMapper;
    private final RsaUtils rsaUtils;

    @Override
    public void run(String... args) {
        log.info("开始检查密码加密状态...");

        List<User> users = userMapper.findAll();
        int migratedCount = 0;
        int skippedCount = 0;

        for (User user : users) {
            try {
                // 跳过已经是 RSA 加密的密码
                if (user.getPassword() != null && user.getPassword().length() > 200) {
                    log.debug("用户 {} 的密码已加密，跳过", user.getUsername());
                    skippedCount++;
                    continue;
                }

                log.info("正在加密用户 {} 的明文密码", user.getUsername());

                String encryptedPassword = rsaUtils.encrypt(user.getPassword());

                user.setPassword(encryptedPassword);
                userMapper.update(user);
                migratedCount++;

            } catch (Exception e) {
                log.error("加密用户 {} 的密码失败: {}", user.getUsername(), e.getMessage());
            }
        }

        log.info("密码迁移完成: 加密 {} 个, 跳过 {} 个", migratedCount, skippedCount);
    }
}