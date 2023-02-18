package com.planner.territoty.controller;

import com.planner.territoty.Customer;
import com.planner.territoty.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    /*@Operation(
            operationId = "getCustomers",
            summary = "Used To Get All Customers",
            tags = {"/customer/getCustomers"},

            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = {
                                    @Content(mediaType = "application/json")
                            }
                    )
            }
    )*/
    @CrossOrigin(origins = "*")
    @GetMapping(value="/customer/getCustomers")
    public List<Customer> getCustomers(){
        return customerService.getCustomerList();
    }
    /* ToDO from here */
}
