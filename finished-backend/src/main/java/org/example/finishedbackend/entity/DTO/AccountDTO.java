package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_account")
@AllArgsConstructor
public class AccountDTO {
    @TableId(type = IdType.AUTO)
    Integer id;
    String username;
    String password;
    String avatar;
    String email;
    String role;
    @TableField("register_time")
    Date create_time;
}
