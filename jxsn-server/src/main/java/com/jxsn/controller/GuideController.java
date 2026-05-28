package com.jxsn.controller;

import com.jxsn.common.Result;
import com.jxsn.dto.GuideAdviceRequest;
import com.jxsn.service.GuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @Autowired
    private GuideService guideService;

    @GetMapping("/list")
    public Result listGuideLogs(
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String processName,
            @RequestParam(required = false, defaultValue = "true") Boolean onlyError
    ) {
        return guideService.listGuideLogs(studentName, studentNo, processName, onlyError);
    }

    @PostMapping("/advice/generate")
    public Result generateAdvice(@RequestBody GuideAdviceRequest request) {
        return guideService.generateAdvice(request);
    }
}