package com.jxsn.service.impl;

import com.jxsn.common.Result;
import com.jxsn.dto.TrainingOperationRequest;
import com.jxsn.entity.OperationLog;
import com.jxsn.mapper.OperationLogMapper;
import com.jxsn.service.TrainingOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrainingOperationServiceImpl implements TrainingOperationService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Result submitOperation(TrainingOperationRequest request) {
        if (request.getSessionId() == null) {
            return Result.fail("缺少实训会话ID");
        }

        if (request.getStepIndex() == null) {
            return Result.fail("缺少操作步骤");
        }

        if (!hasText(request.getParamName())) {
            return Result.fail("缺少参数类型");
        }

        if (!hasText(request.getParamValue())) {
            return Result.fail("缺少参数值");
        }

        String realTimeValue = request.getParamName() + "=" + request.getParamValue();

        boolean correct = checkParam(request.getParamName(), request.getParamValue());

        String feedback = generateFeedback(request.getParamName(), request.getParamValue(), correct);

        OperationLog log = new OperationLog();
        log.setSessionId(request.getSessionId());
        log.setStepIndex(request.getStepIndex());
        log.setRealTimeValue(realTimeValue);
        log.setIsCorrect(correct ? 1 : 0);
        log.setAiFeedback(feedback);
        log.setOpTime(LocalDateTime.now());

        operationLogMapper.insert(log);

        return Result.success(log);
    }

    private boolean checkParam(String paramName, String paramValue) {
        try {
            if ("temperature".equals(paramName)) {
                double value = Double.parseDouble(paramValue);
                return value >= 30 && value <= 38;
            }

            if ("humidity".equals(paramName)) {
                double value = Double.parseDouble(paramValue);
                return value >= 60 && value <= 85;
            }

            if ("pressure".equals(paramName)) {
                double value = Double.parseDouble(paramValue);
                return value >= 0.10 && value <= 0.25;
            }

            if ("duration".equals(paramName)) {
                int value = Integer.parseInt(paramValue);
                return value >= 60 && value <= 120;
            }

            if ("microbe".equals(paramName)) {
                double value = Double.parseDouble(paramValue);
                return value >= 0.3 && value <= 0.8;
            }

            if ("seal_status".equals(paramName)) {
                return "已密封".equals(paramValue) || "正常".equals(paramValue);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateFeedback(String paramName, String paramValue, boolean correct) {
        if (correct) {
            return "当前参数 " + paramName + "=" + paramValue + " 符合工艺要求，可继续进行下一步实训。";
        }

        if ("temperature".equals(paramName)) {
            return "系统检测到温度参数异常，请将温度控制在30℃至38℃范围内。";
        }

        if ("humidity".equals(paramName)) {
            return "系统检测到湿度参数异常，请将湿度控制在60%至85%范围内。";
        }

        if ("pressure".equals(paramName)) {
            return "系统检测到压力参数异常，请检查蒸馏设备阀门状态，并将压力控制在0.10MPa至0.25MPa范围内。";
        }

        if ("duration".equals(paramName)) {
            return "系统检测到工艺时长异常，请将操作时长控制在60至120分钟范围内。";
        }

        if ("microbe".equals(paramName)) {
            return "系统检测到微生物浓度异常，请检查发酵环境并将浓度控制在0.3至0.8范围内。";
        }

        if ("seal_status".equals(paramName)) {
            return "系统检测到密封状态异常，请检查容器封口，确认密封完成后继续操作。";
        }

        return "系统检测到当前操作存在异常，请检查参数是否符合工艺标准。";
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}