package org.example.finishedbackend.controller;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.DTO.AccountDetailsDTO;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.request.DetailsSaveVO;
import org.example.finishedbackend.entity.VO.request.ModifyEmailVO;
import org.example.finishedbackend.entity.VO.request.changePasswordVO;
import org.example.finishedbackend.entity.VO.response.AccountDetailsVO;
import org.example.finishedbackend.entity.VO.response.AccountVO;
import org.example.finishedbackend.service.AccountDetailsService;
import org.example.finishedbackend.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Resource
    AccountService accountService;

    @Resource
    AccountDetailsService accountDetailsService;

    @GetMapping("/info")
    public RestBean<AccountVO> info(@RequestAttribute("id") int id) {
        AccountDTO dto = accountService.findAccountById(id);
        AccountVO vo = new AccountVO(dto.getUsername(), dto.getEmail(), dto.getRole(), dto.getCreate_time());
        return RestBean.success(vo, "account info get success");
    }

    @GetMapping("/details")
    public RestBean<AccountDetailsVO> details(@RequestAttribute("id") int id) {
        AccountDetailsDTO dto = Optional.ofNullable(accountDetailsService.findAccountDetailsById(id)).orElseGet(AccountDetailsDTO::new);
        AccountDetailsVO vo = new AccountDetailsVO(dto.getGender(), dto.getPhone(), dto.getQq(), dto.getDesc(), dto.getAvatar());
        return RestBean.success(vo, "account details get success");
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute("id") int id,
                                      @RequestBody DetailsSaveVO vo) {
        return accountDetailsService.saveAccountDetails(id, vo) ? RestBean.success("修改成功") : RestBean.failure(401, "此用户名已被其他用户使用!");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute("id") int id,
                                      @RequestBody @Valid ModifyEmailVO vo) {
        String result = accountService.modifyEmail(id, vo);
        return result == null ? RestBean.success("修改成功") : RestBean.failure(400, result);
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestAttribute("id") int id,
                                         @RequestBody @Valid changePasswordVO vo) {
        String s = accountService.changePassword(id, vo);
        return s == null ? RestBean.success("修改密码成功") : RestBean.failure(401, s);
    }
}
