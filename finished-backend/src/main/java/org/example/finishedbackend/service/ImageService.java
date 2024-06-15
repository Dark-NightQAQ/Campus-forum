package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.StoreImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ImageService extends IService<StoreImageDTO> {
    String uploadAvatar(MultipartFile file, int id) throws IOException;
    String uploadImage(MultipartFile file, int id) throws IOException;
    void fetchImageFromMinio(OutputStream stream, String image) throws Exception ;
}
