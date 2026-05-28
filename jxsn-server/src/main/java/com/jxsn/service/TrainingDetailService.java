package com.jxsn.service;

import com.jxsn.common.Result;

public interface TrainingDetailService {

    Result getTrainingDetail(Long sessionId);

    Result listTrainingRecords(
            String studentName,
            String studentNo,
            String processName,
            String startTime,
            String endTime
    );

    Result getMonitorData();
}