package com.hwz.user.dto;

import com.hwz.common.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponse {
    private Long userId;
    private String username;
    private String displayName;
    private User.Role role;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .build();
    }
}
