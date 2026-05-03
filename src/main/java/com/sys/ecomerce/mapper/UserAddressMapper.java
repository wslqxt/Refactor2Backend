package com.sys.ecomerce.mapper;

import com.sys.ecomerce.entity.UserAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    @Select("""
            SELECT id, user_id, receiver_name, phone, province, city, district, detail, is_default, created_at
            FROM user_address
            WHERE user_id = #{userId}
            ORDER BY is_default DESC, created_at DESC, id DESC
            """)
    List<UserAddress> findByUserIdOrderByDefaultAddrDescCreatedAtDesc(Long userId);

    @Select("SELECT COUNT(1) FROM user_address WHERE user_id = #{userId}")
    long countByUserId(Long userId);

    @Select("""
            SELECT id, user_id, receiver_name, phone, province, city, district, detail, is_default, created_at
            FROM user_address
            WHERE id = #{id}
            """)
    UserAddress findById(Long id);

    @Insert("""
            INSERT INTO user_address(user_id, receiver_name, phone, province, city, district, detail, is_default, created_at)
            VALUES(#{userId}, #{receiverName}, #{phone}, #{province}, #{city}, #{district}, #{detail}, #{defaultAddr}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress address);

    @Update("""
            UPDATE user_address
            SET receiver_name = #{receiverName},
                phone = #{phone},
                province = #{province},
                city = #{city},
                district = #{district},
                detail = #{detail},
                is_default = #{defaultAddr}
            WHERE id = #{id}
            """)
    int update(UserAddress address);

    @Delete("DELETE FROM user_address WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM user_address WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
