package com.henu.community;

import com.henu.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class MailTest {
    @Resource
    private MailClient mailClient;

    @Test
    public void testTextMail(){
        mailClient.sendMail("839538011@henu.edu.cn","测试","你好");
    }

}
