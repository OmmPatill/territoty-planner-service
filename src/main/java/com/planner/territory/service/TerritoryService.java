package com.planner.territory.service;

import com.planner.territory.model.pjp.PlanParameter;
import org.springframework.stereotype.Service;

@Service
public interface TerritoryService {

    void createPlan(PlanParameter planParameter);

    void createPlan(Long planId);
}
