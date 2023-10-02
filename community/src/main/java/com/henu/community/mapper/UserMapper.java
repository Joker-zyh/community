package com.henu.community.mapper;

import com.henu.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectUserById(Integer userId);

    User selectUserByUsername(String username);

    User selectUserByEmail(String email);

    void insertUser(User user);

    void updateUserStatus(@Param("id")Integer id,@Param("status") Integer status);

    void updateHeaderUrlById(@Param("id") Integer id, @Param("headerUrl") String headerUrl);

    void updatePasswordById(@Param("id")Integer id,@Param("password")String password);

}
