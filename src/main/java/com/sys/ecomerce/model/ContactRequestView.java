package com.sys.ecomerce.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestView {
    private Long id;
    private UserPublicProfile counterparty;
    private String status;
    private LocalDateTime createdAt;
}
