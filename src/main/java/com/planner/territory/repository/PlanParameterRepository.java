package com.planner.territory.repository;

import com.planner.territory.entity.PlanParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanParameterRepository extends JpaRepository<PlanParameterEntity, Long> {

    @Query("select p from PlanParameterEntity p where p.planId  = :planId")
    PlanParameterEntity findByPlanId(@Param("planId") Integer planId);
}
