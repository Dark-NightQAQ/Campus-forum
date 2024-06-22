package org.example.finishedbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.finishedbackend.entity.DTO.Interact;
import org.example.finishedbackend.entity.DTO.TopicDTO;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<TopicDTO> {
    @Insert("""
            <script>
                insert ignore into db_topic_interact_${type} values
                <foreach collection="interacts" item="item" separator=','>
                    (#{item.tid}, #{item.uid}, #{item.time})
                </foreach>
            </script>
            """)
    void addInteract(List<Interact> interacts, String type);

    @Delete("""
            <script>
                delete from db_topic_interact_${type} where
                <foreach collection="interacts" item="item" separator=" or ">
                    (tid = #{item.tid} and uid = #{item.uid})
                </foreach>
            </script>
            """)
    int deleteInteract(List<Interact> interacts, String type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid}
            """)
    int interactCount(int tid, String type);

    @Select("""
            select count(*) from db_topic_interact_${type} where tid = #{tid} and uid = #{uid}
            """)
     int userInteractCount(int tid, int uid, String type);

    @Select("""
            select * from db_topic_interact_collect left join db_topic on tid = db_topic.id 
            where db_topic_interact_collect.uid = #{uid}
            """)
    List<TopicDTO> collectTopics(int uid);
}
