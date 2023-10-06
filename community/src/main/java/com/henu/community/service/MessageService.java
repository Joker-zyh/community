package com.henu.community.service;

import com.henu.community.pojo.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    // 查询当前用户的会话（每个会话显示最新的消息）
    List<Message> findConversations(int userId,int offset,int limit);

    // 查询当前用户的会话个数（用于分页）
    int findConversationCount(int userId);

    // 查询当前用户与对方用户的会话中的所有消息
    List<Message> findLetters(String conversationId,int offset,int limit);

    //查询当前用户与对方用户的会话中的所有消息的个数（用于分页）
    int findLetterCount(String conversationId);

    // 查询当前用户（在当前会话）中的未读消息个数
    int findUnreadLettersCount(int userId,String conversationId);

    //新增消息
    void saveLetter(Message message);

    //修改消息状态
    void updateStatus(List<Integer> ids,int status);
}
