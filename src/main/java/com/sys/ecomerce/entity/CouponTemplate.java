package com.sys.ecomerce.entity;

import com.sys.ecomerce.enums.CouponDiscountType;
import com.sys.ecomerce.enums.CouponScopeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplate {

    private Long id;
    private String name;
    private String description;
    private CouponDiscountType discountType;
    private BigDecimal discountValue;
    private CouponScopeType scopeType;
    private Long categoryId;
    private List<Long> categoryIds = new ArrayList<>();
    private BigDecimal minOrderAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
