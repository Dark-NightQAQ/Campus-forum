package org.example.finishedbackend.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate template;

    public <T> void saveListToCache(String key, List<T> data, long expire) {
        template.opsForValue().set(key, JSONArray.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }

    public <T> void saveListToCache(String key, T data, long expire) {
        template.opsForValue().set(key, JSONArray.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }

    public <T> List<T> getListFromCache(String key, Class<T> itemType) {
        String s = template.opsForValue().get(key);
        if (s == null) return null;
        return JSONArray.parseArray(s).toList(itemType);
    }

    public <T> T getFromCache(String key, Class<T> itemType) {
        String s = template.opsForValue().get(key);
        if (s == null) return null;
        return JSONObject.parseObject(s).to(itemType);
    }

    public void deleteCache(String key) {
        template.delete(key);
    }

    public void deleteCachePattern(String key) {
        Set<String> keys = Optional.ofNullable(template.keys(key)).orElse(Collections.emptySet());
        template.delete(keys);
    }
}
