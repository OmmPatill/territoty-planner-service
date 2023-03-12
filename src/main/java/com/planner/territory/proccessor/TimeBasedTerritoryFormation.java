package com.planner.territory.proccessor;

import java.util.*;

import java.util.stream.Collectors;


import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/****
 * 
 * The TimeBasedTerritoryFormation Implements timeBasedTerritoriesAllocation
 *
 */

@Component
public class TimeBasedTerritoryFormation {
	
	@Autowired
	TerritoryFormation territoryFormation;
	
	/**
	 * used to create territories using Time based constraints
	 * @param kmeanClusterDetails
	 * @param threshold
	 * @param totalTime
	 * @param distributorAvgSpeed
	 * @param plnParameter
	 * @param distributionFlag
	 * @param allDistance
	 * @param distributor 
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, List<ClusterDetails>> timeBasedTerritoriesAllocation(
			List<ClusterDetails> kmeanClusterDetails, double threshold, double totalTime, int distributorAvgSpeed,
			PlanParameter plnParameter, boolean distributionFlag, Map<String,Double> allDistance, Distributor distributor)
			throws Exception {

		Double currVal = 0d;
		double thresholdNew = threshold;
		int indexId = 0;
		List<ClusterDetails> kList = new ArrayList<>();
		Map<Integer, List<ClusterDetails>> finalResultMap = new HashMap<>();
		ClusterDetails temp;
		double curvalsum = 0;

		if (kmeanClusterDetails != null && !kmeanClusterDetails.isEmpty()) {
			Vector<Outlet> dataPoints = new Vector<>();
			ClusterDetails gsOutput = new ClusterDetails();

			Outlet prevOutlet = plnParameter.getRoundTripTime() == 1 ? new Outlet(distributor.getLatitude(),distributor.getLongitude()) : null;
			Outlet currentOutlet = null;

			totalTime=0;
			double avgTime = 0.0;
			prevOutlet = null;
			if (distributionFlag){
				for (int i = 0; i < kmeanClusterDetails.size(); i++) {
					temp = kmeanClusterDetails.get(i);
					double tempSize = temp.getDataPoints().size();
					for (int j = 0; j < tempSize; j++) {
	
						currentOutlet = temp.getDataPoints().get(j);
						double time = currentOutlet.getCallDuration();
						totalTime += time*currentOutlet.getCallFrequencyMultiplier();
					}
				}
			}else{
				for (int i = 0; i < kmeanClusterDetails.size(); i++) {
					temp = kmeanClusterDetails.get(i);
					double tempSize = temp.getDataPoints().size();
					for (int j = 0; j < tempSize; j++) {
	
						currentOutlet = temp.getDataPoints().get(j);
						double distance = 0;
						double vTime = currentOutlet.getVisitDuration();
						double time = 0.0;
						if (prevOutlet != null) {
							if(plnParameter.getDistanceType() == 2){
								//use OSRM distance
								distance = territoryFormation.distanceBetweenTwoOutlets(prevOutlet, currentOutlet, allDistance);
							}
							else{
								distance = territoryFormation.calculateLinearDistance(prevOutlet, currentOutlet);
							}
							time = (distance / (distributorAvgSpeed * 16.667));
							avgTime += time;
							//time = vTime + time;
							indexId++;
						}
						time = vTime + time;
						totalTime += time*currentOutlet.getMultiplier();
						prevOutlet=currentOutlet;
						System.out.println("currentOutlet: " + currentOutlet.getOutletID() +",distance: "+distance + ",Time:" + time + ",TotalTime: "+totalTime);
					}
				}
				if (plnParameter.getRoundTripTime() == 1) {
					double distance = 0.0;
					Outlet dist = new Outlet(distributor.getLatitude(), distributor.getLongitude());
					if(plnParameter.getDistanceType() == 2){
						//use OSRM distance
						 distance = territoryFormation.distanceBetweenTwoOutlets(currentOutlet, dist, allDistance);
					}
					else{
						 distance = territoryFormation.calculateLinearDistance(currentOutlet, dist);
					}
					double time= (distance / (distributorAvgSpeed * 16.667));
					totalTime +=time;
					avgTime += time;
					//time = vTime + time;
					indexId++;
				}
				avgTime = avgTime/indexId;
			}
			
			if (plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
				
				plnParameter.setNoOfSalesperson(territoryFormation.identifySalesPerson(plnParameter, null, 0, totalTime,
						plnParameter.getNoOfSalesperson(), distributionFlag));
				
				double sp1 = plnParameter.getNoOfSalesperson();
				plnParameter.setSp(sp1);
				plnParameter.setNoOfSalesperson((int) (plnParameter.getHalfDays() + 0.5));
				
				totalTime = totalTime - (avgTime*plnParameter.getHalfDays());
				threshold = totalTime / plnParameter.getHalfDays();

			} else {
				plnParameter.setNoOfSalesperson(territoryFormation.identifySalesPerson(plnParameter, null, 0, totalTime,
						plnParameter.getNoOfSalesperson(), distributionFlag));
				totalTime = totalTime - (avgTime*plnParameter.getNoOfSalesperson());
				threshold = territoryFormation.calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalTime);
			}
			
			thresholdNew = threshold;
			indexId = 0;
			prevOutlet = null;
			int visitSeq = 1;
			for (int i = 0; i < kmeanClusterDetails.size(); i++) {
				temp = kmeanClusterDetails.get(i);
				double tempSize = temp.getDataPoints().size();
				for (int j = 0; j < tempSize; j++) {

					currentOutlet = temp.getDataPoints().get(j);
					currentOutlet.setVisitSequence(visitSeq++);
					double distance = 0;
					double timeWithDist = 0;
					double time = 0.0;
					double distTime = 0.0;
					double duration=0.0;
					if (distributionFlag){
						duration = currentOutlet.getCallDuration() * currentOutlet.getCallFrequencyMultiplier();
						timeWithDist = duration;
					}
					else{
						if (prevOutlet != null) {
							if(plnParameter.getDistanceType() == 2){
								//Use OSRM distance
								distance = territoryFormation.distanceBetweenTwoOutlets(prevOutlet, currentOutlet, allDistance);
							}
							else{
								distance = territoryFormation.calculateLinearDistance(prevOutlet, currentOutlet);
							}
							time = (distance / (distributorAvgSpeed * 16.667));
						}

						duration = currentOutlet.getVisitDuration()*currentOutlet.getMultiplier();
						timeWithDist = time*currentOutlet.getMultiplier() + duration;
						
						if (plnParameter.getRoundTripTime() == 1) {
							Outlet dist = new Outlet(distributor.getLatitude(), distributor.getLongitude());
							
							if(plnParameter.getDistanceType() == 2){
								//Use OSRM distance
								distance = territoryFormation.distanceBetweenTwoOutlets(currentOutlet, dist, allDistance);
							}
							else{
								distance = territoryFormation.calculateLinearDistance(currentOutlet, dist);
							}
							distTime = (distance / (distributorAvgSpeed * 16.667));
							timeWithDist += distTime;
						}
					}

					if ((currVal + timeWithDist ) > thresholdNew) {
						boolean addInBeat = true;
						if (territoryFormation.isGapIsShortWithNext(currVal+ timeWithDist,
								timeWithDist, thresholdNew)) {
							addInBeat = false;
							dataPoints.add(currentOutlet);
							currVal += timeWithDist;
						}

						gsOutput.setDataPoints(dataPoints);
						kList.add(gsOutput);
						finalResultMap.put(indexId, kList);
						indexId++;
						System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalTime\t" + totalTime + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
						kList = new ArrayList<ClusterDetails>();
						dataPoints = new Vector<>();
						gsOutput = new ClusterDetails();
						curvalsum += currVal;
						
						if (plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday")
								&& plnParameter.getHalfDayFlag()) {
							thresholdNew = Math.round(((threshold * plnParameter.getHalfDays()) - curvalsum)
									/ (plnParameter.getHalfDays() - indexId) * 1000) / 1000.0;
						} else {
							thresholdNew = Math.round(
									(totalTime - curvalsum) / (Math.round((totalTime - curvalsum) / threshold)) * 1000)
									/ 1000.0;
						}
						
						thresholdNew = thresholdNew <=0?threshold:thresholdNew;
						currVal = 0d;
						if (addInBeat){
							dataPoints.add(currentOutlet);
							currVal = duration;
						}
						prevOutlet = null;
					}
					else {
						prevOutlet = temp.getDataPoints().get(j);
						currentOutlet.setTravalingDistance(distance);
						currentOutlet.setTravalingTime(time);
						dataPoints.add(currentOutlet);
						timeWithDist-=distTime;
						currVal += timeWithDist;
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
				System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalTime\t" + totalTime + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
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
				System.out.println(indexId + "\tDatapoints\t" + dataPoints.size() + "\ttotalTime\t" + totalTime + "\tcurvalsum\t" + curvalsum + "\tCurval\t" + currVal + "\tThres\t"+thresholdNew);
				dataPoints = new Vector<>();
				gsOutput = new ClusterDetails();
			}
		}
		return finalResultMap;
	}
	
	
}
