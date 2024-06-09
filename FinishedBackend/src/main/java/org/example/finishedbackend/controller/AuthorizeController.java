package org.example.finishedbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.request.RegisterAccountByEmailVO;
import org.example.finishedbackend.entity.VO.request.ResetAccountPasswordVO;
import org.example.finishedbackend.service.AccountService;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;

@RestController
@Validated
@RequestMapping("/api/auth")
@Slf4j
public class AuthorizeController {

    @Resource
    private AccountService accountService;

    @GetMapping("/ask-code")
    public RestBean<String> askCode(@RequestParam @Email String email,
                                    @RequestParam @Pattern(regexp = "register|reset") String type,
                                    HttpServletRequest request) {
        String string = accountService.emailVerifyCode(email, type, request.getRemoteAddr());
        log.info("向{}发送了验证码", email);
        return string == null ? RestBean.success("验证码发送成功, 请及时查看邮箱") : RestBean.failure(400, string);
    }

    @PostMapping("/reset-password")
    public RestBean<String> resetPassword(@Valid ResetAccountPasswordVO vo) {
        String s = accountService.resetAccountPassWord(vo);
        return s == null ? RestBean.success("密码重置成功！") : RestBean.failure(400, s);
    }

    @PostMapping("/register")
    public RestBean<String> registerAccount(@Valid RegisterAccountByEmailVO vo) {
        String s = accountService.registerAccountByEmail(vo);
        return s == null ? RestBean.success("注册成功！") : RestBean.failure(400, s);
    }
}
