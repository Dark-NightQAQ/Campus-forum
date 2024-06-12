package org.example.finishedbackend.entity.VO.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Valid
public class DetailsSaveVO {
    @Length(min = 1, max = 30)
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    private String username;
    @Min(0)
    @Max(1)
    int gender;
    @Pattern(regexp = "\\d{3}-\\d{8}|\\d{4}-\\d{7}")
    @Length(max = 11)
    String phone;
    @Length(max = 13)
    String qq;
    @Length(max = 200)
    String desc;
}
