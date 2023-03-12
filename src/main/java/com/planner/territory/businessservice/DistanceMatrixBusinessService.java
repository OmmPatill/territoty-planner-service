package com.planner.territory.businessservice;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DistanceMatrixBusinessService {
    List<Customer> calculateOutletDistance(List<Customer> customerList);
}
