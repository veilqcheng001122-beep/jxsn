package com.jxsn.dto;

import lombok.Data;

@Data
public class TrainingOperationRequest {

    private Long sessionId;

    private Integer stepIndex;

    private String paramName;

    private String paramValue;
}