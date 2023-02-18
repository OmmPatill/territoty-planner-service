package com.planner.territoty;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class Customer {
    private Long customerId;
    private String customerName;
    private String customerCode;
    private Double lattitude;
    private Double longitude;
    private Double distance;
    private List<Customer> customerList = new ArrayList<>();
}
