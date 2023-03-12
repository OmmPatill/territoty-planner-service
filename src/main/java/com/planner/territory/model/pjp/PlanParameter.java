package com.planner.territory.model.pjp;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlanParameter {

    private String planMode;
    private String beatGroupMode;
    private int noOfSalesperson;
    private int noOfOutletPerBeat;
    private int minTime;
    private int maxTime;
    private int valuePerBeat;
    private int useDeliverySlot;
    private int reverseSeqFlag;
    private int beatNameFlag;
    private int monThuFlag;
    private int trafficTimeFlag;
    private int territoryLoop;
    private int clusterLoop;
    private int distanceType;
    private int workDayInWeek;
    private int planForWeek;
    private int maxFreqency;
    private int noOfBeat;
    private double distanceFactor;
    private int distanceToCentroid;
    private Boolean halfDayFlag;
    private int planId;
    private int distributorId;
    private int noOfCallsPerBeat;

    private int telecallingPlanFlag;
    private String telecallingPlanMode;
    private int perBeatCallTime;
    private int telecallingCount;
    private String planType;
    private int minClusterSize;
    private int multiplier;
    private double halfDays;
    private double sp;
    private int mainMaxFrequency;
    private int roundTripTime;

}
