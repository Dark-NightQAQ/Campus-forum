package org.example.finishedbackend.controller;

import org.example.finishedbackend.entity.RestBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/test")
    public String test() {
        return RestBean.success("成功").asJSONString();
    }
}
