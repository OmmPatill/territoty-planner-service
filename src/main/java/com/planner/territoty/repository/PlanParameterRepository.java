package com.planner.territoty.repository;

import com.planner.territoty.entity.PlanParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanParameterRepository extends JpaRepository<PlanParameterEntity, Long> {
}
