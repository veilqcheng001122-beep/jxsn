package com.jxsn.controller;

import com.jxsn.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    private static final List<Map<String, Object>> RULE_LIST = new ArrayList<>();
    private static final AtomicLong ID_GENERATOR = new AtomicLong(3);

    static {
        Map<String, Object> rule1 = new HashMap<>();
        rule1.put("ruleId", 1L);
        rule1.put("ruleName", "发酵温度超阈值规则");
        rule1.put("processType", "fermentation");
        rule1.put("processName", "发酵控制");
        rule1.put("condition", "temperature > 38");
        rule1.put("threshold", "30℃ - 38℃");
        rule1.put("adviceTemplate", "当前发酵温度偏高，请降低温度并重新观察发酵状态。");
        rule1.put("status", "enabled");
        rule1.put("createTime", "2026-05-27 10:30:00");
        RULE_LIST.add(rule1);

        Map<String, Object> rule2 = new HashMap<>();
        rule2.put("ruleId", 2L);
        rule2.put("ruleName", "蒸馏压力异常规则");
        rule2.put("processType", "distillation");
        rule2.put("processName", "蒸馏操作");
        rule2.put("condition", "pressure > 0.25");
        rule2.put("threshold", "0.10MPa - 0.25MPa");
        rule2.put("adviceTemplate", "当前蒸馏压力偏高，请检查阀门状态并适当降低加热强度。");
        rule2.put("status", "enabled");
        rule2.put("createTime", "2026-05-27 10:35:00");
        RULE_LIST.add(rule2);
    }

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> listRules(
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String processType,
            @RequestParam(required = false) String status
    ) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> rule : RULE_LIST) {
            boolean match = true;

            if (ruleName != null && !ruleName.isEmpty()) {
                String currentRuleName = String.valueOf(rule.get("ruleName"));
                if (!currentRuleName.contains(ruleName)) {
                    match = false;
                }
            }

            if (processType != null && !processType.isEmpty()) {
                if (!processType.equals(rule.get("processType"))) {
                    match = false;
                }
            }

            if (status != null && !status.isEmpty()) {
                if (!status.equals(rule.get("status"))) {
                    match = false;
                }
            }

            if (match) {
                result.add(rule);
            }
        }

        return Result.success(result);
    }

    @PostMapping("/create")
    public Result<String> createRule(@RequestBody Map<String, Object> data) {
        Long ruleId = ID_GENERATOR.getAndIncrement();

        data.put("ruleId", ruleId);
        data.put("processName", convertProcessName(String.valueOf(data.get("processType"))));
        data.put("createTime", "2026-05-27 11:00:00");

        if (data.get("status") == null || String.valueOf(data.get("status")).isEmpty()) {
            data.put("status", "enabled");
        }

        RULE_LIST.add(data);

        System.out.println("新增异常规则：" + data);

        return Result.success("异常规则新增成功");
    }

    @PutMapping("/update")
    public Result<String> updateRule(@RequestBody Map<String, Object> data) {
        Long ruleId = Long.valueOf(String.valueOf(data.get("ruleId")));

        for (Map<String, Object> rule : RULE_LIST) {
            Long currentId = Long.valueOf(String.valueOf(rule.get("ruleId")));

            if (currentId.equals(ruleId)) {
                rule.put("ruleName", data.get("ruleName"));
                rule.put("processType", data.get("processType"));
                rule.put("processName", convertProcessName(String.valueOf(data.get("processType"))));
                rule.put("condition", data.get("condition"));
                rule.put("threshold", data.get("threshold"));
                rule.put("adviceTemplate", data.get("adviceTemplate"));
                rule.put("status", data.get("status"));

                System.out.println("修改异常规则：" + rule);

                return Result.success("异常规则修改成功");
            }
        }

        return Result.fail("未找到对应规则");
    }

    @PutMapping("/status")
    public Result<String> updateStatus(@RequestBody Map<String, Object> data) {
        Long ruleId = Long.valueOf(String.valueOf(data.get("ruleId")));
        String status = String.valueOf(data.get("status"));

        for (Map<String, Object> rule : RULE_LIST) {
            Long currentId = Long.valueOf(String.valueOf(rule.get("ruleId")));

            if (currentId.equals(ruleId)) {
                rule.put("status", status);

                System.out.println("修改规则状态：" + rule);

                return Result.success("规则状态修改成功");
            }
        }

        return Result.fail("未找到对应规则");
    }

    @DeleteMapping("/delete")
    public Result<String> deleteRule(@RequestParam Long ruleId) {
        Iterator<Map<String, Object>> iterator = RULE_LIST.iterator();

        while (iterator.hasNext()) {
            Map<String, Object> rule = iterator.next();
            Long currentId = Long.valueOf(String.valueOf(rule.get("ruleId")));

            if (currentId.equals(ruleId)) {
                iterator.remove();

                System.out.println("删除异常规则，ruleId = " + ruleId);

                return Result.success("异常规则删除成功");
            }
        }

        return Result.fail("未找到对应规则");
    }

    private String convertProcessName(String processType) {
        if ("fermentation".equals(processType)) {
            return "发酵控制";
        }

        if ("distillation".equals(processType)) {
            return "蒸馏操作";
        }

        if ("aging".equals(processType)) {
            return "陈酿管理";
        }

        if ("raw_material".equals(processType)) {
            return "原料处理";
        }

        return "未知工序";
    }
}