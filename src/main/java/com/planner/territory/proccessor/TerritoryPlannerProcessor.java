package com.planner.territory.proccessor;

import com.planner.territory.model.pjp.data.TerritoryPlannerInputDataModel;
import com.planner.territory.model.pjp.data.TerritoryPlannerOutputDataModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TerritoryPlannerProcessor {
    public TerritoryPlannerOutputDataModel process(TerritoryPlannerInputDataModel model) throws Exception;
}
