package org.example.finishedbackend.entity.VO.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicTopVO {
    int id;
    String title;
    Date time;
}
