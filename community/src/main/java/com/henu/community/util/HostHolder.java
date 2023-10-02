package com.henu.community.util;

import com.henu.community.pojo.User;
import org.springframework.stereotype.Component;

/**
 * 用户信息托管
 */
@Component
public class HostHolder {

    private ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public void set(User user){
        threadLocal.set(user);
    }

    public User get(){
        return threadLocal.get();
    }

    public void clear(){
        threadLocal.remove();
    }

}
