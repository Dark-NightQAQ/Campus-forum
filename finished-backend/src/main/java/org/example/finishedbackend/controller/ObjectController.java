package org.example.finishedbackend.controller;

import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.finishedbackend.entity.RestBean;
import org.example.finishedbackend.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class ObjectController {

    @Resource
    ImageService imageService;

    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        response.setContentType("image/jpeg");
        this.fetchImage(request, response);

    }

    private void fetchImage(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream outputStream = response.getOutputStream();
        if (imagePath.length() < 13) {
            response.setStatus(404);
            outputStream.println(RestBean.failure(404, "404 Not Found").asJSONString());
        } else {
            try {
                imageService.fetchImageFromMinio(outputStream, imagePath);
                response.setHeader("cache-control", "max-age=2592000");
                response.setHeader("Content-Type", "image/jpeg");
            } catch (ErrorResponseException e) {
                if (e.response().code() == 404) {
                    response.setStatus(404);
                    outputStream.println(RestBean.failure(404, "404 Not Found").asJSONString());
                } else {
                    log.error("从Minio获取图片异常：{}", e.getMessage());
                }
            }
        }

    }
}
