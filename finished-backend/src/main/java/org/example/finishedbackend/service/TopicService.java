package org.example.finishedbackend.service;

import org.example.finishedbackend.entity.DTO.TopicTypeDTO;
import org.example.finishedbackend.entity.VO.response.TopicTypeVO;

import java.util.List;

public interface TopicService {
    List<TopicTypeDTO> listTypes();
}
