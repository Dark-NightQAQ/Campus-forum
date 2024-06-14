package org.example.finishedbackend.entity.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountPrivacyVO {
    boolean phone;
    boolean email;
    boolean qq;
    boolean gender;
}
