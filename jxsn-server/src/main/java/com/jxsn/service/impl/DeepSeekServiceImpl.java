package com.jxsn.service.impl;

import com.jxsn.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DeepSeekServiceImpl implements DeepSeekService {

    @Value("${deepseek.api-key:}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${deepseek.model:deepseek-v4-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateTrainingAdvice(
            String paramName,
            String paramValue,
            boolean correct,
            String localRuleFeedback
    ) {
        if (apiKey == null || apiKey.isBlank()) {
            return localRuleFeedback;
        }

        try {
            String prompt = buildPrompt(paramName, paramValue, correct, localRuleFeedback);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", "你是一个酿造实训智能指导助手。请根据学生提交的工艺参数，给出简洁、明确、可执行的中文指导建议。不要编造不存在的数据。"
                            ),
                            Map.of(
                                    "role", "user",
                                    "content", prompt
                            )
                    ),
                    "thinking", Map.of("type", "disabled"),
                    "stream", false
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map responseBody = response.getBody();

            if (responseBody == null) {
                return localRuleFeedback;
            }

            List choices = (List) responseBody.get("choices");

            if (choices == null || choices.isEmpty()) {
                return localRuleFeedback;
            }

            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");

            if (message == null || message.get("content") == null) {
                return localRuleFeedback;
            }

            return message.get("content").toString();

        } catch (Exception e) {
            System.err.println("DeepSeek 调用失败，使用本地规则反馈：" + e.getMessage());
            return localRuleFeedback;
        }
    }

    private String buildPrompt(
            String paramName,
            String paramValue,
            boolean correct,
            String localRuleFeedback
    ) {
        return """
                学生提交了一项实训操作参数，请生成教师风格的智能纠错建议。

                参数类型：%s
                参数值：%s
                系统判断：%s
                本地规则提示：%s

                要求：
                1. 用中文回答。
                2. 不要太长，控制在80字以内。
                3. 如果参数合规，鼓励学生继续下一步。
                4. 如果参数异常，说明问题并给出修正方向。
                5. 不要输出JSON，不要输出多余格式。
                """.formatted(
                paramName,
                paramValue,
                correct ? "合规" : "异常",
                localRuleFeedback
        );
    }
}