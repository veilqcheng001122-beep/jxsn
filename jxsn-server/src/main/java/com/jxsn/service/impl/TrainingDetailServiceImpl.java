package com.jxsn.service.impl;

import com.jxsn.common.Result;
import com.jxsn.mapper.OperationLogMapper;
import com.jxsn.mapper.TrainingSessionMapper;
import com.jxsn.service.TrainingDetailService;
import com.jxsn.vo.OperationLogVO;
import com.jxsn.vo.TrainingDetailVO;
import com.jxsn.vo.TrainingRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingDetailServiceImpl implements TrainingDetailService {

    @Autowired
    private TrainingSessionMapper trainingSessionMapper;

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Result getTrainingDetail(Long sessionId) {
        TrainingDetailVO detail = trainingSessionMapper.selectTrainingDetail(sessionId);

        if (detail == null) {
            return Result.fail("未找到对应实训会话");
        }

        List<OperationLogVO> logs = operationLogMapper.selectLogListBySessionId(sessionId);
        detail.setLogs(logs);

        return Result.success(detail);
    }

    @Override
    public Result listTrainingRecords(
            String studentName,
            String studentNo,
            String processName,
            String startTime,
            String endTime
    ) {
        List<TrainingRecordVO> records = trainingSessionMapper.selectTrainingRecordList(
                studentName,
                studentNo,
                processName,
                startTime,
                endTime
        );

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", records.size());

        return Result.success(result);
    }

    @Override
    public Result getMonitorData() {
        List<TrainingRecordVO> records = trainingSessionMapper.selectTrainingRecordList(
                null, null, null, null, null
        );

        int onlineCount = records.size();
        int runningCount = 0;
        int finishedCount = 0;
        int warningCount = 0;

        for (TrainingRecordVO record : records) {
            if ("进行中".equals(record.getStatus())) {
                runningCount++;
            }

            if ("已完成".equals(record.getStatus())) {
                finishedCount++;
            }

            if (record.getWarningCount() != null) {
                warningCount += record.getWarningCount();
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("onlineCount", onlineCount);
        data.put("runningCount", runningCount);
        data.put("finishedCount", finishedCount);
        data.put("warningCount", warningCount);

        return Result.success(data);
    }
}