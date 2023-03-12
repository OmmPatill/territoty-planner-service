package com.planner.territory.model.pjp.data;

import com.planner.territory.model.pjp.DistanceMatrix;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TerritoryPlannerInputDataModel {
        private List<Outlet> outletsLst;
        private List<Distributor> distLst;
        private List<PlanParameter> planParameterLst;
        private List<DistanceMatrix> distanceMatrixLst;
        private Map<String,Double> allDistance;
}
