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
        return Result.success("教师干预建议已下发，等待学生查看并修正");
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

}