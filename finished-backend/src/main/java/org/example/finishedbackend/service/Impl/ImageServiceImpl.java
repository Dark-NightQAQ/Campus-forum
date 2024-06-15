package org.example.finishedbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.example.finishedbackend.entity.DTO.AccountDTO;
import org.example.finishedbackend.entity.DTO.StoreImageDTO;
import org.example.finishedbackend.mapper.AccountMapper;
import org.example.finishedbackend.mapper.ImageStoreMapper;
import org.example.finishedbackend.service.ImageService;
import org.example.finishedbackend.utils.Const;
import org.example.finishedbackend.utils.FlowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Wrapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImageDTO> implements ImageService {

    @Resource
    MinioClient client;

    @Resource
    AccountMapper mapper;

    @Resource
    FlowUtils flowUtils;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {

        String imageName = UUID.randomUUID().toString().replace("_", "");
        imageName = "/avatar/"+imageName+".jpg";
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            client.putObject(args);
            String avatar = mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            if (mapper.update(null, Wrappers.<AccountDTO>update().set("avatar", imageName).eq("id", id)) > 0) {
                return imageName;
            }
            return null;
        } catch (Exception e) {
            log.error("图片上传发送错误：{}", e.getMessage());
            return null;
        }
    }

    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 3600))
            return null;
        String imageName = UUID.randomUUID().toString().replace("_", "");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("forum")
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(imageName)
                .build();
        try {
            client.putObject(args);
            if (this.save(new StoreImageDTO(id, imageName, date))) {
                return imageName;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("图片上传发送错误：{}", e.getMessage());
            return null;
        }
    }

    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("forum")
                .object(image)
                .build();
        GetObjectResponse response = client.getObject(args);
        IOUtils.copy(response, stream);
    }

    private void deleteOldAvatar(String avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket("forum")
                .object(avatar)
                .build();
        client.removeObject(args);
    }
}
