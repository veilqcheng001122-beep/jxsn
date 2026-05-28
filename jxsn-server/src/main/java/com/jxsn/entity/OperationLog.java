package com.jxsn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    private Long sessionId;

    private Integer stepIndex;

    private String realTimeValue;

    private Integer isCorrect;

    private String aiFeedback;

    private LocalDateTime opTime;
}