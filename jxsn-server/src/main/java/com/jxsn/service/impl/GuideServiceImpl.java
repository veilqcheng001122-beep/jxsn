package com.jxsn.service.impl;

import com.jxsn.common.Result;
import com.jxsn.dto.GuideAdviceRequest;
import com.jxsn.entity.OperationLog;
import com.jxsn.mapper.OperationLogMapper;
import com.jxsn.service.GuideService;
import com.jxsn.vo.GuideLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuideServiceImpl implements GuideService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Result listGuideLogs(
            String studentName,
            String studentNo,
            String processName,
            Boolean onlyError
    ) {
        List<GuideLogVO> logs = operationLogMapper.selectGuideLogList(
                studentName,
                studentNo,
                processName,
                onlyError
        );

        return Result.success(logs);
    }

    @Override
    public Result generateAdvice(GuideAdviceRequest request) {
        OperationLog log = operationLogMapper.selectById(request.getLogId());

        if (log == null) {
            return Result.fail("未找到对应操作日志");
        }

        String realTimeValue = log.getRealTimeValue();
        String advice = buildAdvice(realTimeValue);

        OperationLog updateLog = new OperationLog();
        updateLog.setLogId(log.getLogId());
        updateLog.setAiFeedback(advice);

        operationLogMapper.updateById(updateLog);

        return Result.success(advice);
    }

    private String buildAdvice(String realTimeValue) {
        if (realTimeValue == null) {
            return "系统检测到当前操作存在异常，请检查本步骤的工艺参数并重新提交。";
        }

        if (realTimeValue.startsWith("temperature=")) {
            return "系统检测到温度参数异常，请对照当前工序的标准温度范围，适当调整加热或冷却状态，并重新提交温度参数。";
        }

        if (realTimeValue.startsWith("humidity=")) {
            return "系统检测到湿度参数异常，请检查发酵环境湿度设置，适当降低或提高湿度，使其回到标准工艺范围内。";
        }

        if (realTimeValue.startsWith("pressure=")) {
            return "系统检测到压力参数异常，请检查蒸馏设备阀门状态和加热强度，避免压力过高影响安全与蒸馏效果。";
        }

        if (realTimeValue.startsWith("duration=")) {
            return "系统检测到工艺时长异常，请核对当前步骤的标准操作时间，避免过短或过长影响实训结果。";
        }

        if (realTimeValue.startsWith("microbe=")) {
            return "系统检测到微生物浓度异常，请检查发酵环境、温湿度条件及原料状态，并根据提示重新调整。";
        }

        if (realTimeValue.startsWith("seal_status=")) {
            return "系统检测到容器密封状态异常，请重新检查封口情况，确认密封完成后再继续后续操作。";
        }

        return "系统检测到当前操作存在异常，请检查本步骤参数是否符合标准工艺要求，并根据提示重新调整。";
    }
}