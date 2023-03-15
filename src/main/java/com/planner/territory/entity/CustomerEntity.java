package com.planner.territory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="Outlet")
@Getter
@Setter
public class CustomerEntity {
    @Id
    @GeneratedValue(generator = "idSequence")
    @SequenceGenerator(name = "idSequence", allocationSize = 1)
    @Column(name = "Customer_Id")
    private Long customerId;

    @Column(name = "Customer_Name", nullable = false)
    private String customerName;

    @Column(name = "Customer_Code", nullable = false)
    private String customerCode;

    @Column(name = "Latitude", nullable = false)
    private Double latitude;

    @Column(name = "Longitude", nullable = false)
    private Double longitude;

    @Column(name = "Visit_Frequency")
    private int visitFrequency;

    @Column(name = "Visit_Duration")
    private int visitDuration;

    @Column(name = "Avg_Turnover")
    private double avgTurnover;

    @Column(name="Outlet_Type")
    private String outletType;

    @Column(name="Distributor_Code")
    private String distributorCode;

    @Column(name="Multiplier")
    private double multiplier;

    @Column(name="VisitSequence")
    private int visitSequence;

    @Column(name="Distance")
    private double distance;

    @Column(name="TravelingDistance")
    private double travelingDistance;

    @Column(name="TravelingTime")
    private double travelingTime;
}
