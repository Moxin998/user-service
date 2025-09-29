package com.moxin.moxincreateuser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moxin.moxincreateuser.domain.User;

/**
 * @author mo_xi
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-09-29 14:05:23
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    String getEncryptPassword(String userPassword);
}
