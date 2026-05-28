package com.jxsn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("teacher_intervention")
public class TeacherIntervention {

    @TableId(type = IdType.AUTO)
    private Long interventionId;

    private Long sessionId;

    private Long teacherId;

    private String interventionAction;

    private LocalDateTime interventionTime;
}