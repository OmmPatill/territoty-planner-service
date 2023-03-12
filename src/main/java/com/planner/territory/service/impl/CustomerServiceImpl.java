package com.planner.territory.service.impl;

import com.planner.territory.businessservice.DistanceMatrixBusinessService;
import com.planner.territory.entity.CustomerEntity;
import com.planner.territory.repository.CustomerRepository;
import com.planner.territory.service.CustomerService;
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
