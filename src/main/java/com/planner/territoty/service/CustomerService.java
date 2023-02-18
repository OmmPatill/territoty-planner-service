package com.planner.territoty.service;

import com.planner.territoty.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {

    List<Customer> getCustomerList();
    List<Customer> calculateDistance();
}
