package com.henu.community.service;

import com.henu.community.pojo.DiscussPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DiscussPostService {
    List<DiscussPost> findDiscussPost(Integer userId, int page, int pageSize);
    int findDiscussPostRows(Integer userId);
    void saveDiscussPost(DiscussPost discussPost);
    DiscussPost findDiscussPostById(Integer id);
    void updateCommentCount(int id,int commentCount);
}
