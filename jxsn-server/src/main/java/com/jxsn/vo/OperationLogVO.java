package com.jxsn.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogVO {

    private Long logId;

    private Long sessionId;

    private Integer stepIndex;

    private String operationName;

    private String paramValue;

    private Boolean isCorrect;

    private String aiFeedback;

    private LocalDateTime opTime;
}