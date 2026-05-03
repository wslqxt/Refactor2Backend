package com.sys.ecomerce.controller;

import com.sys.ecomerce.entity.UserAddress;
import com.sys.ecomerce.model.UserAddressRequest;
import com.sys.ecomerce.service.UserAddressService;
import commons.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public Result<List<UserAddress>> list(@RequestParam Long userId) {
        return Result.success(userAddressService.listByUserId(userId));
    }

    @PostMapping
    public Result<UserAddress> create(@RequestBody UserAddressRequest body) {
        return Result.success("已添加地址", userAddressService.create(body));
    }

    @PutMapping("/{id}")
    public Result<UserAddress> update(@PathVariable Long id, @RequestBody UserAddressRequest body) {
        return Result.success("已更新地址", userAddressService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        userAddressService.delete(userId, id);
        return Result.success("已删除地址", null);
    }

    @PutMapping("/{id}/default")
    public Result<UserAddress> setDefault(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success("已设为默认地址", userAddressService.setDefault(userId, id));
    }
}
