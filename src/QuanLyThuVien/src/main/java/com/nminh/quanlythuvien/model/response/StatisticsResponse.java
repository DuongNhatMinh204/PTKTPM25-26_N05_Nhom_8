package com.nminh.quanlythuvien.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsResponse {
    private int month;
    private int year;
    private Double totalRevenue;
    private Double totalImportCost;
    private BookStatistic bestSeller;
    private BookStatistic worstSeller;
    private List<DailyRevenue> dailyRevenueList;
}
