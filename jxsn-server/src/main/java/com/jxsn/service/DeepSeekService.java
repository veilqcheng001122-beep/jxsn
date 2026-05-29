package com.jxsn.service;

public interface DeepSeekService {

    String generateTrainingAdvice(
            String paramName,
            String paramValue,
            boolean correct,
            String localRuleFeedback
    );
}