package org.example.finishedbackend.entity.VO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Data
public class RegisterAccountByEmailVO {
    @Email
    @Length(min = 6)
    private String email;
    @Length(min = 1, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    private String username;
    @Length(min = 6)
    private String password;
    @Length(min = 6, max = 6)
    private String code;
}
