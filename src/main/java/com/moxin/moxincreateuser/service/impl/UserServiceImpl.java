package com.moxin.moxincreateuser.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxin.moxincreateuser.domain.User;
import com.moxin.moxincreateuser.domain.vo.LoginUserVO;
import com.moxin.moxincreateuser.exception.BusinessException;
import com.moxin.moxincreateuser.exception.ErrorCode;
import com.moxin.moxincreateuser.mapper.UserMapper;
import com.moxin.moxincreateuser.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.moxin.moxincreateuser.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author mo_xi
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-09-29 14:05:23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {


    // 依赖注入
    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        // 1. 参数校验
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和确认密码不匹配");
        }

        // 2. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);

        if (count > 1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "账号重复");
        }

        // 3. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);


        // 4. 用户不存在，插入用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);

        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，数据库错误");
        }

        // 5. 注册成功  返回用户 id
        return user.getId();
    }

    /**
     * 密码加密
     *
     * @param userPassword 密码
     * @return md5加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "moxin";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    /**
     * 用户信息脱敏
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();

        // 这里会把 user 对象中的属性值拷贝到 loginUserVO 对象中
        BeanUtil.copyProperties(user, loginUserVO);

        return loginUserVO;
    }

    /**
     * 用户注销
     *
     * @param request 请求
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 1. 判断是否登录
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);

        if (object == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    /**
     * 根据用户名查询用户
     *
     * @param userName 用户名
     * @return
     */
    @Override
    public LoginUserVO searchUserName(String userName) {
        // 1. 参数校验
        if(StrUtil.hasBlank(userName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 2. 根据用户名查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("userName", userName);
        User user = userMapper.selectOne(queryWrapper);

        // 3. 用户不存在，抛出异常
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        // 4. 返回脱敏后的用户信息
        return this.getLoginUserVO(user);
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求
     * @return 登录后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 参数校验
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号过短");
        }

        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码过短");
        }


        // 2. 密码加密
        String encryptPassword = getEncryptPassword(userPassword);


        // 3. 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);

        User user = userMapper.selectOne(queryWrapper);

        // 4. 用户不存在，抛出异常
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }


        // 5. 用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);


        // 6. 返回脱敏后的用户信息

        return this.getLoginUserVO(user);
    }


    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {

        // 1. 先判断是否已经登录
        Object object = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) object;

        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 如果追求性能，可以注释掉下面的代码直接返回 currentUser
        // 2. 从数据库查询用户信息
        Long userId = currentUser.getId();
        currentUser = this.getById(userId);

        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户不存在");
        }

        return currentUser;
    }
}




