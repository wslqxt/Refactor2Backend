package com.sys.ecomerce.entity;

import com.sys.ecomerce.enums.UserCouponStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    private Long id;
    private Long templateId;
    private CouponTemplate template;
    private Long userId;
    private User user;
    private UserCouponStatus status;
    private Long reservedOrderId;
    private LocalDateTime obtainedAt;
    private LocalDateTime expiresAt;
}
