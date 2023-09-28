package com.henu.community;

import com.henu.community.mapper.DiscussPostMapper;
import com.henu.community.pojo.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void TestSelectDiscussPost(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(null, 10, 5);
        discussPosts.forEach(discussPost -> {
            System.out.println(discussPost);
        });

        System.out.println(discussPostMapper.selectDiscussPostRows(null));
    }
}
