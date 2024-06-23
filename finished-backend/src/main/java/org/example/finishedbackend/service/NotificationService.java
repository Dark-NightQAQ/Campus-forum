package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.NotificationDTO;
import org.example.finishedbackend.entity.VO.response.NotificationVO;

import java.util.List;

public interface NotificationService extends IService<NotificationDTO> {
    List<NotificationVO> findUserNotifications(int uid);
    void deleteUserNotifications(int id, int uid);
    void deleteUserAllNotifications(int uid);
    void addNotification(int uid, String title, String content, String type, String url);
}
