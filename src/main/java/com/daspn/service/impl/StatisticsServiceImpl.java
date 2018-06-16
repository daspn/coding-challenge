package com.daspn.service.impl;

import com.daspn.service.StatisticsService;
import com.daspn.service.dto.Statistics;
import com.daspn.service.dto.Transaction;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private Map<Long, List<Transaction>> store = new ConcurrentHashMap<>();
    private Long transationsSequence = 0L;

    @Override
    public Long addTransaction(Transaction transaction) {
        long storeKey = Instant.ofEpochMilli(transaction.getTimestamp()).getEpochSecond();
        List<Transaction> list = store.getOrDefault(storeKey, new ArrayList<>());
        list.add(transaction);
        store.put(storeKey, list);
        return ++transationsSequence;
    }

    @Override
    public Statistics getStatistics() {
        List<Transaction> transactions = new ArrayList<>();
        long currentTimestamp = Instant.now().getEpochSecond();
        for (int i = 0; i < 60; i++) {
            long storeKey = currentTimestamp - i;
            transactions.addAll(store.getOrDefault(storeKey, new ArrayList<>()));
        }

        if (transactions.isEmpty()) {
            return null;
        }

        return Statistics
                .builder()
                .sum(transactions.stream().mapToDouble(Transaction::getAmount).sum())
                .avg(transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble())
                .max(transactions.stream().mapToDouble(Transaction::getAmount).max().getAsDouble())
                .min(transactions.stream().mapToDouble(Transaction::getAmount).min().getAsDouble())
                .count(transactions.size())
                .build();
    }
}
