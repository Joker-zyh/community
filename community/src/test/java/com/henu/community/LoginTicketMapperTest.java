package com.henu.community;

import com.henu.community.mapper.LoginTicketMapper;
import com.henu.community.pojo.LoginTicket;
import com.henu.community.util.GenerateUUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
public class LoginTicketMapperTest {
    @Resource
    private LoginTicketMapper mapper;

    @Test
    public void testInsert(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(111);
        loginTicket.setTicket(GenerateUUID.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date());
        mapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelect(){
        System.out.println(mapper.selectByTicket("cccf7a119c634097bba2cc836317321c"));
    }

    @Test
    public void testUpdate(){
        mapper.updateStatusByTicket("cccf7a119c634097bba2cc836317321c",1);
    }
}
