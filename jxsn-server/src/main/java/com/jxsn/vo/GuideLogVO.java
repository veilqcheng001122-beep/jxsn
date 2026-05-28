package com.jxsn.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideLogVO {

    private Long logId;

    private Long sessionId;

    private Long recordId;

    private String studentName;

    private String studentNo;

    private String processName;

    private Integer stepIndex;

    private String realTimeValue;

    private Integer isCorrect;

    private String status;

    private String errorType;

    private String aiFeedback;

    private LocalDateTime opTime;
}