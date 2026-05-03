package com.sys.ecomerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfile {
    private Long id;
    private String username;
    private String avatar;
    private String role;
    private String bio;
    private LocalDateTime createdAt;
}
