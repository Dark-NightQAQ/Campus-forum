package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("db_topic")
@AllArgsConstructor
@NoArgsConstructor
public class TopicDTO {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    Integer uid;
}
