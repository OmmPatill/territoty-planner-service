package com.planner.territory.service.impl;

import com.planner.territory.entity.CustomerEntity;
import com.planner.territory.model.globalsequence.Customer;
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

    @Override
    public List<Customer> getCustomerList() {
        List<CustomerEntity> customerEntityList = customerRepository.findAll();
        List<Customer> customerList  = new ArrayList<Customer>();

        customerEntityList.stream().forEach(o->{
            Customer c = new Customer();
            c.setCustomerId(Math.toIntExact(o.getCustomerId()));
            c.setCustomerCode(o.getCustomerCode());
            c.setLongitude(o.getLongitude());
            c.setLongitude(o.getLatitude());
            customerList.add(c);
        });

        return customerList;
    }

    @Override
    public List<Customer> calculateDistance() {
        List<Customer> customerList = getCustomerList();
        return null;//distanceMatrixBusinessService.calculateOutletDistance(customerList);

    }

}
