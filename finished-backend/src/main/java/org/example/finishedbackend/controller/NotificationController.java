package org.example.finishedbackend.controller;

import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.annotations.Delete;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.entity.VO.response.NotificationVO;
import org.example.finishedbackend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService service;

    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute("id") int id) {
        return RestBean.success(service.findUserNotifications(id), null);
    }

    @GetMapping("/delete")
    public RestBean<Integer> deleteNotification(@RequestAttribute("id") int uid,
                                                @RequestParam @Min(0) int id) {
        service.deleteUserNotifications(id, uid);
        return RestBean.success(null);
    }

    @GetMapping("/delete-all")
    public RestBean<Integer> deleteAllNotification(@RequestAttribute("id") int uid) {
        service.deleteUserAllNotifications(uid);
        return RestBean.success(null);
    }
}
