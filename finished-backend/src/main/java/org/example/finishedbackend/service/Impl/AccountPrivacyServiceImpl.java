package org.example.finishedbackend.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.finishedbackend.entity.DTO.AccountPrivacyDTO;
import org.example.finishedbackend.entity.VO.request.PrivacySaveVO;
import org.example.finishedbackend.mapper.AccountPrivacyMapper;
import org.example.finishedbackend.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacyDTO> implements AccountPrivacyService {

    @Override
    @Transactional
    public void savePrivacy(int id, PrivacySaveVO vo) {
        AccountPrivacyDTO dto = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacyDTO(id));
        boolean status = vo.isStatus();
        switch (vo.getType()) {
            case "phone" -> dto.setPhone(status);
            case "email" -> dto.setEmail(status);
            case "qq" -> dto.setQq(status);
            case "gender" -> dto.setGender(status);
        }
        this.saveOrUpdate(dto);
    }

    @Override
    public AccountPrivacyDTO accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacyDTO(id));
    }


}
