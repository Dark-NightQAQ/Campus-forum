package org.example.finishedbackend.service.Impl;

import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.TopicTypeDTO;
import org.example.finishedbackend.mapper.TopicTypeMapper;
import org.example.finishedbackend.service.TopicService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    TopicTypeMapper typeMapper;

    @Override
    public List<TopicTypeDTO> listTypes() {
        return typeMapper.selectList(null);
    }
}
