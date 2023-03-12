package com.planner.territory.proccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/****
 * 
 * The OutlierSkipper implements outlying outlet skipper logic using method recurseToGetAllOutlier(M)
 *
 */
@Component
public class OutlierSkipper {
	
	@Autowired
	TerritoryFormation territoryFormation;
	
	/****
	 * Used to get all outlier cunsumer
	 * @param prevInvalidListSize
	 * @param nextInvalidListSize
	 * @param avgDistance
	 * @param invalidClusterList
	 * @param validClusterList
	 * @param kmeanClusterDetails
	 * @param distributor
	 * @param plnParameter
	 * @return
	 */
	public List<ClusterDetails> recurseToGetAllOutlier(int prevInvalidListSize, int nextInvalidListSize,
													   double avgDistance, List<ClusterDetails> invalidClusterList, List<ClusterDetails> validClusterList,
													   List<ClusterDetails> kmeanClusterDetails, Distributor distributor, PlanParameter plnParameter) {

		List<ClusterDetails> newValidClusterList = new ArrayList<>();



		skipOutlierOutletFromBeatPlan(avgDistance, invalidClusterList, validClusterList, kmeanClusterDetails);


		nextInvalidListSize = invalidClusterList.size();

		if (prevInvalidListSize != nextInvalidListSize) {

			prevInvalidListSize = nextInvalidListSize;

			kmeanClusterDetails = new ArrayList<>(validClusterList);

			Outlet newCentroid = calculateNewCentroid(kmeanClusterDetails);
			calculateNewCentroidToCurrentCentroidDistance(newCentroid, kmeanClusterDetails);
			avgDistance = getAvgDistance(kmeanClusterDetails,plnParameter.getMultiplier());

			//avgDistance = getAvgDistanceOfClusterCentroid(kmeanClusterDetails, distributor);

			recurseToGetAllOutlier(prevInvalidListSize, nextInvalidListSize, avgDistance, invalidClusterList,
					validClusterList, kmeanClusterDetails, distributor,plnParameter);
		}
		return validClusterList;
	}
	
	/****
	 * Method used filter out outlyling outlet and valid outlet
	 * @param avgDistance
	 * @param invalidClusterList
	 * @param validClusterList
	 * @param kmeanClusterDetails
	 */
	private void skipOutlierOutletFromBeatPlan(double avgDistance, List<ClusterDetails> invalidClusterList,
			List<ClusterDetails> validClusterList, List<ClusterDetails> kmeanClusterDetails) {

		validClusterList.removeAll(validClusterList);

		for (ClusterDetails clusterDetails : kmeanClusterDetails) {
			 if (clusterDetails.getNewCentoirdToCurrentCetroidDitance() <= avgDistance) {
				validClusterList.add(clusterDetails);
			} else {
				invalidClusterList.add(clusterDetails);
			}
		}
	}
	
	/***
	 * method used to get average distance 
	 * @param kmeanClusterDetails
	 * @param multiplier
	 * @return
	 */
	public double getAvgDistance(List<ClusterDetails> kmeanClusterDetails, int multiplier) {
		double avgDistace = kmeanClusterDetails.stream().collect(Collectors.summingDouble(c->c.getNewCentoirdToCurrentCetroidDitance()));
		return (avgDistace/kmeanClusterDetails.size()) * multiplier;
	}
	
	/***
	 * Method used to set distance between newCentroid to already calculate centroid from kmeans
	 * @param newCentroid
	 * @param kmeanClusterDetails
	 */
	public void calculateNewCentroidToCurrentCentroidDistance(Outlet newCentroid,
			List<ClusterDetails> kmeanClusterDetails) {

		double distance = 0;
		Outlet prevOutlet = newCentroid ;
		Outlet currentOutlet = null;

		for (ClusterDetails clusterDetails : kmeanClusterDetails) {

			currentOutlet = new Outlet(clusterDetails.getCentroid().getCx() ,clusterDetails.getCentroid().getCy());

			if(prevOutlet != null){
				distance = territoryFormation.calculateLinearDistance(prevOutlet, currentOutlet);
			}
			clusterDetails.setNewCentoirdToCurrentCetroidDitance(distance/1000);
		}
	}
	
	/***
	 * Method used to calculate avarage based centroid
	 * @param kmeanClusterDetails
	 * @return
	 */
	public Outlet calculateNewCentroid(List<ClusterDetails> kmeanClusterDetails) {
		Double lat = kmeanClusterDetails.stream().collect(Collectors.summingDouble(c->c.getCentroid().getCx()));
		Double lang = kmeanClusterDetails.stream().collect(Collectors.summingDouble(c->c.getCentroid().getCy()));

		lat = lat/kmeanClusterDetails.size();
		lang = lang/kmeanClusterDetails.size();

		return  new Outlet(lat,lang,0);
	}
}
