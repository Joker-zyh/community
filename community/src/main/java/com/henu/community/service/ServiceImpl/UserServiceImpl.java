package com.henu.community.service.ServiceImpl;

import com.henu.community.mapper.UserMapper;
import com.henu.community.pojo.User;
import com.henu.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(Integer userId){
        return userMapper.selectUserById(userId);
    }
}
