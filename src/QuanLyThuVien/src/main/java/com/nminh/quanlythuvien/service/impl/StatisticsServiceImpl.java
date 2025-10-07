package com.nminh.quanlythuvien.service.impl;

import com.nminh.quanlythuvien.entity.WarehouseLog;
import com.nminh.quanlythuvien.model.response.*;
import com.nminh.quanlythuvien.repository.BookOrderRepository;
import com.nminh.quanlythuvien.repository.OrderDetailRepository;
import com.nminh.quanlythuvien.repository.WarehouseLogRepository;
import com.nminh.quanlythuvien.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private BookOrderRepository bookOrderRepository;

    @Autowired
    private WarehouseLogRepository warehouseLogRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    public Object getStatistics(int month, int year) {
        double totalRevenue = bookOrderRepository.totalRevenueByMonth(month, year).orElse(0.0);
        double totalImportCost = warehouseLogRepository.totalImportCostByMonth(month, year).orElse(0.0);
        BookStatisticProjection bookBestSellerProjection = orderDetailRepository.findBookBestSeller(month, year);
        BookStatistic bookBestSeller = null;
        if (bookBestSellerProjection != null) {
            bookBestSeller = new BookStatistic().builder()
                    .bookName(bookBestSellerProjection.getBookName())
                    .bookId(bookBestSellerProjection.getBookId())
                    .quantity(bookBestSellerProjection.getQuantity())
                    .build();
        }
        BookStatisticProjection bookWorthSellerProjection = orderDetailRepository.findBookWorthSeller(month, year);
        BookStatistic bookWorthSeller = null;
        if (bookWorthSellerProjection != null) {
            bookWorthSeller = new BookStatistic().builder()
                    .bookId(bookWorthSellerProjection.getBookId())
                    .bookName(bookWorthSellerProjection.getBookName())
                    .quantity(bookWorthSellerProjection.getQuantity())
                    .build();
        }
        List<DailyRevenueProjection> dailyRevenueProjectionList = bookOrderRepository.dailyRevenue(month, year);
        List<DailyRevenue> dailyRevenueList = dailyRevenueProjectionList.stream()
                .map(p -> new DailyRevenue(p.getDay(), p.getRevenue()))
                .toList();
        return new StatisticsResponse().builder()
                .month(month)
                .year(year)
                .totalRevenue(totalRevenue)
                .totalImportCost(totalImportCost)
                .bestSeller(bookBestSeller)
                .worstSeller(bookWorthSeller)
                .dailyRevenueList(dailyRevenueList)
                .build();
    }
}
