package com.planner.territory.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {

    List<Customer> getCustomerList();
    List<Customer> calculateDistance();
}
