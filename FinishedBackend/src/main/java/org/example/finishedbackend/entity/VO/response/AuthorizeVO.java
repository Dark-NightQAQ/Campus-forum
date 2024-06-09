package org.example.finishedbackend.entity.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AuthorizeVO {
    private String username;
    private String role;
    private String token;
    private Date expireTime;
}
