package com.sys.ecomerce.service;

import com.sys.ecomerce.entity.UserAddress;
import com.sys.ecomerce.model.UserAddressRequest;

import java.util.List;

public interface UserAddressService {

    List<UserAddress> listByUserId(Long userId);

    UserAddress create(UserAddressRequest req);

    UserAddress update(Long addressId, UserAddressRequest req);

    void delete(Long userId, Long addressId);

    UserAddress setDefault(Long userId, Long addressId);

    UserAddress getOwnedOrThrow(Long userId, Long addressId);
}
