package com.sys.ecomerce.mapper;

import com.sys.ecomerce.entity.ContactRequest;
import com.sys.ecomerce.enums.ContactRequestStatus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ContactRequestMapper {

    @Select("""
            SELECT id, from_user_id, to_user_id, status, created_at, responded_at
            FROM contact_request
            WHERE to_user_id = #{toUserId} AND status = #{status}
            ORDER BY created_at DESC, id DESC
            """)
    List<ContactRequest> findByToUserIdAndStatusOrderByCreatedAtDesc(
            @Param("toUserId") Long toUserId,
            @Param("status") ContactRequestStatus status);

    @Select("""
            SELECT id, from_user_id, to_user_id, status, created_at, responded_at
            FROM contact_request
            WHERE from_user_id = #{fromUserId} AND status = #{status}
            ORDER BY created_at DESC, id DESC
            """)
    List<ContactRequest> findByFromUserIdAndStatusOrderByCreatedAtDesc(
            @Param("fromUserId") Long fromUserId,
            @Param("status") ContactRequestStatus status);

    @Select("""
            SELECT id, from_user_id, to_user_id, status, created_at, responded_at
            FROM contact_request
            WHERE from_user_id = #{fromUserId} AND to_user_id = #{toUserId} AND status = #{status}
            """)
    ContactRequest findByFromUserIdAndToUserIdAndStatus(
            @Param("fromUserId") Long fromUserId,
            @Param("toUserId") Long toUserId,
            @Param("status") ContactRequestStatus status);

    @Select("""
            SELECT id, from_user_id, to_user_id, status, created_at, responded_at
            FROM contact_request
            WHERE id = #{id}
            """)
    ContactRequest findById(Long id);

    @Insert("""
            INSERT INTO contact_request(from_user_id, to_user_id, status, created_at, responded_at)
            VALUES(#{fromUserId}, #{toUserId}, #{status}, #{createdAt}, #{respondedAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactRequest request);

    @Update("""
            UPDATE contact_request
            SET status = #{status},
                responded_at = #{respondedAt}
            WHERE id = #{id}
            """)
    int update(ContactRequest request);

    @Delete("DELETE FROM contact_request WHERE from_user_id = #{uid} OR to_user_id = #{uid}")
    int deleteAllInvolvingUser(@Param("uid") Long uid);
}
