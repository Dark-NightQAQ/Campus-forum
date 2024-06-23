package org.example.finishedbackend.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.finishedbackend.entity.DTO.NotificationDTO;
import org.example.finishedbackend.entity.VO.response.NotificationVO;
import org.example.finishedbackend.mapper.NotificationMapper;
import org.example.finishedbackend.service.NotificationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, NotificationDTO> implements NotificationService {

    @Override
    public List<NotificationVO> findUserNotifications(int uid) {
        return this.query().eq("uid", uid).list()
                .stream()
                .map(item -> {
                    NotificationVO notificationVO = new NotificationVO();
                    BeanUtils.copyProperties(item, notificationVO);
                    return notificationVO;
                }).toList();
    }

    @Override
    public void deleteUserNotifications(int id, int uid) {
        this.remove(Wrappers.<NotificationDTO>query().eq("uid", uid).eq("id", id));
    }

    @Override
    public void deleteUserAllNotifications(int uid) {
        this.remove(Wrappers.<NotificationDTO>query().eq("uid", uid));
    }

    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        NotificationDTO dto = new NotificationDTO();
        dto.setUid(uid);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setType(type);
        dto.setUrl(url);
        this.save(dto);
    }
}
