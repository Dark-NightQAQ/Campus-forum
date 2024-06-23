package org.example.finishedbackend.service.Impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.example.finishedbackend.entity.DTO.*;
import org.example.finishedbackend.entity.VO.request.AddCommentVO;
import org.example.finishedbackend.entity.VO.request.TopicCreateVO;
import org.example.finishedbackend.entity.VO.request.TopicUpdateVO;
import org.example.finishedbackend.entity.VO.response.CommentVO;
import org.example.finishedbackend.entity.VO.response.TopicDetailVO;
import org.example.finishedbackend.entity.VO.response.TopicPreviewVO;
import org.example.finishedbackend.entity.VO.response.TopicTopVO;
import org.example.finishedbackend.mapper.*;
import org.example.finishedbackend.service.NotificationService;
import org.example.finishedbackend.service.TopicService;
import org.example.finishedbackend.utils.CacheUtils;
import org.example.finishedbackend.utils.Const;
import org.example.finishedbackend.utils.FlowUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
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

    @Resource
    TopicCommentMapper commentMapper;

    @Resource
    NotificationService notificationService;

    @Resource
    StringRedisTemplate template;

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
        if (!textLimitCheck(vo.getContent(), 20000)) return "文章字数过多, 请稍后再试";
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
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if (vo.getContent() == null) return "文章内容不能为空";
        if (!textLimitCheck(vo.getContent(), 20000)) return "文章字数过多, 请稍后再试";
        if (!types.contains(vo.getType())) return "文章类型非法！";
        baseMapper.update(null, Wrappers.<TopicDTO>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType()));
        return null;
    }

    @Override
    public String createComment(int uid, AddCommentVO vo) {
        if (!flowUtils.limitPeriodCounterCheck(Const.FORUM_TOPIC_COMMENT_COUNTER + uid, 2, 60)) return "发表评论频繁, 请稍后再试";
        if (!textLimitCheck(JSONObject.parseObject(vo.getContent()), 2000)) return "评论内容字数过多, 请重新编辑";
        TopicCommentDTO dto = new TopicCommentDTO();
        dto.setUid(uid);
        BeanUtils.copyProperties(vo, dto);
        dto.setTime(new Date());
        commentMapper.insert(dto);
        TopicDTO topicDTO = baseMapper.selectById(vo.getTid());
        AccountDTO accountDTO = accountMapper.selectById(uid);
        if (vo.getQuote() > 0) {
            TopicCommentDTO commentDTO = commentMapper.selectById(vo.getQuote());
            if (Objects.equals(accountDTO.getId(), commentDTO.getUid())) {
                notificationService.addNotification(commentDTO.getUid(), "您有新的帖子评论回复", accountDTO.getUsername()+"回复了你发表的评论", "success", "/index/topic-detail/"+commentDTO.getTid());
            }
        } else if (!Objects.equals(accountDTO.getId(), topicDTO.getUid())) {
            notificationService.addNotification(topicDTO.getUid(), "您有新的帖子评论回复", accountDTO.getUsername()+"回复了你发表的主题:"+topicDTO.getTitle(), "success", "/index/topic-detail/"+topicDTO.getId());
        }
        return null;
    }

    @Override
    public List<CommentVO> comments(int tid, int page) {
        Page<TopicCommentDTO> comments = Page.of(page + 1, 10);
        commentMapper.selectPage(comments, Wrappers.<TopicCommentDTO>query().eq("tid", tid));
        return comments.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getQuote() > 0) {
                TopicCommentDTO commentDTO = commentMapper.selectOne(Wrappers.<TopicCommentDTO>query().eq("id", dto.getQuote()).orderByAsc("time"));
                if (commentDTO != null) {
                    JSONObject object = JSONObject.parseObject(commentDTO.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {});
                    vo.setQuote(builder.toString());
                } else {
                    vo.setQuote("此评论已被删除");
                }
            }
            CommentVO.User user = new CommentVO.User();
            vo.setUser(this.fillUserDetailsByPrivacy(user, dto.getUid()));
            return vo;
        }).toList();
    }

    @Override
    public void deleteComment(int uid, int id) {
        commentMapper.delete(Wrappers.<TopicCommentDTO>query().eq("id", id).eq("uid", uid));
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
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid).stream().map(topic -> {
            TopicPreviewVO vo = new TopicPreviewVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }


    @Override
    public List<TopicTopVO> listTopTopics() {
        List<TopicDTO> topics = baseMapper.selectList(Wrappers.<TopicDTO>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> new TopicTopVO(topic.getId(), topic.getTitle(), topic.getTime())).toList();
    }

    @Override
    public TopicDetailVO getTopic(int tid, int uid) {
        TopicDetailVO vo = new TopicDetailVO();
        TopicDTO dto = baseMapper.selectById(tid);
        BeanUtils.copyProperties(dto, vo);
        TopicDetailVO.User user = new TopicDetailVO.User();
        TopicDetailVO.Interact interact = new TopicDetailVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect"));
        vo.setInteract(interact);
        vo.setUser(this.fillUserDetailsByPrivacy(user, dto.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<TopicCommentDTO>query().eq("tid", tid)));
        return vo;
    }

    private boolean hasInteract(int tid, int uid, String type) {
        String key = tid + ":" + uid;
        if (template.opsForHash().hasKey(type, key))
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()) {
            template.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
    }

    private final Map<String, Boolean> map = new HashMap<>();
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
    private void saveInteractSchedule(String type) {
        if (!map.getOrDefault(type, false)) {
            map.put(type, true);
            service.schedule(() -> {
                this.saveInteract(type);
                map.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }

    private void saveInteract(String type) {
        synchronized (type.intern()) {
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            template.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString()))
                    check.add(Interact.parseInteract(k.toString(), type));
                else
                    uncheck.add(Interact.parseInteract(k.toString(), type));
            });
            if (!check.isEmpty()) baseMapper.addInteract(check, type);
            if (!uncheck.isEmpty()) baseMapper.deleteInteract(uncheck, type);
            template.delete(type);
        }
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
        TopicPreviewVO vo = new TopicPreviewVO(dto.getId(), dto.getType(), dto.getTitle(), null, null, dto.getTime(), dto.getUid(), account.getUsername(), account.getAvatar(),
                baseMapper.interactCount(dto.getId(), "like"),
                baseMapper.interactCount(dto.getId(), "collect"));
        List<String> images = new LinkedList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(dto.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> images.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imagesHandler) {
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text) {
                if (previewText.length() >= 300) continue;
                previewText.append(text);
            } else if (insert instanceof Map<?,?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(imagesHandler);
            }
        }
    }

    private boolean textLimitCheck(JSONObject object, int max) {
        long length = 0;
        for (Object ops : object.getJSONArray("ops")) {
            length += JSONObject.from(ops).getString("insert").length();
            if (length > max) return false;
        }
        return true;
    }

}
