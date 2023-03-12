package com.planner.territory.model.pjp.data;

import com.planner.territory.model.pjp.Territory;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TerritoryPlannerOutputDataModel {
    List<Territory> territoryLst;
}
