package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.VO.request.RegisterAccountByEmailVO;
import org.example.finishedbackend.entity.VO.request.ResetAccountPasswordVO;

public interface AccountService extends IService<AccountDTO> {
    String emailVerifyCode(String email, String type, String ip);
    String resetAccountPassWord(ResetAccountPasswordVO vo);
    String registerAccountByEmail(RegisterAccountByEmailVO vo);
}
