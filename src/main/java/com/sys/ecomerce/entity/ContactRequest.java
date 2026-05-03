package com.sys.ecomerce.entity;

import com.sys.ecomerce.enums.ContactRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private ContactRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
