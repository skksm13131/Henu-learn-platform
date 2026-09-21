package com.hwz.admin.service;

import com.hwz.common.context.BaseContext;
import com.hwz.common.entity.User;
import com.hwz.common.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminAccessService {

    private final UserMapper userMapper;

    public AdminAccessService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User requireUser() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在，请重新登录");
        }
        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号已被停用，请联系管理员");
        }
        return user;
    }

    public User requireAdmin() {
        User user = requireUser();
        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅管理员可以访问该功能");
        }
        return user;
    }
}
