package com.moxin.moxincreateuser.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxin.moxincreateuser.domain.User;
import com.moxin.moxincreateuser.exception.BusinessException;
import com.moxin.moxincreateuser.exception.ErrorCode;
import com.moxin.moxincreateuser.mapper.UserMapper;
import com.moxin.moxincreateuser.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userAccount.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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

        // 5. 返回用户 id
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
}




