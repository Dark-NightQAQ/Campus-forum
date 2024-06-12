package org.example.finishedbackend.entity.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDetailsVO {
    int gender;
    String phone;
    String qq;
    String desc;
    String avatar;
}
