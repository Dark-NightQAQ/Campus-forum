package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("db_topic_type")
public class TopicTypeDTO {
    Integer id;
    String name;
    @TableField("`desc`")
    String desc;
}
