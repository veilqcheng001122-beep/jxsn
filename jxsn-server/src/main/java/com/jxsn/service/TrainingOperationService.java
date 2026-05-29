package com.jxsn.service;

import com.jxsn.common.Result;
import com.jxsn.dto.TrainingOperationRequest;

public interface TrainingOperationService {

    Result submitOperation(TrainingOperationRequest request);
}