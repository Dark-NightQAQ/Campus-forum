package org.example.finishedbackend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.TopicDTO;
import org.example.finishedbackend.entity.DTO.TopicTypeDTO;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.response.TopicPreviewVO;
import org.example.finishedbackend.entity.VO.response.TopicTopVO;
import org.example.finishedbackend.mapper.TopicMapper;
import org.example.finishedbackend.mapper.TopicTypeMapper;
import org.example.finishedbackend.service.TopicService;
import org.example.finishedbackend.utils.CacheUtils;
import org.example.finishedbackend.utils.Const;
import org.example.finishedbackend.utils.FlowUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, TopicDTO> implements TopicService {

    @Resource
    TopicTypeMapper typeMapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    // 预处理listTypes的Id;
    private Set<Integer> types = null;

    @PostConstruct
    private void init() {
        types = this.listTypes().stream().map(TopicTypeDTO::getId).collect(Collectors.toSet());
    }

    @Override
    public List<TopicTypeDTO> listTypes() {
        return typeMapper.selectList(null);
    }

    @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        if (vo.getContent() == null) return "文章内容不能为空";
        if (!textLimitCheck(vo.getContent())) return "文章字数过多, 请稍后再试";
        if (!types.contains(vo.getType())) return "文章类型非法！";
        if (!flowUtils.limitPeriodCounterCheck(Const.FORUM_TOPIC_CREATE_COUNTER + uid, 3, 3600)) return "发文频繁, 请稍后再试";
        TopicDTO dto = new TopicDTO(0, vo.getTitle(), vo.getContent().toJSONString(), vo.getType(), new Date(), uid, null, null);
        if (this.save(dto)) {
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误, 请联系管理员";
        }
    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int page, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + page + ":" + type;
        List<TopicPreviewVO> voList = cacheUtils.getListFromCache(key, TopicPreviewVO.class);
        if (voList != null) return voList;
        List<TopicDTO> list = null;
        if (type == 0) {
            list = baseMapper.topicList(page * 5);
        } else {
            list = baseMapper.topicListByType(page * 5, type);
        }
        if (list.isEmpty()) return null;
        voList = list.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, voList, 60);
        return voList;
    }

    @Override
    public List<TopicTopVO> listTopTopics() {
        List<TopicDTO> topics = baseMapper.selectList(Wrappers.<TopicDTO>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> new TopicTopVO(topic.getId(), topic.getTitle(), topic.getTime())).toList();
    }

    private TopicPreviewVO resolveToPreview(TopicDTO dto) {
        TopicPreviewVO vo = new TopicPreviewVO(dto.getId(), dto.getType(), dto.getTitle(), null, null, dto.getTime(), dto.getUid(), dto.getUsername(), dto.getAvatar());
        List<String> images = new LinkedList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(dto.getContent()).getJSONArray("ops");
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?,?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    private boolean textLimitCheck(JSONObject object) {
        long length = 0;
        for (Object ops : object.getJSONArray("ops")) {
            length += JSONObject.from(ops).getString("insert").length();
            if (length > 20000) return false;
        }
        return true;
    }

}
