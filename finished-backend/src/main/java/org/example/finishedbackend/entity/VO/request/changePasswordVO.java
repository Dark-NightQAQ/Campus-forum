package org.example.finishedbackend.entity.VO.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class changePasswordVO {
    @Length(min = 6)
    String password;
    @Length(min = 6)
    String new_password;
}
