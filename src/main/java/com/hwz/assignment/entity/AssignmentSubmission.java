package com.hwz.assignment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("assignment_submission")
public class AssignmentSubmission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "submission_id", type = IdType.AUTO)
    private Long submissionId;

    @TableField("assignment_id")
    private Long assignmentId;

    @TableField("student_id")
    private Long studentId;

    @TableField("status")
    private String status;

    @TableField("answer_text")
    private String answerText;

    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    @TableField("graded_at")
    private LocalDateTime gradedAt;

    @TableField("score")
    private BigDecimal score;

    @TableField("feedback")
    private String feedback;

    @TableField("graded_by")
    private Long gradedBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
