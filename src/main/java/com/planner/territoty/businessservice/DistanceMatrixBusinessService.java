package com.planner.territoty.businessservice;

import com.planner.territoty.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DistanceMatrixBusinessService {
    List<Customer> calculateOutletDistance(List<Customer> customerList);
}
