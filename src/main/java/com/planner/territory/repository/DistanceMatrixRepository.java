package com.planner.territory.repository;


import com.planner.territory.entity.DistanceMatrixEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrixEntity, Long> {
}
