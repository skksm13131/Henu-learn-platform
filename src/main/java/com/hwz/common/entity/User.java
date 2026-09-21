package com.hwz.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** user_id bigint auto_increment */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("display_name")
    private String displayName;

    @TableField("email")
    private String email;

    @TableField("real_name")
    private String realName;

    @TableField("grade")
    private String grade;

    @TableField("role")
    private Role role;

    @TableField("status")
    private Status status;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    public enum Role {
        ADMIN, USER
    }

    public enum Status {
        ACTIVE, INACTIVE, LOCKED
    }
}
