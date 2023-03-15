package com.planner.territory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(name="PlanDetails")
@Entity
@Getter
@Setter
public class PlanDetailsEntity {

    @Id
    @GeneratedValue(generator = "idSequence")
    @SequenceGenerator(name = "idSequence", allocationSize = 1)
    @Column(name="PlanId")
    private Integer planId;

    @Column(name="PlanCode")
    private String planCode;

    @Column(name="PlanName")

    private String planName;
}
