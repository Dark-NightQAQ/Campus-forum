package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.VO.request.ModifyEmailVO;
import org.example.finishedbackend.entity.VO.request.RegisterAccountByEmailVO;
import org.example.finishedbackend.entity.VO.request.ResetAccountPasswordVO;
import org.example.finishedbackend.entity.VO.request.changePasswordVO;

public interface AccountService extends IService<AccountDTO> {
    String emailVerifyCode(String email, String type, String ip);
    String resetAccountPassWord(ResetAccountPasswordVO vo);
    String registerAccountByEmail(RegisterAccountByEmailVO vo);
    AccountDTO findAccountById(int id);
    AccountDTO findAccountByUsernameOrEmail(String username);
    String modifyEmail(int id, ModifyEmailVO vo);
    String changePassword(int id, changePasswordVO vo);
}
