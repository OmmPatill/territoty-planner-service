package com.planner.territory.model.pjp;

import lombok.*;

import java.util.Vector;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Territory{
    int territoryId;
    private Vector<Outlet> outletPoints;
    private Double noOfDataPoint;
    private Double totalTime;
    private Double totalvalue;

    public Territory(int territoryId, Vector<Outlet> outletPoints) {
        super();
        this.territoryId = territoryId;
        this.outletPoints = outletPoints;
        calculateTotalOutlet(outletPoints);
        calculateTime(outletPoints);
        calculateValues(outletPoints);
    }

    private void calculateTime(Vector<Outlet> outletPoints) {
        this.totalTime = outletPoints.stream().collect(Collectors.summingDouble(Outlet::getVisitDuration));

    }

    private void calculateValues(Vector<Outlet> outletPoints) {
        this.totalvalue = outletPoints.stream().collect(Collectors.summingDouble(Outlet::getAvgTurnover));
    }

    private void calculateTotalOutlet(Vector<Outlet> outletPoints) {
        this.noOfDataPoint = outletPoints.stream().collect(Collectors.summingDouble(Outlet::getMultiplier));
    }

    @Override
    public String toString() {
        return territoryId + "," + outletPoints + ","
                + noOfDataPoint + "," + totalTime + "," + totalvalue + "\n";
    }



}
