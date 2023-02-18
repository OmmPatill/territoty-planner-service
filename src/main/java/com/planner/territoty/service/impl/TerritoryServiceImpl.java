package com.planner.territoty.service.impl;

import com.planner.territoty.entity.PlanParameterEntity;
import com.planner.territoty.model.PlanParameter;
import com.planner.territoty.repository.PlanParameterRepository;
import com.planner.territoty.service.TerritoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerritoryServiceImpl implements TerritoryService {

    @Autowired
    PlanParameterRepository planParameterRepository;

    @Override
    public void createPlan(PlanParameter planParameter) {
        PlanParameterEntity p = new PlanParameterEntity();
        p.setPlanId(planParameter.getPlanId());
        planParameterRepository.save(p);
    }
}
