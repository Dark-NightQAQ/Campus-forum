package org.example.finishedbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.example.finishedbackend.entity.DTO.TopicDTO;

import java.util.List;

@Mapper
public interface TopicMapper extends BaseMapper<TopicDTO> {
//    @Select("""
//            select * from db_topic left join db_account on uid = db_account.id
//            order by `time` desc limit #{start}, 5
//             """)
//    List<TopicDTO> topicList(int start);
//
//    @Select("""
//            select * from db_topic left join db_account on uid = db_account.id
//            where type = #{type}
//            order by `time` desc limit #{start}, 5
//             """)
//    List<TopicDTO> topicListByType(int start, int type);
}
