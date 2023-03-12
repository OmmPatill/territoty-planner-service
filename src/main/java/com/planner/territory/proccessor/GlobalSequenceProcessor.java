package com.planner.territory.proccessor;

import com.planner.territory.model.globalsequence.GlobalSequenceInputDataModel;
import com.planner.territory.model.globalsequence.GlobalSequenceOutputDataModel;
import org.springframework.stereotype.Service;

@Service
public interface GlobalSequenceProcessor {
    GlobalSequenceOutputDataModel process(GlobalSequenceInputDataModel globalSequenceFinderModel) throws Exception;
}
