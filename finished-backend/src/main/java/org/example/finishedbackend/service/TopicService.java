package org.example.finishedbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.finishedbackend.entity.DTO.TopicDTO;
import org.example.finishedbackend.entity.DTO.TopicTypeDTO;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.response.TopicDetailVO;
import org.example.finishedbackend.entity.VO.response.TopicPreviewVO;
import org.example.finishedbackend.entity.VO.response.TopicTopVO;

import java.util.List;

public interface TopicService extends IService<TopicDTO> {
    List<TopicTypeDTO> listTypes();
    String createTopic(int uid, TopicCreateVO vo);
    List<TopicPreviewVO> listTopicByPage(int page, int type);
    List<TopicTopVO> listTopTopics();
    TopicDetailVO getTopic(int tid);
}
