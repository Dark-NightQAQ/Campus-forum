package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.AccountPrivacyDTO;
import org.example.finishedbackend.entity.VO.request.PrivacySaveVO;

public interface AccountPrivacyService extends IService<AccountPrivacyDTO> {
    void savePrivacy(int id, PrivacySaveVO vo);
    AccountPrivacyDTO accountPrivacy(int id);
}
