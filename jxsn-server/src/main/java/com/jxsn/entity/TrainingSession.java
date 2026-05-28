package com.jxsn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("training_session")
public class TrainingSession {

    @TableId(type = IdType.AUTO)
    private Long sessionId;

    private Long studentId;

    private Long templateId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String sessionStatus;

    private String finalReportUrl;
}