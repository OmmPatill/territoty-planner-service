package com.planner.territory.repository;

import com.planner.territory.entity.DistanceMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Long> {
}
