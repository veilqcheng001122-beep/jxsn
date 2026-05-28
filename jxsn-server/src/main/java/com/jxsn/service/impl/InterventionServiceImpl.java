package com.jxsn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxsn.common.Result;
import com.jxsn.dto.InterventionRequest;
import com.jxsn.entity.TeacherIntervention;
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
        TeacherIntervention intervention = new TeacherIntervention();

        Long sessionId = request.getSessionId();

        if (sessionId == null) {
            sessionId = request.getRecordId();
        }

        intervention.setSessionId(sessionId);

        // 现在还没接真实登录，先默认教师ID为2
        intervention.setTeacherId(
                request.getTeacherId() == null ? 2L : request.getTeacherId()
        );

        intervention.setInterventionAction(request.getCommand());
        intervention.setInterventionTime(LocalDateTime.now());

        teacherInterventionMapper.insert(intervention);

        return Result.success("教师远程干预指令已保存并下发");
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
}