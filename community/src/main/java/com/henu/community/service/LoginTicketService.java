package com.henu.community.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface LoginTicketService {
    Map<String,String> login(String username,String password,boolean rememberMe);

    void logout(String ticket);
}
