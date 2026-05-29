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

    /**
     * 实训会话状态：进行中 / 已完成
     */
    private String status;

    /**
     * 历史异常次数
     */
    private Integer warningCount;

    /**
     * 最新提交参数，例如 temperature=42
     */
    private String latestOperation;

    /**
     * 最新一次操作是否合规：1 合规，0 异常
     */
    private Integer latestIsCorrect;

    /**
     * 最新一次 AI 反馈
     */
    private String latestAiFeedback;

    /**
     * 异常状态：暂无操作 / 正常 / 异常待处理 / 已纠正
     */
    private String warningStatus;

    /**
     * 最新教师干预内容
     */
    private String latestTeacherIntervention;

    private LocalDateTime updateTime;
}