package com.planner.territory.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Territory_Plan_Parameter")
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

        @Column(name = "Use_Delivery_Slot")
        private Boolean useDeliverySlot;

        @Column(name = "Reverse_Seq_Flag")
        private Boolean reverseSeqFlag;

        @Column(name = "Beat_Name_Flag")
        private Boolean beatNameFlag;

        @Column(name = "Mon_Thu_Flag")
        private Boolean monThuFlag;

        @Column(name = "Traffic_Time_Flag")
        private Boolean trafficTimeFlag;

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

        @Column(name = "Telecalling_Plan_Flag")
        private Boolean telecallingPlanFlag;

        @Column(name = "Telecalling_Plan_Mode")
        private String telecallingPlanMode;

        @Column(name = "Per_Beat_Call_Time")
        private Integer perBeatCallTime;

        @Column(name = "Telecalling_Count")
        private Integer telecallingCount;

        @Column(name = "Beat_Type")
        private String beatType;

        // TODO foreign key from planDetails table remeber from screen when plan created
        // first entry done in plandetails then planparameters table
        @Column(name = "Plan_Id", nullable = false)
        private Integer planId;

        @Column(name = "No_Of_Calls_Per_Beat")
        private Integer noOfCallsPerBeat;

        @Column(name = "Multiplier")
        private Integer multiplier;

        @Column(name = "Half_Day_Flag")
        private Boolean halfDayFlag;

        @Column(name = "Round_Trip_Time")
        private Integer roundTripTime;

    }
