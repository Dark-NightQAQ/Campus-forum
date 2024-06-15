package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("db_image_store")
@AllArgsConstructor
@NoArgsConstructor
public class StoreImageDTO {

    Integer uid;
    String name;
    Date time;
}
