package org.example.finishedbackend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.*;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.response.TopicDetailVO;
import org.example.finishedbackend.entity.VO.response.TopicPreviewVO;
import org.example.finishedbackend.entity.VO.response.TopicTopVO;
import org.example.finishedbackend.mapper.*;
import org.example.finishedbackend.service.TopicService;
import org.example.finishedbackend.utils.CacheUtils;
import org.example.finishedbackend.utils.Const;
import org.example.finishedbackend.utils.FlowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountPrivacyMapper privacyMapper;

    // 预处理listTypes的Id;
    private Set<Integer> types = null;
    @Autowired
    private AccountDetailsMapper accountDetailsMapper;

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
        TopicDTO dto = new TopicDTO(0, vo.getTitle(), vo.getContent().toJSONString(), vo.getType(), new Date(), uid);
        if (this.save(dto)) {
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误, 请联系管理员";
        }
    }

    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> voList = cacheUtils.getListFromCache(key, TopicPreviewVO.class);
        if (voList != null) return voList;
        Page<TopicDTO> page = Page.of(pageNumber + 1, 5);
        if (type == 0) {
            baseMapper.selectPage(page, Wrappers.<TopicDTO>query().orderByDesc("time"));
        } else {
            baseMapper.selectPage(page, Wrappers.<TopicDTO>query().eq("type", type).orderByDesc("time"));
        }
        List<TopicDTO> list = page.getRecords();
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

    @Override
    public TopicDetailVO getTopic(int tid) {
        TopicDetailVO vo = new TopicDetailVO();
        TopicDTO dto = baseMapper.selectById(tid);
        BeanUtils.copyProperties(dto, vo);
        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, dto.getUid()));
        return vo;
    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid) {
        AccountDetailsDTO details = accountDetailsMapper.selectById(uid);
        AccountDTO account = accountMapper.selectById(uid);
        AccountPrivacyDTO privacy = privacyMapper.selectById(uid);
        String[] ignores = privacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
    }

    private TopicPreviewVO resolveToPreview(TopicDTO dto) {
        AccountDTO account = accountMapper.selectById(dto.getUid());
        TopicPreviewVO vo = new TopicPreviewVO(dto.getId(), dto.getType(), dto.getTitle(), null, null, dto.getTime(), dto.getUid(), account.getUsername(), account.getAvatar());
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
