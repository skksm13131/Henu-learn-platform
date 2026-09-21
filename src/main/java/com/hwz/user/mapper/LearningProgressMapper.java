package com.hwz.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hwz.user.entity.LearningProgress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {
    @Update({
            "INSERT INTO learning_record (user_id, item_pk, first_learn_time, learn_duration_sec)",
            "VALUES (#{userId}, #{itemPk}, NOW(), #{deltaSec})",
            "ON DUPLICATE KEY UPDATE",
            "  first_learn_time = IFNULL(first_learn_time, NOW()),",
            "  learn_duration_sec = learn_duration_sec + VALUES(learn_duration_sec),",
            "  updated_at = CURRENT_TIMESTAMP"
    })
    int upsertLearnDuration(@Param("userId") Long userId,
                            @Param("itemPk") Long itemPk,
                            @Param("deltaSec") Long deltaSec);

    @Update({
            "UPDATE learning_record",
            "SET learn_duration_sec = COALESCE(learn_duration_sec, 0) + #{deltaSec},",
            "    updated_at = CURRENT_TIMESTAMP",
            "WHERE user_id = #{userId}",
            "  AND item_pk = #{itemPk}",
            "  AND first_learn_time IS NOT NULL"
    })
    int addLearnDuration(@Param("userId") Long userId,
                         @Param("itemPk") Long itemPk,
                         @Param("deltaSec") Long deltaSec);

    @Select({
            "SELECT learn_duration_sec",
            "FROM learning_record",
            "WHERE user_id = #{userId} AND item_pk = #{itemPk}",
            "LIMIT 1"
    })
    Long selectLearnDurationSec(@Param("userId") Long userId,
                                @Param("itemPk") Long itemPk);
}
