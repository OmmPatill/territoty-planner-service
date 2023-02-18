package com.planner.territoty.service;

import com.planner.territoty.model.PlanParameter;
import org.springframework.stereotype.Service;

@Service
public interface TerritoryService {

    void createPlan(PlanParameter planParameter);
}
