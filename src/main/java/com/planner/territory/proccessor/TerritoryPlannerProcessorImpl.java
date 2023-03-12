package com.planner.territory.proccessor;

import com.planner.territory.model.globalsequence.Customer;
import com.planner.territory.model.globalsequence.GlobalSequenceInputDataModel;
import com.planner.territory.model.globalsequence.GlobalSequenceOutputDataModel;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import com.planner.territory.model.pjp.Territory;
import com.planner.territory.model.pjp.data.TerritoryPlannerInputDataModel;
import com.planner.territory.model.pjp.data.TerritoryPlannerOutputDataModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TerritoryPlannerProcessorImpl implements TerritoryPlannerProcessor  {

    @Autowired
    TerritoryFormation territoryFormation;

    @Autowired
    GlobalSequenceProcessorCaller globalSequenceProcessorCaller;

    @Autowired
    GlobalSequenceProcessorImpl globalSequenceProcessor;

    @Override
    public TerritoryPlannerOutputDataModel process(TerritoryPlannerInputDataModel model)
            throws Exception {

        log.info("------------------------------------Start BeatPlanner Plan Run ------------------------------------");

        PlanParameter plnParameter = new PlanParameter();
        Distributor distributor = model.getDistLst().get(0);
        TerritoryPlannerOutputDataModel outputModel = new TerritoryPlannerOutputDataModel();

        int noOfSalesPersons = 0;
        int workingDays = 0;
        double distanceFactor = 1;
        int distanceToCentroid = 0;

        List<Distributor> distLst = model.getDistLst();
        List<PlanParameter> plnParameterLst = model.getPlanParameterLst();
        List<Outlet> outletList = model.getOutletsLst();
        model.setAllDistance(new HashMap<>());


        if (distLst != null) {

            if (plnParameterLst != null) {
                plnParameter = model.getPlanParameterLst().get(0);

                /***
                 * validation for working days 4 , 5 and half days
                 */
                Set<Integer> uniqueFrequency = new HashSet<Integer>();
                outletList.stream().filter(p -> uniqueFrequency.add(p.getVisitFrequency())).collect(Collectors.toList());

                if (plnParameter.getWorkDayInWeek() == 4 && uniqueFrequency.contains(12)) {
                    throw new Exception(
                            "Location frequency must be 1,2,4,8,16 for 4 working days in week");
                } else if (plnParameter.getWorkDayInWeek() == 5
                        && uniqueFrequency.contains(12)) {
                    throw new Exception("Location frequency must be 1,2,4,8,16,20 for 5 working days in week");
                }

                if(plnParameter.getHalfDayFlag() && (plnParameter.getWorkDayInWeek() == 5 || plnParameter.getWorkDayInWeek() == 4)) {
                    throw new Exception("Please run half day plan for only 5 and half working days.");
                }

                if(plnParameter.getPlanMode().equalsIgnoreCase("Value") && plnParameter.getTelecallingPlanFlag() == 1) {
                    throw new Exception("Please run plan with Plan_Mode : Outlet|Time while Tellecalling flag is Yes|1|true");
                }

                if(plnParameter.getDistanceType() == 4){
                    /**
                     * Apply Global sequence on all outlets
                     */

                    Customer startPoint = new Customer();
                    startPoint.setLatitude(distributor.getLatitude());
                    startPoint.setLongitude(distributor.getLongitude());
                    //set id of distributor as -1 so that it does not clash with customer id
                    startPoint.setCustomerId(-1);

                    GlobalSequenceInputDataModel globalSequenceFinderModel = globalSequenceProcessorCaller.createGlobalSequenceInputModel(
                            new Vector<>(model.getOutletsLst()), plnParameter.getDistanceType(), new HashMap<>());
                    globalSequenceFinderModel.setStartPoint(startPoint);

                    GlobalSequenceOutputDataModel globalSequenceOutputModel = globalSequenceProcessor
                            .process(globalSequenceFinderModel);

                    plnParameter.setDistanceType(2);
                    model.setAllDistance(globalSequenceOutputModel.getAllDistance());
                }

                workingDays = plnParameter.getWorkDayInWeek();

                plnParameter.setDistanceToCentroid(distanceToCentroid);
                plnParameter.setDistanceFactor(distanceFactor);

                if (workingDays > 8 || workingDays < 1) {
                    workingDays = 6;
                }

                if (plnParameter.getPlanType().equalsIgnoreCase("Territory Creation")) {

                    plnParameter.setMinClusterSize(250);

                    /***
                     * territory formation logic entry point
                     */
                    List<Territory> territoryList = territoryFormation.formTerritory(model, outletList, distLst, noOfSalesPersons,
                            plnParameter, false, outputModel);


                    outputModel.setTerritoryLst(territoryList);

                    return outputModel;

                }

            } else {
                throw new Exception("Please provide Plan Parameter Data....");
            }
        } else {
            throw new Exception("Please provide Distributor Data....");
        }
        log.info("------------------------------------End BeatPlanner Plan Run------------------------------------");
        return outputModel;
    }


}
