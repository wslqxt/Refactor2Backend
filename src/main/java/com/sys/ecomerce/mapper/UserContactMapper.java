package com.sys.ecomerce.mapper;

import com.sys.ecomerce.entity.UserContact;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserContactMapper {

    @Select("""
            SELECT id, owner_id, contact_id, created_at
            FROM user_contact
            WHERE owner_id = #{ownerId}
            ORDER BY created_at DESC, id DESC
            """)
    List<UserContact> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    @Select("SELECT COUNT(1) > 0 FROM user_contact WHERE owner_id = #{ownerId} AND contact_id = #{contactId}")
    boolean existsByOwnerIdAndContactId(@Param("ownerId") Long ownerId, @Param("contactId") Long contactId);

    @Insert("""
            INSERT INTO user_contact(owner_id, contact_id, created_at)
            VALUES(#{ownerId}, #{contactId}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserContact userContact);

    @Delete("DELETE FROM user_contact WHERE owner_id = #{ownerId} AND contact_id = #{contactId}")
    int deleteByOwnerIdAndContactId(@Param("ownerId") Long ownerId, @Param("contactId") Long contactId);

    @Delete("DELETE FROM user_contact WHERE owner_id = #{ownerId}")
    int deleteByOwnerId(Long ownerId);

    @Delete("DELETE FROM user_contact WHERE contact_id = #{contactId}")
    int deleteByContactId(Long contactId);
}
