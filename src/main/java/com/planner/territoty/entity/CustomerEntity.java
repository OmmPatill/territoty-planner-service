package com.planner.territoty.entity;

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

    @Column(name = "Lattitude", nullable = false)
    private Double lattitude;

    @Column(name = "Longitude", nullable = false)
    private Double longitude;
}
