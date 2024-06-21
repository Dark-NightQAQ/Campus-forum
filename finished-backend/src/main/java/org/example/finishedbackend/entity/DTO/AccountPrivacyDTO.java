package org.example.finishedbackend.entity.DTO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

@Data
@TableName("db_account_privacy")
public class AccountPrivacyDTO {
    @TableId(type = IdType.AUTO)
    final Integer id;
    boolean phone = true;
    boolean email = true;
    boolean qq = true;
    boolean gender = true;

    public String[] hiddenFields() {
        List<String> strings = new LinkedList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this)) {
                    strings.add(field.getName());
                }
             } catch (Exception ignored) {

            }
        }
        return strings.toArray(String[]::new);
    }
}
