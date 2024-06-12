package org.example.finishedbackend.entity.VO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class ResetAccountPasswordVO {
    @Email
    @Length(min = 6)
    private String email;
    @Length(min = 6)
    private String password;
    @Length(min = 6, max = 6)
    private String code;
}
