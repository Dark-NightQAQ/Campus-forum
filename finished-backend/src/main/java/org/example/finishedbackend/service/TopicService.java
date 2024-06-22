package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.example.finishedbackend.entity.DTO.Interact;
import org.example.finishedbackend.entity.DTO.TopicDTO;
import org.example.finishedbackend.entity.DTO.TopicTypeDTO;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.request.TopicUpdateVO;
import org.example.finishedbackend.entity.VO.response.TopicDetailVO;
import org.example.finishedbackend.entity.VO.response.TopicPreviewVO;
import org.example.finishedbackend.entity.VO.response.TopicTopVO;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TopicService extends IService<TopicDTO> {
    List<TopicTypeDTO> listTypes();
    String createTopic(int uid, TopicCreateVO vo);
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    List<TopicTopVO> listTopTopics();
    TopicDetailVO getTopic(int tid, int uid);
    void interact(Interact interact, boolean state);
    List<TopicPreviewVO> listTopicCollects(int uid);
    String updateTopic(int uid, TopicUpdateVO vo);
}
