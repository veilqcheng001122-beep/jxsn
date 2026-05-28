package com.jxsn.dto;

import lombok.Data;

@Data
public class InterventionRequest {

    // 前端现在传 recordId，这里先当作 sessionId 使用
    private Long recordId;

    private Long sessionId;

    private Long teacherId;

    private String studentName;

    private String command;
}