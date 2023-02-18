package com.planner.territoty.entity;




import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "TerritoryDistanceMatrix")
@Getter
@Setter
public class DistanceMatrix{
    @Id
    @GeneratedValue(generator = "idSequence")
    @SequenceGenerator(name = "idSequence", allocationSize = 1)
    @Column(name = "Distance_Matrix_Id")
    private Long distanceMatrixId;

    @Column(name = "From_Consumer_Code", nullable = false)
    private String fromConsumerCode;

    @Column(name = "To_Consumer_Code", nullable = false)
    private String toConsumerCode;

    @Column(name = "Distance", nullable = false)
    private Double distance;

    @Column(name = "Distributor_Code")
    private String distributorCode;

}
