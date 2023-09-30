package com.henu.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Component
public class MailClient {
    //需要一个logger记录日志
    //需要javaMailSender发送邮件
    //需要配置中的username
    //需要一个方法可以被调用以发送邮件，参数收件人，主题，内容

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Resource
    private JavaMailSender javaMailSender;

    @Value(value = "${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            helper.setSentDate(new Date());
            javaMailSender.send(helper.getMimeMessage());
            /*SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setSentDate(new Date());
            javaMailSender.send(message);*/
        }catch (Exception e){
            logger.error("发送邮件失败，" + e.getMessage());
        }
    }













}
