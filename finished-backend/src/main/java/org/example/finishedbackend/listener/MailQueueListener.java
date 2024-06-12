package org.example.finishedbackend.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "email")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage simpleMailMessage = switch (type) {
            case "register" -> createMessage("账号注册", "你的账号注册验证码是:"+code.toString()+", 有效时间为5分钟, 请及时使用, 请勿向他人泄露验证码。", email);
            case "reset" -> createMessage("密码重置", "您正在进行账号密码重置操作, 重置密码验证码是:"+code.toString()+", 验证码有效时间为5分钟, 如非本人操作请忽视。", email);
            case "modify" -> createMessage("修改绑定邮件", "你正在进行绑定邮件修改操作, 改绑验证码是:"+code.toString()+", 验证码有效时间为5分钟, 如非本人操作请忽视。", email);
            default -> null;
        };
        if (simpleMailMessage == null) return;
        sender.send(simpleMailMessage);
    }

    private SimpleMailMessage createMessage(String title, String content, String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(title);
        simpleMailMessage.setText(content);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom(username);
        return simpleMailMessage;
    }
}
