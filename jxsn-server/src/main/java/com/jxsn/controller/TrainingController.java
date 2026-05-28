package com.jxsn.controller;

import com.jxsn.common.Result;
import com.jxsn.dto.InterventionRequest;
import com.jxsn.service.InterventionService;
import com.jxsn.service.TrainingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Autowired
    private InterventionService interventionService;
    @Autowired
    private TrainingDetailService trainingDetailService;

    @PostMapping("/intervene")
    public Result sendIntervention(@RequestBody InterventionRequest request) {
        return interventionService.sendIntervention(request);
    }

    @GetMapping("/intervention/list")
    public Result listInterventions(@RequestParam(required = false) Long sessionId) {
        return interventionService.listInterventions(sessionId);
    }

    @GetMapping("/monitor")
    public Result getMonitorData() {
        return trainingDetailService.getMonitorData();
    }

    @GetMapping("/records")
    public Result getTrainingRecords(
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String processName,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        return trainingDetailService.listTrainingRecords(
                studentName,
                studentNo,
                processName,
                startTime,
                endTime
        );
    }


    @GetMapping("/detail")
    public Result getTrainingDetail(@RequestParam Long recordId) {
        return trainingDetailService.getTrainingDetail(recordId);
    }

}