package com.planner.territory.proccessor;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;

/****
 * 
 * The ValueBasedTerritoryFormation Implements valueBasedTerritoriesAllocation
 *
 */

@Component
public class ValueBasedTerritoryFormation {
	
	@Autowired
	TerritoryFormation territoryFormation;
	
	/****
	 * used to create territories using Value based constraints
	 *
	 * @param kmeanClusterDetails
	 * @param threshold
	 * @param totalValue
	 * @param noOfCluster
	 * @param workDayInWeek
	 * @return
	 */
	public Map<Integer, List<ClusterDetails>> valueBasedTerritoriesAllocation(
			List<ClusterDetails> kmeanClusterDetails, double threshold, double totalValue, int noOfCluster,
			int workDayInWeek, PlanParameter plnParameter) {
		
		
		int size = 0;
		for (ClusterDetails c : kmeanClusterDetails) {
			size += c.getDataPoints().size();
		}

		double avgValue = totalValue / size;
		
		if (plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {

			totalValue = totalValue - (avgValue * plnParameter.getHalfDays());
			threshold = totalValue/plnParameter.getHalfDays();

		} else {

			totalValue = totalValue - (avgValue * noOfCluster);
			threshold = territoryFormation.calTerritoryThreshold(noOfCluster, totalValue);
		}
		
		Double currVal = 0d;
		double thresholdNew = threshold;
		int indexId = 0;
		List<ClusterDetails> kList = new ArrayList<>();
		Map<Integer, List<ClusterDetails>> finalResultMap = new HashMap<>();
		ClusterDetails temp;
		boolean isLast = false;
		
		double curvalsum = 0;

		if (kmeanClusterDetails != null && !kmeanClusterDetails.isEmpty()) {
			
			Vector<Outlet> dataPoints = new Vector<>();
			ClusterDetails gsOutput = new ClusterDetails();
			
			Outlet currentOutlet = null;
			
			for (int i = 0; i < kmeanClusterDetails.size(); i++) {
				
				temp = kmeanClusterDetails.get(i);
				double tempSize = temp.getDataPoints().size();
				
				for (int j = 0; j < tempSize; j++) {
					
					currentOutlet = temp.getDataPoints().get(j);
					double turnOverValue = (currentOutlet.getAvgTurnover() * currentOutlet.getMultiplier());
					
					if (currVal > thresholdNew) {
						
						boolean addInBeat = true;
						if (territoryFormation.isGapIsShortWithNext(currVal + turnOverValue,
								turnOverValue, thresholdNew)) {
							addInBeat = false;
							dataPoints.add(currentOutlet);
							currVal += turnOverValue;
						}
						
						gsOutput.setDataPoints(dataPoints);
						kList.add(gsOutput);
						finalResultMap.put(indexId, kList);
						indexId++;
						System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalValue\t" + totalValue + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
						kList = new ArrayList<ClusterDetails>();
						dataPoints = new Vector<>();
						gsOutput = new ClusterDetails();
						curvalsum += currVal;
						
						if (plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday")
								&& plnParameter.getHalfDayFlag()) {
							thresholdNew = Math.round(((threshold * plnParameter.getHalfDays()) - curvalsum)
									/ (plnParameter.getHalfDays() - indexId) * 1000) / 1000.0;
						} else {
							thresholdNew = Math.round((totalValue - curvalsum)
									/ (Math.round((totalValue - curvalsum) / threshold)) * 1000) / 1000.0;
						}
						
						thresholdNew = thresholdNew <=0?threshold:thresholdNew;
						currVal = 0d;
						
						if (addInBeat){
							dataPoints.add(currentOutlet);
							currVal = turnOverValue;
						}
						
					}else {
						dataPoints.add(currentOutlet);
						currVal += turnOverValue;
					}
				}
			}
			if (dataPoints.size() > 0 && indexId==plnParameter.getNoOfSalesperson()) {
				Vector<Outlet> dataPointNew = new Vector<>();
				gsOutput = finalResultMap.get(indexId-1).get(0);
				dataPointNew = gsOutput.getDataPoints();
				for(int dp=0; dp<dataPoints.size();dp++){
					dataPointNew.add(dataPoints.get(dp));
				}
				gsOutput.setDataPoints(dataPointNew);
				kList.add(gsOutput);
				finalResultMap.put(indexId-1, kList);
				indexId++;
				kList = new ArrayList<ClusterDetails>();
				System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalValue\t" + totalValue + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
				dataPoints = new Vector<>();
				gsOutput = new ClusterDetails();
			}
			else if (dataPoints.size() > 0 ){
				gsOutput.setDataPoints(dataPoints);
				kList.add(gsOutput);
				finalResultMap.put(indexId, kList);
				indexId++;
				plnParameter.setNoOfSalesperson(indexId);
				kList = new ArrayList<ClusterDetails>();
				System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalVime\t" + totalValue + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
				dataPoints = new Vector<>();
				gsOutput = new ClusterDetails();
			}
		}	
		
		int count = 0;
		for(Map.Entry<Integer, List<ClusterDetails>> f : finalResultMap.entrySet()) {
			for(ClusterDetails c : f.getValue()) {
				count += c.getDataPoints().size();
			}
			
		}
		return finalResultMap;
	}
}
