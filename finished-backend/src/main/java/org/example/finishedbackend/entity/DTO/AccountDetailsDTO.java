package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@TableName("db_account_details")
@NoArgsConstructor
public class AccountDetailsDTO {
    @TableId(type = IdType.AUTO)
    Integer id;
    int gender;
    String phone;
    String qq;
    @TableField("`desc`")
    String desc;
    String avatar;
}
