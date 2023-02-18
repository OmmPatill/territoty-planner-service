package com.planner.territoty;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
public class Customer {
    private Long customerId;
    private String customerName;
    private String customerCode;
    private Double lattitude;
    private Double longitude;
}
