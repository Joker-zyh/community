package com.henu.community.service;

import com.henu.community.pojo.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface UserService {
    User findUserById(Integer userId);

    Map<String,String> register(User user);

    int activation(int userID,String code);

    void updateHeaderUrl(int id,String headerUrl);

    void updatePassword(int id,String password);
}
