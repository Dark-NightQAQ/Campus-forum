package org.example.finishedbackend.entity.VO.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PrivacySaveVO {
    @Pattern(regexp = "(phone|email|qq|gender)")
    private String type;
    boolean status;
}
