package com.planner.territory.controller;

import com.planner.territory.model.pjp.PlanParameter;
import com.planner.territory.service.CustomerService;
import com.planner.territory.service.TerritoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    TerritoryService territoryService;

    @Operation(
            operationId = "createPlan",
            summary = "Used To Create Plan",
            tags = {"/customer/createPlan"},

            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = {
                                    @Content(mediaType = "application/json")
                            }
                    )
            }
    )
    @CrossOrigin(origins = "*")
    @GetMapping(value="/customer/createPlan")
    public String createPlan(PlanParameter planParameter){
        territoryService.createPlan(planParameter);
        return "create plan";
    }



    @Operation(
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
    )
    @CrossOrigin(origins = "*")
    @GetMapping(value="/customer/getCustomers")
    public List<Customer> getCustomers(){
        return customerService.getCustomerList();
    }


    @Operation(
            operationId = "calculateDistanceX",
            summary = "Used To Calculate Distance",
            tags = {"/customer/calculateDistance"},

            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "OK",
                            content = {
                                    @Content(mediaType = "application/json")
                            }
                    )
            }
    )
    @CrossOrigin(origins = "*")
    @GetMapping(value="/customer/calculateDistance")
    public List<Customer> calculateDistanceX(){
        return customerService.calculateDistance();

    }


}
