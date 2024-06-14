package org.example.finishedbackend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.DTO.AccountDetailsDTO;
import org.example.finishedbackend.entity.VO.request.DetailsSaveVO;
import org.example.finishedbackend.mapper.AccountDetailsMapper;
import org.example.finishedbackend.service.AccountDetailsService;
import org.example.finishedbackend.service.AccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetailsDTO> implements AccountDetailsService {

    @Resource
    AccountService accountService;

    @Override
    public AccountDetailsDTO findAccountDetailsById(int id) {
        return this.query().eq("id", id).one();
    }

    @Override
    @Transactional
    public synchronized boolean saveAccountDetails(int id, DetailsSaveVO vo) {
        AccountDTO accountDTO = accountService.findAccountByUsernameOrEmail(vo.getUsername());
        if (accountDTO == null || accountDTO.getId() == id) {
            accountService.update().eq("id", id).set("username", vo.getUsername()).update();
            this.saveOrUpdate(new AccountDetailsDTO(id, vo.getGender(), vo.getPhone(), vo.getQq(), vo.getDesc()));
            return true;
        }
        return false;
    }
}
