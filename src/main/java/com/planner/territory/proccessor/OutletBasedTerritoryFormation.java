package com.planner.territory.proccessor;

import java.util.*;

import org.springframework.stereotype.Component;


import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;

/****
 * 
 * The OutletBasedTerritoryFormation Implements outletBasedTerritoriesAllocation
 *
 */
@Component
public class OutletBasedTerritoryFormation {
	
	/***
	 * used to create territories using Outlet Based based constraints
	 *
	 * @param kmeanClusterDetails
	 * @param threshold
	 * @param distributionFlag
	 * @return
	 */
	public Map<Integer, List<ClusterDetails>> outletBasedTerritoriesAllocation(
			List<ClusterDetails> kmeanClusterDetails, double threshold, PlanParameter plnParameter,
			boolean distributionFlag) {

		double currVal = 0d;
		double thresholdNew = threshold;
		int indexId = 0;
		List<ClusterDetails> kList = new ArrayList<>();
		Map<Integer, List<ClusterDetails>> finalResultMap = new HashMap<>();
		ClusterDetails temp;

		double curvalsum = 0;
		int visitSeq = 1;
		
		double halfDays = plnParameter.getHalfDays();

		if (kmeanClusterDetails != null && !kmeanClusterDetails.isEmpty()) {
			Vector<Outlet> dataPoints = new Vector<>();
			ClusterDetails gsOutput = new ClusterDetails();

			indexId = 0;
			boolean addInCurrentBeat = false;
			for (int i = 0; i < kmeanClusterDetails.size(); i++) {
				temp = kmeanClusterDetails.get(i);
				double tempSize = temp.getDataPoints().size();
				for (int j = 0; j < tempSize; j++) {
					Outlet currentOutlet = temp.getDataPoints().get(j);
					currentOutlet.setVisitSequence(visitSeq++);
					double multipler = distributionFlag == true
							? currentOutlet.getCallFrequencyMultiplier()
							: currentOutlet.getMultiplier();
					if ((currVal + multipler) > thresholdNew) {
						if (addInCurrentBeat) {
							dataPoints.add(currentOutlet);
							currVal += multipler;
							gsOutput.setDataPoints(dataPoints);
							curvalsum += currVal;
							System.out.println("Datapoints-" + dataPoints.size() + " Curval " + currVal + " Thres "
									+ thresholdNew);
							dataPoints = new Vector<>();
							currVal = 0d;
							addInCurrentBeat = false;
						} else {
							gsOutput.setDataPoints(dataPoints);
							curvalsum += currVal;
							System.out.println("Datapoints-" + dataPoints.size() + " Curval " + currVal + " Thres "
									+ thresholdNew);
							dataPoints = new Vector<>();
							dataPoints.add(currentOutlet);
							currVal = multipler;
							addInCurrentBeat = true;
						}

						kList.add(gsOutput);
						finalResultMap.put(indexId, kList);
						indexId++;
						kList = new ArrayList<ClusterDetails>();
						gsOutput = new ClusterDetails();
						
						if(plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
							thresholdNew = Math.round(((threshold * halfDays) - curvalsum)
									/ (halfDays - indexId) * 1000) / 1000.0;
						}else {
							thresholdNew = Math.round(((threshold * plnParameter.getNoOfSalesperson()) - curvalsum)
									/ (plnParameter.getNoOfSalesperson() - indexId) * 1000) / 1000.0;
						}
						
						if(thresholdNew <= 0) {
							thresholdNew = threshold;
						}

					} else {
						dataPoints.add(currentOutlet);
						currVal = Math.round((currVal + multipler) * 1000) / 1000.0;

					}
				}

			}
			if (dataPoints.size() > 0 && indexId == plnParameter.getNoOfSalesperson()) {
				Vector<Outlet> dataPointNew = new Vector<>();
				gsOutput = finalResultMap.get(indexId - 1).get(0);
				dataPointNew = gsOutput.getDataPoints();
				for (int dp = 0; dp < dataPoints.size(); dp++) {
					dataPointNew.add(dataPoints.get(dp));
				}
				gsOutput.setDataPoints(dataPointNew);
				kList.add(gsOutput);
				finalResultMap.put(indexId - 1, kList);
				indexId++;
				kList = new ArrayList<ClusterDetails>();
				System.out.println("Datapoints-" + dataPoints.size());
				dataPoints = new Vector<>();
				gsOutput = new ClusterDetails();
			} else if(dataPoints.size() > 0) {
				gsOutput.setDataPoints(dataPoints);
				kList.add(gsOutput);
				finalResultMap.put(indexId, kList);
				kList = new ArrayList<ClusterDetails>();
				System.out.println("Datapoints-" + dataPoints.size());
				dataPoints = new Vector<>();
				gsOutput = new ClusterDetails();
			}

		}
		return finalResultMap;
	}
	
}
