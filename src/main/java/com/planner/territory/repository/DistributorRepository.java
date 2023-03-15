package com.planner.territory.repository;

import com.planner.territory.entity.DistributorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributorRepository extends JpaRepository<DistributorEntity, Integer> {
}
