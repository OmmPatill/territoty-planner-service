package com.planner.territory.repository;

import com.planner.territory.entity.PlanDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDetailsRepository extends JpaRepository<PlanDetailsEntity, Integer> {
}
