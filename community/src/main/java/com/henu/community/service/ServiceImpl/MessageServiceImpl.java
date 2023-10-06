package com.henu.community.service.ServiceImpl;

import com.henu.community.mapper.MessageMapper;
import com.henu.community.pojo.Message;
import com.henu.community.service.MessageService;
import com.henu.community.util.HostHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Resource
    private MessageMapper messageMapper;

    @Override
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId,offset,limit);
    }

    @Override
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    @Override
    public int findUnreadLettersCount(int userId, String conversationId) {
        return messageMapper.selectUnreadLettersCount(userId,conversationId);
    }

    @Override
    public void saveLetter(Message message) {
        messageMapper.insertLetter(message);
    }

    @Override
    public void updateStatus(List<Integer> ids, int status) {
        messageMapper.updateStatus(ids,status);
    }
}
