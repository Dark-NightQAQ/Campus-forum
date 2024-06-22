package org.example.finishedbackend.entity.VO.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicUpdateVO {
    @Min(0)
    Integer id;
    @Min(1)
    @Max(5)
    int type;
    @Length(min = 1, max = 38)
    String title;
    JSONObject content;
}
