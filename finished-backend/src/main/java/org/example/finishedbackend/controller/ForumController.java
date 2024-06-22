package org.example.finishedbackend.controller;

import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.example.finishedbackend.entity.DTO.Interact;
import org.example.finishedbackend.entity.DTO.TopicDTO;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.request.TopicUpdateVO;
import org.example.finishedbackend.entity.VO.response.*;
import org.example.finishedbackend.service.TopicService;
import org.example.finishedbackend.service.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
        return RestBean.success(topic.listTypes().stream().map(type -> new TopicTypeVO(type.getId(), type.getName(), type.getDesc(), type.getColor())).toList(), "获取typeList成功");
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@RequestAttribute("id") int id,
                                      @RequestBody @Valid TopicCreateVO vo) {
        String s = topic.createTopic(id, vo);
        return s == null ? RestBean.success("创建帖子成功") : RestBean.failure(400, s);
    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type) {
        List<TopicPreviewVO> voList = topic.listTopicByPage(page, type);
        return voList != null ? RestBean.success(voList, null) : RestBean.failure(400, "已经到头了~");
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVO>> topTopic() {
        return RestBean.success(topic.listTopTopics(), null);
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVO> topic(@RequestParam @Min(0) int tid,
                                         @RequestAttribute("id") int id) {
        return RestBean.success(topic.getTopic(tid, id), null);
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute("id") int id) {
        topic.interact(new Interact(tid, id, new Date(), type), state);
        return RestBean.success(null);
    }

    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVO>> collect(@RequestAttribute("id") int id) {
        return RestBean.success(topic.listTopicCollects(id),null);
    }

    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@RequestAttribute("id") int id,
                                      @RequestBody @Valid TopicUpdateVO vo) {
        String s = topic.updateTopic(id, vo);
        return s == null ? RestBean.success("帖子更新成功") : RestBean.failure(400, s);
    }
}
