package com.henu.community;

import com.henu.community.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testUpdatePassword(){
        userMapper.updatePasswordById(117,"123");
    }

    @Test
    public void testUpdateHeaderUrl(){
        userMapper.updateHeaderUrlById(117,"http://images.nowcoder.com/head/45t.png");
    }


    @Test
    public void testSelectUserByUsername(){
        System.out.println(userMapper.selectUserByUsername("niuke"));
    }

    @Test
    public void testSelectUserById(){
        System.out.println(userMapper.selectUserById(11));

    }

    @Test
    public void testUpdate(){
        userMapper.updateUserStatus(158,1);
    }


}
