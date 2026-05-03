package com.sys.ecomerce.mapper;

import com.sys.ecomerce.entity.UserCoupon;
import com.sys.ecomerce.enums.UserCouponStatus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserCouponMapper {

    @Select("""
            SELECT id, template_id, user_id, status, reserved_order_id, obtained_at, expires_at
            FROM user_coupon
            WHERE id = #{id}
            """)
    @Results(id = "userCouponResult", value = {
            @Result(column = "template_id", property = "templateId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "reserved_order_id", property = "reservedOrderId"),
            @Result(column = "obtained_at", property = "obtainedAt"),
            @Result(column = "expires_at", property = "expiresAt"),
            @Result(column = "template_id", property = "template",
                    one = @One(select = "com.sys.ecomerce.mapper.CouponTemplateMapper.findById")),
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.sys.ecomerce.mapper.UserMapper.findById"))
    })
    UserCoupon findById(Long id);

    @Select("""
            SELECT id, template_id, user_id, status, reserved_order_id, obtained_at, expires_at
            FROM user_coupon
            WHERE id = #{id}
            FOR UPDATE
            """)
    @Results(value = {
            @Result(column = "template_id", property = "templateId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "reserved_order_id", property = "reservedOrderId"),
            @Result(column = "obtained_at", property = "obtainedAt"),
            @Result(column = "expires_at", property = "expiresAt"),
            @Result(column = "template_id", property = "template",
                    one = @One(select = "com.sys.ecomerce.mapper.CouponTemplateMapper.findById")),
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.sys.ecomerce.mapper.UserMapper.findById"))
    })
    UserCoupon findByIdForUpdate(Long id);

    @Select("""
            SELECT id, template_id, user_id, status, reserved_order_id, obtained_at, expires_at
            FROM user_coupon
            WHERE user_id = #{userId} AND status = #{status}
            ORDER BY expires_at ASC, id ASC
            """)
    @Results(value = {
            @Result(column = "template_id", property = "templateId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "reserved_order_id", property = "reservedOrderId"),
            @Result(column = "obtained_at", property = "obtainedAt"),
            @Result(column = "expires_at", property = "expiresAt"),
            @Result(column = "template_id", property = "template",
                    one = @One(select = "com.sys.ecomerce.mapper.CouponTemplateMapper.findById")),
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.sys.ecomerce.mapper.UserMapper.findById"))
    })
    List<UserCoupon> findByUserIdAndStatusOrderByExpiresAtAsc(@Param("userId") Long userId,
                                                               @Param("status") UserCouponStatus status);

    @Select("""
            <script>
            SELECT id, template_id, user_id, status, reserved_order_id, obtained_at, expires_at
            FROM user_coupon
            WHERE status IN
            <foreach collection="statuses" item="status" open="(" separator="," close=")">
                #{status}
            </foreach>
            ORDER BY id DESC
            </script>
            """)
    @Results(value = {
            @Result(column = "template_id", property = "templateId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "reserved_order_id", property = "reservedOrderId"),
            @Result(column = "obtained_at", property = "obtainedAt"),
            @Result(column = "expires_at", property = "expiresAt"),
            @Result(column = "template_id", property = "template",
                    one = @One(select = "com.sys.ecomerce.mapper.CouponTemplateMapper.findById")),
            @Result(column = "user_id", property = "user",
                    one = @One(select = "com.sys.ecomerce.mapper.UserMapper.findById"))
    })
    List<UserCoupon> findByStatusesOrderByIdDesc(@Param("statuses") Collection<UserCouponStatus> statuses);

    @Select("SELECT COUNT(1) FROM user_coupon WHERE template_id = #{templateId}")
    long countByTemplateId(Long templateId);

    @Select("SELECT COUNT(1) FROM user_coupon WHERE status = #{status}")
    long countByStatus(UserCouponStatus status);

    @Select("SELECT COUNT(1) FROM user_coupon")
    long countAll();

    @Insert("""
            INSERT INTO user_coupon(template_id, user_id, status, reserved_order_id, obtained_at, expires_at)
            VALUES(#{templateId}, #{userId}, #{status}, #{reservedOrderId}, #{obtainedAt}, #{expiresAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);

    @Update("""
            UPDATE user_coupon
            SET template_id = #{templateId},
                user_id = #{userId},
                status = #{status},
                reserved_order_id = #{reservedOrderId},
                obtained_at = #{obtainedAt},
                expires_at = #{expiresAt}
            WHERE id = #{id}
            """)
    int update(UserCoupon userCoupon);

    @Delete("DELETE FROM user_coupon WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
