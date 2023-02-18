package com.planner.territoty.repository;

import com.planner.territoty.entity.DistanceMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Long> {
}
