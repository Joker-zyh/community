package com.henu.community;

import com.henu.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class SensitiveTest {

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String s = "这个人爱赌博，嫖娼和吸毒，△f△a△b△c△";
        System.out.println(sensitiveFilter.filter(s));
    }
}
