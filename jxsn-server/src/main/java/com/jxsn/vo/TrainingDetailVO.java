package com.jxsn.vo;

import lombok.Data;

import java.util.List;

@Data
public class TrainingDetailVO {

    private Long recordId;

    private Long sessionId;

    private String studentName;

    private String studentNo;

    private String processName;

    private Integer progress;

    private String status;

    private List<OperationLogVO> logs;
}