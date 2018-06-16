package com.daspn.service;

import com.daspn.service.dto.Statistics;
import com.daspn.service.dto.Transaction;

public interface StatisticsService {
    Long addTransaction(Transaction transaction);

    Statistics getStatistics();
}
