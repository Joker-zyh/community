package com.henu.community.service.ServiceImpl;


import com.henu.community.mapper.DiscussPostMapper;
import com.henu.community.pojo.DiscussPost;
import com.henu.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPost(Integer userId,int page, int pageSize){
        return discussPostMapper.selectDiscussPost(userId, page, pageSize);
    }

    public int findDiscussPostRows(Integer userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

}
