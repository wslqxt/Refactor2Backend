package com.sys.ecomerce.service.impl;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.entity.UserAddress;
import com.sys.ecomerce.mapper.UserAddressMapper;
import com.sys.ecomerce.mapper.UserMapper;
import com.sys.ecomerce.model.UserAddressRequest;
import com.sys.ecomerce.service.UserAddressService;
import commons.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;
    private final UserMapper userMapper;

    @Override
    public List<UserAddress> listByUserId(Long userId) {
        getUser(userId);
        return userAddressMapper.findByUserIdOrderByDefaultAddrDescCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public UserAddress create(UserAddressRequest req) {
        User user = getUser(req.getUserId());
        validateFields(req);
        UserAddress address = new UserAddress();
        address.setUserId(user.getId());
        fill(address, req);
        boolean first = userAddressMapper.countByUserId(user.getId()) == 0;
        boolean asDefault = Boolean.TRUE.equals(req.getDefaultAddr()) || first;
        if (asDefault) {
            clearDefault(user.getId());
        }
        address.setDefaultAddr(asDefault);
        address.setCreatedAt(LocalDateTime.now());
        userAddressMapper.insert(address);
        return getAddress(address.getId());
    }

    @Override
    @Transactional
    public UserAddress update(Long addressId, UserAddressRequest req) {
        User user = getUser(req.getUserId());
        UserAddress address = getAddress(addressId);
        if (!address.getUserId().equals(user.getId())) {
            throw new BusinessException("无权修改该地址");
        }
        validateFields(req);
        fill(address, req);
        if (Boolean.TRUE.equals(req.getDefaultAddr())) {
            clearDefault(user.getId());
            address.setDefaultAddr(true);
        }
        userAddressMapper.update(address);
        return getAddress(addressId);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long addressId) {
        getUser(userId);
        UserAddress address = getAddress(addressId);
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该地址");
        }
        boolean wasDefault = address.isDefaultAddr();
        userAddressMapper.deleteById(addressId);
        if (wasDefault) {
            List<UserAddress> rest = userAddressMapper.findByUserIdOrderByDefaultAddrDescCreatedAtDesc(userId);
            if (!rest.isEmpty()) {
                UserAddress pick = rest.get(0);
                pick.setDefaultAddr(true);
                userAddressMapper.update(pick);
            }
        }
    }

    @Override
    @Transactional
    public UserAddress setDefault(Long userId, Long addressId) {
        getUser(userId);
        UserAddress address = getAddress(addressId);
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该地址");
        }
        clearDefault(userId);
        address.setDefaultAddr(true);
        userAddressMapper.update(address);
        return getAddress(addressId);
    }

    @Override
    public UserAddress getOwnedOrThrow(Long userId, Long addressId) {
        UserAddress address = getAddress(addressId);
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("收货地址与当前用户不匹配");
        }
        return address;
    }

    private void clearDefault(Long userId) {
        List<UserAddress> list = userAddressMapper.findByUserIdOrderByDefaultAddrDescCreatedAtDesc(userId);
        for (UserAddress address : list) {
            if (address.isDefaultAddr()) {
                address.setDefaultAddr(false);
                userAddressMapper.update(address);
            }
        }
    }

    private static void fill(UserAddress address, UserAddressRequest req) {
        address.setReceiverName(req.getReceiverName().trim());
        address.setPhone(req.getPhone().trim());
        address.setProvince(req.getProvince().trim());
        address.setCity(req.getCity().trim());
        address.setDistrict(req.getDistrict().trim());
        address.setDetail(req.getDetail().trim());
    }

    private static void validateFields(UserAddressRequest req) {
        if (req.getReceiverName() == null || req.getReceiverName().isBlank()) {
            throw new BusinessException("请填写收货人");
        }
        if (req.getPhone() == null || req.getPhone().isBlank()) {
            throw new BusinessException("请填写手机号");
        }
        if (req.getProvince() == null || req.getProvince().isBlank()) {
            throw new BusinessException("请填写省");
        }
        if (req.getCity() == null || req.getCity().isBlank()) {
            throw new BusinessException("请填写市");
        }
        if (req.getDistrict() == null || req.getDistrict().isBlank()) {
            throw new BusinessException("请填写区/县");
        }
        if (req.getDetail() == null || req.getDetail().isBlank()) {
            throw new BusinessException("请填写详细地址");
        }
    }

    private User getUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("缺少用户身份");
        }
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private UserAddress getAddress(Long addressId) {
        UserAddress address = userAddressMapper.findById(addressId);
        if (address == null) {
            throw new BusinessException("地址不存在");
        }
        return address;
    }
}
