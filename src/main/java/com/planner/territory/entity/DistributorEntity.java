package com.planner.territory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Distributor")
@Getter
@Setter
public class DistributorEntity {
        @Id
        @GeneratedValue(generator = "idSequence")
        @SequenceGenerator(name = "idSequence", allocationSize = 1)
        @Column(name = "Distributor_Id")
        private Long distributorId;

        @Column(name = "Distributor_Code")
        private String distributorCode;
        @Column(name = "Latitude")
        private double latitude;

        @Column(name = "Longitude")
        private double longitude;

        @Column(name = "Speed")
        private int travellingSpeed;
}
