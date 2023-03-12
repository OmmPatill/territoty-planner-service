package com.planner.territory.service.impl;

import com.planner.territory.entity.PlanParameterEntity;
import com.planner.territory.model.pjp.PlanParameter;
import com.planner.territory.repository.PlanParameterRepository;
import com.planner.territory.service.TerritoryService;
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

    @Override
    public void createPlan(Long planId) {
        PlanParameterEntity planParameterEntity = planParameterRepository.findByPlanId(planId);

        //planParameterRepository.save(p);
    }
}
