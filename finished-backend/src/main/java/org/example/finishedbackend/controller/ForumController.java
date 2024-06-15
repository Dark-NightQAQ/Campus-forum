package org.example.finishedbackend.controller;

import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.response.TopicTypeVO;
import org.example.finishedbackend.entity.VO.response.WeatherVO;
import org.example.finishedbackend.service.TopicService;
import org.example.finishedbackend.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService service;

    @Resource
    TopicService topic;

    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double longitude, double latitude) {
        WeatherVO vo = service.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理位置与天气失败, 请稍后再试!") : RestBean.success(vo, "接口数据获取成功");
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes() {
        return RestBean.success(topic.listTypes().stream().map(type -> new TopicTypeVO(type.getId(), type.getName(), type.getDesc())).toList(), "获取typeList成功");
    }
}
