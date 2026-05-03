package com.sys.ecomerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sys.ecomerce.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String avatar;
    private String bio;
    private UserRole role;
    private String adminMenuKeys;
    private LocalDateTime createdAt;
}
