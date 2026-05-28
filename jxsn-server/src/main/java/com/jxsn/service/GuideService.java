package com.jxsn.service;

import com.jxsn.common.Result;
import com.jxsn.dto.GuideAdviceRequest;

public interface GuideService {

    Result listGuideLogs(
            String studentName,
            String studentNo,
            String processName,
            Boolean onlyError
    );

    Result generateAdvice(GuideAdviceRequest request);
}