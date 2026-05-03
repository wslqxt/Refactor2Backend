package com.sys.ecomerce.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContact {

    private Long id;
    private Long ownerId;
    private Long contactId;
    private LocalDateTime createdAt;
}
