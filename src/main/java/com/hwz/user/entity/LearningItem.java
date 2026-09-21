package com.hwz.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_item")
public class LearningItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "item_pk", type = IdType.AUTO)
    private Long itemPk;

    @TableField("json_id")
    private Long jsonId;

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("category")
    private String category;

    @TableField("difficulty")
    private String difficulty;

    @TableField("duration")
    private String duration;

    @TableField("prerequisites")
    private String prerequisites;

    @TableField("objectives")
    private String objectives;

    @TableField("features")
    private String features;

    @TableField("status")
    private String status;

    @TableField("template_path")
    private String templatePath;

    @TableField("author_id")
    private Long authorId;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
