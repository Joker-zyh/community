package com.henu.community.service;

import com.henu.community.pojo.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User findUserById(Integer userId);
}
