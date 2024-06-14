package org.example.finishedbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.VO.request.ModifyEmailVO;
import org.example.finishedbackend.entity.VO.request.RegisterAccountByEmailVO;
import org.example.finishedbackend.entity.VO.request.ResetAccountPasswordVO;
import org.example.finishedbackend.entity.VO.request.changePasswordVO;
import org.example.finishedbackend.mapper.AccountMapper;
import org.example.finishedbackend.service.AccountService;
import org.example.finishedbackend.utils.Const;
import org.example.finishedbackend.utils.FlowUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, AccountDTO> implements AccountService, UserDetailsService {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils flowUtils;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountDTO dto = findAccountByUsernameOrEmail(username);
        if (dto == null) throw new UsernameNotFoundException("用户或密码错误");
        return User
                .withUsername(dto.getUsername())
                .password(dto.getPassword())
                .roles(dto.getRole())
                .build();
    }

    @Override
    public AccountDTO findAccountByUsernameOrEmail(String username) {
        return this
                .query()
                .eq("username", username)
                .or()
                .eq("email", username)
                .one();
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_CODE + vo.getEmail());
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码有误, 请重试";
        AccountDTO dto = this.findAccountByUsernameOrEmail(vo.getEmail());
        if (dto != null && id != dto.getId()) {
         return "该邮件已被其他账号绑定!";
        }
        this.update().eq("id", id).set("email", vo.getEmail()).update();
        stringRedisTemplate.delete(Const.VERIFY_EMAIL_CODE + vo.getEmail());
        return null;
    }

    @Override
    public String changePassword(int id, changePasswordVO vo) {
        String password = this.query().eq("id", id).one().getPassword();
        if (!encoder.matches(vo.getPassword(), password)) return "原密码输入错误, 请重新输入";
        boolean success = this.update().eq("id", id).set("password", encoder.encode(vo.getNew_password())).update();
        return success ? null : "未知错误";
    }

    @Override
    public String emailVerifyCode(String email, String type, String ip) {
        synchronized (ip.intern()) {
            if (verifyLimit(ip)) {
                Random random = new Random();
                int code = random.nextInt(899999) + 100000;
                Map<String, Object> map = Map.of("email", email, "type", type, "code", code);
                rabbitTemplate.convertAndSend("email", map);
                stringRedisTemplate.opsForValue().set(Const.VERIFY_EMAIL_CODE+email, String.valueOf(code), 5, TimeUnit.MINUTES);
                return null;
            } else {
                return "请求繁忙, 请稍后再试";
            }
        }
    }

    @Override
    public String resetAccountPassWord(ResetAccountPasswordVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_CODE + email);
        if (!existsAccountByEmail(email)) return "该邮箱尚未注册账号!";
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码有误, 请重试";
        AccountDTO dto = this.query().eq("email", email).one();
        if (encoder.matches(vo.getPassword(), dto.getPassword())) return "新旧密码不能一致";
        String newPassword = encoder.encode(vo.getPassword());
        if (this.update().eq("email", email).set("password", newPassword).update()) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_CODE + email);
            return null;
        } else {
            return "内部错误, 请截图联系管理员!";
        }
    }

    @Override
    public String registerAccountByEmail(RegisterAccountByEmailVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_CODE + email);
        if (code == null) return "请先获取验证码";
        if (!vo.getCode().equals(code)) return "验证码有误, 请重试";
        if (existsAccountByEmail(email)) return "该邮箱已被注册";
        String username = vo.getUsername();
        if (existsAccountByUsername(username)) return "该用户名已被使用";
        AccountDTO dto = new AccountDTO(0, vo.getUsername(), encoder.encode(vo.getPassword()), null, email, "user", new Date());
        if (this.baseMapper.insert(dto) > 0) {
            return null;
        } else {
            return "错误代码: r001, 请截图联系管理员或稍后重试";
        }
    }

    @Override
    public AccountDTO findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.check(key, 60);
    }

    private boolean existsAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<AccountDTO>query().eq("email", email));
    }

    private boolean existsAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<AccountDTO>query().eq("username", username));
    }
}
