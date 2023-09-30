package com.henu.community.controller;

import com.henu.community.pojo.User;
import com.henu.community.service.UserService;
import com.henu.community.util.ActivationStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class LoginController {

    @Resource
    private UserService userService;

    /**
     * 跳转到注册页面
     * @return
     */
    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    /**
     * 跳转到登陆页面
     * @return
     */
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    /**
     * 注册功能
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, String> map = userService.register(user);
        //map为空证明前边操作没有异常，数据正确，可注册
        if (map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，请在我们发送的邮件里点击链接，激活账号。");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else {
            //有错误，弹出错误信息
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 激活账号
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        //无论是否成功均来到中间页面提示后再跳转
        //成功跳转到登陆界面
        //重复和失败最终均跳转到首页
        int activation = userService.activation(userId, code);
        if (activation == ActivationStatus.ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，请前往登陆页面登陆。");
            model.addAttribute("target","/login");
        }else if (activation == ActivationStatus.ACTIVATION_REPEAT){
            model.addAttribute("msg","无效操作，重复激活。");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","激活失败，激活码错误");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
