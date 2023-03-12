package com.planner.territory.proccessor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.planner.territory.model.globalsequence.Customer;
import com.planner.territory.model.globalsequence.GlobalSequenceInputDataModel;
import com.planner.territory.model.globalsequence.GlobalSequenceOutputDataModel;
import com.planner.territory.model.globalsequence.GlobalVisitSequence;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/****
 * 
 * The GlobalSequenceProcessorCaller implements GlobalSequenceProcessor calling method
 *
 */
@Component
public class GlobalSequenceProcessorCaller {

	@Autowired
	GlobalSequenceProcessor globalSequenceProcessor;

	/**
	 * Map outlets by id
	 *
	 * @param outletsLst list of Outlet objects
	 * @return Map with outletId as key and outlet as value
	 */
	public Map<Integer, Outlet> toOutletMap(List<Outlet> outletsLst) {
		if (outletsLst == null || outletsLst.isEmpty()) {
			return new HashMap<>();
		}
		Map<Integer, Outlet> outletMapById = outletsLst.stream()
				.collect(Collectors.toMap(Outlet::getOutletID, Function.identity()));
		return outletMapById;
	}

	public List<List<Outlet>> applyGlobalSequence(List<List<Outlet>> beats,
												  PlanParameter planParameter, Map<String,Double> allDistance, Distributor distributor) throws Exception {
		/**
		 * get the list of all outlets and map by
		 */
		List<Outlet> completeOutletList = new ArrayList<>();
		beats.forEach(beat -> {
			completeOutletList.addAll(beat);
		});
		Map<Integer, Outlet> outletMapById = toOutletMap(completeOutletList);

		List<List<Outlet>> globallySequencedCustomers = new ArrayList<>();
		for(int i = 0 ; i < beats.size(); i++){
			List<Outlet> beat = beats.get(i);
			if (beat.isEmpty() || beat == null) {
				globallySequencedCustomers.add(beat);
			}else {
				GlobalSequenceInputDataModel GlobalSequenceInputDataModel = createGlobalSequenceInputModel(new Vector<>(beat),
						planParameter.getDistanceType(), allDistance);

				Customer startPoint = new Customer();
				startPoint.setCustomerId(-1);
				startPoint.setLongitude(distributor.getLongitude());
				startPoint.setLatitude(distributor.getLatitude());
				GlobalSequenceInputDataModel.setStartPoint(startPoint);

				GlobalSequenceOutputDataModel globalSeqOutputModel = globalSequenceProcessor
						.process(GlobalSequenceInputDataModel);
				List<GlobalVisitSequence> m = globalSeqOutputModel.getVisitSequence();

				/**
				 * Map GlobalVisitSequence to Outlet
				 */
				List<Outlet> currentList = new ArrayList<>();
				m.stream().forEach(o -> {
					outletMapById.get(o.getCustomerId()).setVisitSequence(o.getVisitSequence());
					currentList.add(outletMapById.get(o.getCustomerId()));
				});
				globallySequencedCustomers.add(currentList);
			}
		}
		return globallySequencedCustomers;
	}
	
	/**
	 * used to create GlobalSequenceInputModel
	 *
	 * @param dataPointsNew
	 * @param distanceType
	 * @param distanceMtarixMap
	 * @return
	 */
	public GlobalSequenceInputDataModel createGlobalSequenceInputModel(Vector<Outlet> dataPointsNew, int distanceType,
																	   Map<String,Double> distanceMtarixMap) {

		List<Customer> locationsList = dataPointsNew.stream()
				.map(loc -> new Customer(loc.getOutletID(), loc.getOutletCode(), loc.getLatitude(), loc.getLongitude()))
				.collect(Collectors.toList());

		GlobalSequenceInputDataModel model = new GlobalSequenceInputDataModel();
		model.setCustomerList(locationsList);
		model.setAllDistance(distanceMtarixMap);
		model.setDistanceType(distanceType);

		return model;
	}

}
