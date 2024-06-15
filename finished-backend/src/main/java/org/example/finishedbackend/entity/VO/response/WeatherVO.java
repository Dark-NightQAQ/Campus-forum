package org.example.finishedbackend.entity.VO.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherVO {
    JSONObject location;
    JSONObject now;
    JSONArray hourly;
}
