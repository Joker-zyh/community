package com.henu.community.service.ServiceImpl;


import com.henu.community.mapper.DiscussPostMapper;
import com.henu.community.pojo.DiscussPost;
import com.henu.community.service.DiscussPostService;
import com.henu.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    /**
     * 分页查找帖子
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public List<DiscussPost> findDiscussPost(Integer userId,int page, int pageSize){
        return discussPostMapper.selectDiscussPost(userId, page, pageSize);
    }

    /**
     * 帖子总数
     * @param userId
     * @return
     */
    public int findDiscussPostRows(Integer userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 添加帖子
     * @param discussPost
     */
    @Override
    public void saveDiscussPost(DiscussPost discussPost) {
        //转义HTML标签
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));

        //敏感词过滤
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        //数据填充
        discussPost.setCommentCount(0);
        discussPost.setCreateTime(new Date());
        discussPost.setScore(0.0);
        discussPost.setStatus(0);
        discussPost.setType(0);

        discussPostMapper.insertDiscussPost(discussPost);
    }

    /**
     * 帖子详情：
     *      1.数据层，业务层，控制层。
     *      2。前端代码修改
     *          1.帖子标题链接，点进进入帖子详情
     *          2.帖子详情页信息展示
     */
    @Override
    public DiscussPost findDiscussPostById(Integer id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 修改帖子评论数量
     * @param id
     * @param commentCount
     */
    @Override
    public void updateCommentCount(int id, int commentCount) {
        discussPostMapper.updateCommentCount(id,commentCount);
    }


}
