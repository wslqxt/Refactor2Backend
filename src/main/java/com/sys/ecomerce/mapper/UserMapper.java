package com.sys.ecomerce.mapper;

import com.sys.ecomerce.entity.User;
import com.sys.ecomerce.enums.UserRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at FROM sys_user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at FROM sys_user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at FROM sys_user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT COUNT(1) > 0 FROM sys_user WHERE username = #{username}")
    boolean existsByUsername(String username);

    @Select("SELECT COUNT(1) > 0 FROM sys_user WHERE email = #{email}")
    boolean existsByEmail(String email);

    @Select("SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at FROM sys_user ORDER BY id DESC")
    List<User> findAll();

    @Select("SELECT COUNT(1) FROM sys_user")
    long countAll();

    @Select("SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at FROM sys_user WHERE role = #{role} ORDER BY id DESC")
    List<User> findByRole(UserRole role);

    @Select("SELECT COUNT(1) FROM sys_user WHERE role = #{role}")
    long countByRole(UserRole role);

    @Select("""
            SELECT id, username, password, email, avatar, bio, role, admin_menu_keys, created_at
            FROM sys_user
            WHERE role = #{role}
              AND id <> #{id}
              AND LOWER(username) LIKE LOWER(CONCAT('%', #{username}, '%'))
            ORDER BY username ASC
            LIMIT 30
            """)
    List<User> findTop30ByRoleAndIdNotAndUsernameContainingIgnoreCaseOrderByUsernameAsc(
            @Param("role") UserRole role,
            @Param("id") Long id,
            @Param("username") String username);

    @Insert("""
            INSERT INTO sys_user(username, password, email, avatar, bio, role, admin_menu_keys, created_at)
            VALUES(#{username}, #{password}, #{email}, #{avatar}, #{bio}, #{role}, #{adminMenuKeys}, #{createdAt})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("""
            UPDATE sys_user
            SET username = #{username},
                password = #{password},
                email = #{email},
                avatar = #{avatar},
                bio = #{bio},
                role = #{role},
                admin_menu_keys = #{adminMenuKeys}
            WHERE id = #{id}
            """)
    int update(User user);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}
