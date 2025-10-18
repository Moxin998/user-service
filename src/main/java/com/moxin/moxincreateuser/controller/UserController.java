package com.moxin.moxincreateuser.controller;


import com.moxin.moxincreateuser.common.BaseResponse;
import com.moxin.moxincreateuser.common.ResultUtils;
import com.moxin.moxincreateuser.common.ThrowUtils;
import com.moxin.moxincreateuser.domain.User;
import com.moxin.moxincreateuser.domain.dto.UserLoginRequest;
import com.moxin.moxincreateuser.domain.dto.UserRegisterRequest;
import com.moxin.moxincreateuser.domain.vo.LoginUserVO;
import com.moxin.moxincreateuser.exception.ErrorCode;
import com.moxin.moxincreateuser.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;


    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求体
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(UserRegisterRequest userRegisterRequest) {
        // 1. 参数校验
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.NOT_FOUND_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求体
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 1. 参数校验
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.NOT_FOUND_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();


        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/currentUser")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 1. 获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 返回脱敏后的用户信息
        LoginUserVO loginUserVO = userService.getLoginUserVO(loginUser);

        return ResultUtils.success(loginUserVO);
    }


    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<LoginUserVO> searchUserName(String username) {
        // 1. 参数校验
        ThrowUtils.throwIf(username == null, ErrorCode.NOT_FOUND_ERROR,"参数为空");
        LoginUserVO loginUserVO = userService.searchUserName(username);
        return ResultUtils.success(loginUserVO);
    }


}
