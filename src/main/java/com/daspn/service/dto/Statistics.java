package com.daspn.service.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Statistics implements Serializable {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Integer count;
}
