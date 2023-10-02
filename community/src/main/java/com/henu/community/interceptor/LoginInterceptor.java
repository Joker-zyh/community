package com.henu.community.interceptor;

import com.henu.community.pojo.LoginTicket;
import com.henu.community.pojo.User;
import com.henu.community.service.LoginTicketService;
import com.henu.community.service.UserService;
import com.henu.community.util.GetCookie;
import com.henu.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 登陆拦截器功能：
 *      1.在请求开始前查询用户登陆状态。ticket是否有效，是否超时
 *      2.用户登陆后，将用户信息存入ThreadLocal中。
 *      3.在模版引擎执行前，将用户信息存入Model中，修改前端代码，让导航栏根据登陆状态显示。
 *      4.在请求结束前清除用户资源。
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private LoginTicketService loginTicketService;

    @Resource
    private UserService userService;

    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取令牌
        String ticket = GetCookie.getCookie(request, "ticket");
        //根据令牌获取登陆信息
        if (ticket != null){
            LoginTicket loginTicket = loginTicketService.getLoginTicket(ticket);
            //检验登陆信息是否无效，是否过期
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
                //信息正常，用户已登陆，将用户信息取出存到ThreadLocal中
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.set(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //3.在模版引擎执行前，将用户信息存入Model中，修改前端代码，让导航栏根据登陆状态显示。
        User user = hostHolder.get();
        if (user != null){
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //4.在请求结束前清除用户资源。
        hostHolder.clear();
    }
}
