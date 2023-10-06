package com.henu.community.service.ServiceImpl;

import com.henu.community.mapper.CommentMapper;
import com.henu.community.pojo.Comment;
import com.henu.community.service.CommentService;
import com.henu.community.service.DiscussPostService;
import com.henu.community.util.SensitiveFilter;
import com.henu.community.util.constant.EntityType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private DiscussPostService discussPostService;

    /**
     * 分页查询
     * @param entityType
     * @param entityId
     * @param offset
     * @param limit
     * @return
     */
    @Override
    public List<Comment> findPage(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectPage(entityType,entityId,offset,limit);
    }

    /**
     * 查询评论总数
     * @param entityType
     * @param entityId
     * @return
     */
    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCommentCount(entityType,entityId);
    }

    /**
     * 添加帖子
     * @param comment
     */
    @Override
    @Transactional
    public void saveComment(Comment comment) {
        if (comment == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        commentMapper.insertComment(comment);
        //更新帖子评论总数
        if (comment.getEntityType() == EntityType.ENTITY_TYPE_POST){
            int count = commentMapper.selectCommentCount(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

    }
}
