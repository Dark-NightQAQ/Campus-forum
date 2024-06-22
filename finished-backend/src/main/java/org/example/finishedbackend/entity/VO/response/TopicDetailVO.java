package org.example.finishedbackend.entity.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetailVO {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    User user;

    @Data
    public static class User {
        Integer id;
        String username;
        String avatar;
        String desc;
        Integer gender;
        String qq;
        String phone;
        String email;
    }
}
