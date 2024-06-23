package org.example.finishedbackend.entity.VO.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationVO {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    Date time;
}
