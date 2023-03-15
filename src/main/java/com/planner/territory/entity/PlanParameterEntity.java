package com.planner.territory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "PlanParameter")
@Getter
@Setter
public class PlanParameterEntity {
        @Id
        @GeneratedValue(generator = "idSequence")
        @SequenceGenerator(name = "idSequence", allocationSize = 1)
        private Long planParametersId;

        @Column(name = "Plan_Mode")
        private String planMode;

        @Column(name = "Beat_Group_Mode")
        private String beatGroupMode;

        @Column(name = "No_Of_Salesperson")
        private Integer noOfSalesperson;

        @Column(name = "No_Of_Outlet_Per_Beat")
        private Integer noOfOutletPerBeat;

        @Column(name = "Min_Time")
        private Integer minTime;

        @Column(name = "Max_Time")
        private Integer maxTime;

        @Column(name = "Value_Per_Beat")
        private Integer valuePerBeat;

        @Column(name = "Territory_Loop")
        private Integer territoryLoop;

        @Column(name = "Cluster_Loop")
        private Integer clusterLoop;

        @Column(name = "Distance_Type")
        private Integer distanceType;

        @Column(name = "Work_Day_In_Week")
        private Integer workDayInWeek;

        @Column(name = "Plan_For_Week")
        private Integer planForWeek;

        @Column(name = "Beat_Type")
        private String beatType;

        @Column(name = "Plan_Id", nullable = false)
        private Integer planId;

        @Column(name = "Multiplier")
        private Integer multiplier;

        @Column(name = "Round_Trip_Time")
        private Integer roundTripTime;

    }
