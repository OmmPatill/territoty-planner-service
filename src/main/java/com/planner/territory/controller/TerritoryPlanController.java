package com.planner.territory.controller;

import com.planner.territory.service.TerritoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TerritoryPlanController {

    @Autowired
    TerritoryService territoryService;

    @Operation(
            operationId = "createPlan",
            summary = "Used to create territory plan",
            tags = {"createPlan"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(mediaType = "application/json")
                            }
                    )
            }
    )

    @PostMapping(value = "territoryPlanner/createPlan")
    public ResponseEntity<String> createPlan(@RequestBody Integer planId) {
        log.info("In territory plan creation rest service....");
        try {

            if (planId == null) {
                return new ResponseEntity<String>("Territory plan failed, the planId must not null ", HttpStatus.OK);
            }

            territoryService.createPlan(planId);

            return new ResponseEntity<String>("Territory plan successfully excepted...", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Territory plan failed to execute", HttpStatus.OK);
        }
    }
}
