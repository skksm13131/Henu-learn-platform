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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_step")
public class LearningStep implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "step_id", type = IdType.AUTO)
    private Long stepId;

    @TableField("item_pk")
    private Long itemPk;

    @TableField("step_no")
    private Integer stepNo;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("tip")
    private String tip;

    @TableField("code")
    private String code;
}
