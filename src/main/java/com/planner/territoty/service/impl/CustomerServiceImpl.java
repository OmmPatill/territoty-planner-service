package com.planner.territoty.service.impl;

import com.planner.territoty.Customer;
import com.planner.territoty.businessservice.DistanceMatrixBusinessService;
import com.planner.territoty.entity.CustomerEntity;
import com.planner.territoty.repository.CustomerRepository;
import com.planner.territoty.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DistanceMatrixBusinessService distanceMatrixBusinessService;
    @Override
    public List<Customer> getCustomerList() {
        List<CustomerEntity> customerEntityList = customerRepository.findAll();
        List<Customer> customerList  = new ArrayList<Customer>();

        customerEntityList.stream().forEach(o->{
            Customer c = new Customer();
            c.setCustomerId(o.getCustomerId());
            c.setCustomerCode(o.getCustomerCode());
            c.setCustomerName(o.getCustomerName());
            c.setLongitude(o.getLongitude());
            c.setLattitude(o.getLattitude());
            customerList.add(c);
        });

        return customerList;
    }

    @Override
    public List<Customer> calculateDistance() {
        List<Customer> customerList = getCustomerList();
        return distanceMatrixBusinessService.calculateOutletDistance(customerList);

    }

}
