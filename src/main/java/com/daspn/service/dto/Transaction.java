package com.daspn.service.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Transaction implements Serializable {
    private double amount;
    private long timestamp;
}
