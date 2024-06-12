package org.example.finishedbackend.entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;

public record RestBean<T>(int code, T data, String message) {
    public static <T> RestBean<T> success(T data, String message) {
        return new RestBean<>(200, data, message);
    }

    public static <T> RestBean<T> success(String message) {
        return new RestBean<>(200, null, message);
    }

    public static <T> RestBean<T> failure(int code, String message) {
        return new RestBean<>(code, null, message);
    }

    public static <T> RestBean<T> failure(int code) {
        return new RestBean<>(code, null, "请求失败");
    }

    public String asJSONString() {
        return JSON.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
