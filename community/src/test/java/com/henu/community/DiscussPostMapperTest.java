package com.henu.community;

import com.henu.community.mapper.DiscussPostMapper;
import com.henu.community.pojo.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DiscussPostMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testUpdateCommentCount(){
        discussPostMapper.updateCommentCount(284,0);
    }

    @Test
    public void testSelectOneById(){
        System.out.println(discussPostMapper.selectDiscussPostById(234));
    }

    @Test
    public void testSelectDiscussPostPage(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPost(null, 10, 5);
        discussPosts.forEach(discussPost -> {
            System.out.println(discussPost);
        });

        System.out.println(discussPostMapper.selectDiscussPostRows(null));
    }

    @Test
    public void testInsert(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setContent("sd");
        discussPostMapper.insertDiscussPost(discussPost);
    }
}
