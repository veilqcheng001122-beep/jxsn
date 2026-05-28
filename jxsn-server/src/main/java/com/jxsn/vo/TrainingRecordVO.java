package com.jxsn.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrainingRecordVO {

    private Long recordId;

    private Long sessionId;

    private String studentName;

    private String studentNo;

    private String processName;

    private Integer progress;

    private String status;

    private Integer warningCount;

    private String latestOperation;

    private LocalDateTime updateTime;
}