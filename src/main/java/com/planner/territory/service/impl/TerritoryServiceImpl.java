package com.planner.territory.service.impl;

import com.planner.territory.entity.CustomerEntity;
import com.planner.territory.entity.DistanceMatrixEntity;
import com.planner.territory.entity.DistributorEntity;
import com.planner.territory.entity.PlanParameterEntity;
import com.planner.territory.model.pjp.DistanceMatrix;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import com.planner.territory.model.pjp.data.TerritoryPlannerInputDataModel;
import com.planner.territory.proccessor.TerritoryPlannerProcessorImpl;
import com.planner.territory.repository.CustomerRepository;
import com.planner.territory.repository.DistanceMatrixRepository;
import com.planner.territory.repository.DistributorRepository;
import com.planner.territory.repository.PlanParameterRepository;
import com.planner.territory.service.TerritoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TerritoryServiceImpl implements TerritoryService {

    @Autowired
    PlanParameterRepository planParameterRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    DistributorRepository distributorRepository;

    @Autowired
    DistanceMatrixRepository distanceMatrixRepository;


    @Autowired
    TerritoryPlannerProcessorImpl territoryPlannerProcessor;

    @Override
    public void createPlan(PlanParameter planParameter) {
        PlanParameterEntity p = new PlanParameterEntity();
        p.setPlanId(planParameter.getPlanId());
        planParameterRepository.save(p);
    }

    @Override
    public void createPlan(Integer planId) throws Exception {
        PlanParameterEntity planParameterEntity = planParameterRepository.findByPlanId(planId);

        TerritoryPlannerInputDataModel model = new TerritoryPlannerInputDataModel();

        model.setOutletsLst(this.getOutletList());
        model.setDistLst(this.getDistributor());
        model.setPlanParameterLst(this.getPlanParameters());

        model.setDistanceMatrixLst(null);
        model.setAllDistance(null);
        model.setDistanceMatrixLst(null);

        territoryPlannerProcessor.process(model);
        //planParameterRepository.save(p);
    }

    List<Outlet> getOutletList() throws Exception {
        List<CustomerEntity> outletLst = customerRepository.findAll();

        if(outletLst != null){
            return  outletLst.stream().map(o->Outlet.builder().
                    outletID(Math.toIntExact(o.getCustomerId())).
                    outletName(o.getCustomerName())
                    .outletCode(o.getCustomerCode())
                    .latitude(o.getLatitude())
                    .longitude(o.getLongitude())
                    .visitFrequency(o.getVisitFrequency())
                    .visitDuration(o.getVisitDuration())
                    .distributorCode(o.getDistributorCode())
                    .build()
            ).collect(Collectors.toList())  ;
        }else{
            throw new Exception("Please provide outlet data...");
        }
    }

    List<Distributor> getDistributor() throws Exception {
        List<DistributorEntity> distributorList = distributorRepository.findAll();

        if(distributorList != null){
            return distributorList.stream().map(
                o->Distributor.builder().distributorId(Math.toIntExact(o.getDistributorId()))
                        .distributorCode(o.getDistributorCode())
                        .latitude(o.getLatitude())
                        .longitude(o.getLongitude())
                        .travellingSpeed(o.getTravellingSpeed()).build()
            ).collect(Collectors.toList());
        }else{
            throw new Exception("Please provide distributor data...");
        }
    }

    List<DistanceMatrix> getDistanceMatrix() throws Exception {
        List<DistanceMatrixEntity> distanceMatrixList = distanceMatrixRepository.findAll();

        if(distanceMatrixList != null){
            return distanceMatrixList.stream().map(
                    o->DistanceMatrix.builder().distanceMatrixId(Math.toIntExact(o.getDistanceMatrixId()))
                            .fromLocationCode(o.getFromConsumerCode())
                            .toLocationCode(o.getToConsumerCode())
                            .distributorCode(o.getDistributorCode())
                            .distance(o.getDistance() == null ? 0 : o.getDistance()).build())
                    .collect(Collectors.toList());
        }else {
            throw new Exception("Please provide distance matrix data for liner type distance...");
        }
    }

    List<PlanParameter> getPlanParameters() throws Exception {
        List<PlanParameterEntity> planParameterList = planParameterRepository.findAll();

        if(planParameterList != null){
            return planParameterList.stream().filter(o->o.getPlanId() == 1).map(o1->
                    PlanParameter.builder().
                            planMode(o1.getPlanMode())
                            .beatGroupMode(o1.getBeatGroupMode())
                            .planType(o1.getBeatType())
                            .planForWeek(o1.getPlanForWeek())
                            .workDayInWeek(o1.getWorkDayInWeek())
                            .noOfSalesperson(o1.getNoOfSalesperson())
                            .valuePerBeat(o1.getValuePerBeat())
                            .noOfBeat(o1.getNoOfOutletPerBeat())
                            .clusterLoop(o1.getClusterLoop())
                            .territoryLoop(o1.getTerritoryLoop())
                            .minTime(o1.getMinTime())
                            .maxTime(o1.getMaxTime())
                            .distanceType(o1.getDistanceType()).build()
            ).collect(Collectors.toList());
        }else{
            throw new Exception("Please provide plan parameters data.......");
        }
    }
}
