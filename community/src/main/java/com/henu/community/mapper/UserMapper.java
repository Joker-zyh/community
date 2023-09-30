package com.henu.community.mapper;

import com.henu.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectUserById(Integer userId);

    User selectUserByUsername(String username);

    User selectUserByEmail(String email);

    void insertUser(User user);

}
