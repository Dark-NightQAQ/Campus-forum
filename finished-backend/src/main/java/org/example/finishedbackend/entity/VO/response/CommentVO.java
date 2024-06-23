package org.example.finishedbackend.entity.VO.response;

import lombok.Data;

import java.util.Date;

@Data
public class CommentVO {
    Integer id;
    String content;
    Date time;
    String quote;
    User user;

    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        Integer gender;
        String qq;
        String phone;
        String email;
    }
}
