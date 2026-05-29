package com.jxsn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxsn.common.Result;
import com.jxsn.dto.InterventionRequest;
import com.jxsn.entity.OperationLog;
import com.jxsn.entity.TeacherIntervention;
import com.jxsn.mapper.OperationLogMapper;
import com.jxsn.mapper.TeacherInterventionMapper;
import com.jxsn.service.InterventionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InterventionServiceImpl implements InterventionService {

    @Autowired
    private TeacherInterventionMapper teacherInterventionMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Result sendIntervention(InterventionRequest request) {
        Long sessionId = request.getSessionId();

        if (sessionId == null) {
            sessionId = request.getRecordId();
        }

        if (sessionId == null) {
            return Result.fail("缺少实训会话ID");
        }

        if (request.getParamList() == null || request.getParamList().isEmpty()) {
            return Result.fail("缺少需要修正的参数");
        }

        if (!hasText(request.getGuidanceText())) {
            return Result.fail("缺少教师指导话语");
        }

        StringBuilder actionBuilder = new StringBuilder();
        actionBuilder.append("修正参数：");

        for (int i = 0; i < request.getParamList().size(); i++) {
            var item = request.getParamList().get(i);

            if (!hasText(item.getParamName()) || !hasText(item.getParamValue())) {
                return Result.fail("修正参数名称或参数值不能为空");
            }

            actionBuilder
                    .append(item.getParamName())
                    .append("=")
                    .append(item.getParamValue());

            if (i < request.getParamList().size() - 1) {
                actionBuilder.append("，");
            }
        }

        actionBuilder.append("；指导话语：").append(request.getGuidanceText());

        // 1. 保存教师远程干预日志
        TeacherIntervention intervention = new TeacherIntervention();
        intervention.setSessionId(sessionId);
        intervention.setTeacherId(request.getTeacherId() == null ? 2L : request.getTeacherId());
        intervention.setInterventionAction(actionBuilder.toString());
        intervention.setInterventionTime(LocalDateTime.now());

        teacherInterventionMapper.insert(intervention);

        // 2. 逐个修改学生操作参数
        for (var item : request.getParamList()) {
            updateOrInsertOperationLog(
                    sessionId,
                    item.getParamName(),
                    item.getParamValue(),
                    request.getGuidanceText()
            );
        }

        return Result.success("远程干预已保存，多个学生参数与指导话语已更新");
    }

    @Override
    public Result listInterventions(Long sessionId) {
        QueryWrapper<TeacherIntervention> wrapper = new QueryWrapper<>();

        if (sessionId != null) {
            wrapper.eq("session_id", sessionId);
        }

        wrapper.orderByDesc("intervention_time");

        return Result.success(teacherInterventionMapper.selectList(wrapper));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
    private void updateOrInsertOperationLog(
            Long sessionId,
            String paramName,
            String paramValue,
            String guidanceText
    ) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
                .likeRight("real_time_value", paramName + "=")
                .orderByDesc("op_time")
                .orderByDesc("log_id")
                .last("LIMIT 1");

        OperationLog targetLog = operationLogMapper.selectOne(wrapper);

        if (targetLog != null) {
            OperationLog updateLog = new OperationLog();
            updateLog.setLogId(targetLog.getLogId());
            updateLog.setRealTimeValue(paramName + "=" + paramValue);
            updateLog.setIsCorrect(1);
            updateLog.setAiFeedback(guidanceText);
            updateLog.setOpTime(LocalDateTime.now());

            operationLogMapper.updateById(updateLog);
        } else {
            OperationLog newLog = new OperationLog();
            newLog.setSessionId(sessionId);
            newLog.setStepIndex(getNextStepIndex(sessionId));
            newLog.setRealTimeValue(paramName + "=" + paramValue);
            newLog.setIsCorrect(1);
            newLog.setAiFeedback(guidanceText);
            newLog.setOpTime(LocalDateTime.now());

            operationLogMapper.insert(newLog);
        }
    }
    private Integer getNextStepIndex(Long sessionId) {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId)
                .orderByDesc("step_index")
                .last("LIMIT 1");

        OperationLog latestLog = operationLogMapper.selectOne(wrapper);

        if (latestLog == null || latestLog.getStepIndex() == null) {
            return 1;
        }

        return latestLog.getStepIndex() + 1;
    }
}