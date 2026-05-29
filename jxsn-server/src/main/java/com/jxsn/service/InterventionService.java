package com.jxsn.service;

import com.jxsn.common.Result;
import com.jxsn.dto.InterventionRequest;

public interface InterventionService {

    Result sendIntervention(InterventionRequest request);

    Result listInterventions(Long sessionId);

    Result markInterventionRead(Long interventionId);
}