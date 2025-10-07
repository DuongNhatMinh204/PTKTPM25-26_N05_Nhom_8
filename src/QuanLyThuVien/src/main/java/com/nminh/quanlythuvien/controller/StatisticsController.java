package com.nminh.quanlythuvien.controller;

import com.nminh.quanlythuvien.model.response.ApiResponse;
import com.nminh.quanlythuvien.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ApiResponse getStatistics(@RequestParam int month, @RequestParam int year) {
        log.info("getStatistics month={}, year={}", month, year);
        ApiResponse apiResponse = new ApiResponse(statisticsService.getStatistics(month, year));
        log.info("getStatistics apiResponse={}", apiResponse);
        return apiResponse;
    }
}
