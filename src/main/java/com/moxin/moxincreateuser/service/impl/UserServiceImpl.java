package com.moxin.moxincreateuser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moxin.moxincreateuser.domain.User;
import com.moxin.moxincreateuser.service.UserService;
import com.moxin.moxincreateuser.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author mo_xi
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-09-29 14:05:23
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




