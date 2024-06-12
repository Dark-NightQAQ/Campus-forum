package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.AccountDetailsDTO;
import org.example.finishedbackend.entity.VO.request.DetailsSaveVO;

public interface AccountDetailsService extends IService<AccountDetailsDTO> {
    AccountDetailsDTO findAccountDetailsById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVO vo);
}
