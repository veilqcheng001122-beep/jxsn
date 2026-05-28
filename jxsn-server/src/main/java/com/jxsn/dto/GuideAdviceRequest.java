package com.jxsn.dto;

import lombok.Data;

@Data
public class GuideAdviceRequest {

    private Long logId;

    private Long sessionId;

    private Long recordId;

    private String realTimeValue;

    private String errorType;
}