package com.planner.territoty;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Customer {
    private Long customerId;
    private String customerName;
    private String customerCode;
    private Double lattitude;
    private Double longitude;
    private Double distance;
    private List<Customer> customerList = new ArrayList<>();
}
