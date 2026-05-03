package com.sys.ecomerce.model;

import lombok.Data;

@Data
public class UserAddressRequest {
    private Long userId;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Boolean defaultAddr;
}
