package com.jxsn.dto;

import lombok.Data;

import java.util.List;

@Data
public class InterventionRequest {

    private Long recordId;

    private Long sessionId;

    private Long teacherId;

    private String studentName;

    private String command;

    private String guidanceText;

    private Long logId;

    private List<InterventionParamDTO> paramList;
}