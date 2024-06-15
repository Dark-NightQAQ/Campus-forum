package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic")
public class TopicDTO {
    Integer id;
    String title;
    String content;
    Integer uid;
    Integer type;
    Date time;
}
