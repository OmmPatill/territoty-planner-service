package com.planner.territory.repository;

import com.planner.territory.entity.PlanParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanParameterRepository extends JpaRepository<PlanParameterEntity, Long> {

    @Query("select p from PlanParameterEntity where planId in ? 1")
    PlanParameterEntity findByPlanId(Long planId);
}
