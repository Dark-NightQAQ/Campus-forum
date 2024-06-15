package org.example.finishedbackend.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/image")
public class ImageController {

    @Resource
    ImageService imageService;

    @PostMapping("/avatar")
    public RestBean<String> avatar(@RequestParam("file") MultipartFile file,
                                   @RequestAttribute("id") int id) throws IOException {
        // 如果文件大小大于1MB 就不允许上传
        if (file.getSize() > 1024 * 1024) {
            return RestBean.failure(400, "图片上传大小不允许大于1MB");
        }
        log.info("正在进行头像上传操作...");
        String s = imageService.uploadAvatar(file, id);
        return s != null ? RestBean.success(s, "头像上传成功！") : RestBean.failure(400, "头像上传失败");
    }

    @PostMapping("/cache")
    public RestBean<String> uploadImage(@RequestParam("file") MultipartFile file,
                                        @RequestAttribute("id") int id,
                                        HttpServletResponse response) throws IOException {
        if (file.getSize() > 1024 * 1024 * 5)
            return RestBean.failure(400, "图片上传大小不允许大于5MB");
        log.info("正在进行帖子图片上传操作...");
        String s = imageService.uploadImage(file, id);
        if (s != null) {
            return RestBean.success(s, "图片上传成功！");
        } else {
            response.setStatus(400);
            return RestBean.failure(400, "图片上传失败");
        }
    }
}
