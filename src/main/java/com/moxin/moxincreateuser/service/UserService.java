package com.moxin.moxincreateuser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moxin.moxincreateuser.domain.User;
import com.moxin.moxincreateuser.domain.vo.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);


    /**
     * 用户信息脱敏
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);


    /**
     * 用户注销
     *
     * @param request 请求
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return 用户信息
     */
    LoginUserVO searchUserName(String userName);


    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求
     * @return 登录后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);
}
