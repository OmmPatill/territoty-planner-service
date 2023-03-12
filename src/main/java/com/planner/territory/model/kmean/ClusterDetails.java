package com.planner.territory.model.kmean;


import com.planner.territory.model.pjp.Outlet;

import java.util.Vector;

/***
 * used to hold all kmean generated cluster details
 */
public class ClusterDetails {
	private int clusterId;
	private Vector<Outlet> dataPoints;
	private Double noOfDataPoint; /** by frequency */
	private Double totalTime;
	private Double value;
	private Centroid centroid;
	private double distance;
	private double newCentoirdToCurrentCetroidDitance;

	public ClusterDetails() {
		super();
	}
	
	public ClusterDetails(int clusterId, Vector<Outlet> dataPoints, final int maxFreq, final int planForWeek,
			JCA jca, Centroid centroid, Outlet prevDp) {
		super();
		this.clusterId = clusterId;
		this.dataPoints = dataPoints;
		calculateTotalOutlet(maxFreq, planForWeek);
		calculateTotalTime(maxFreq, planForWeek, jca, prevDp);
		calculateValues();
		if (centroid == null)
			calculateCentroid();
		else
			this.centroid = centroid;
	}
	
	public ClusterDetails(int clusterId, Vector<Outlet> dataPoints, final int maxFreq,
			JCA jca, Centroid centroid, Outlet prevDp) {
		super();
		this.clusterId = clusterId;
		this.dataPoints = dataPoints;
		calculateTotalOutlet(maxFreq, 1);
		calculateTotalTime();
		if (centroid == null)
			calculateCentroid();
		else
			this.centroid = centroid;
	}

	private void calculateTotalTime() {
		totalTime = 0.0;
		for (int j = 0; j < dataPoints.size(); j++) {
			totalTime = totalTime + dataPoints.elementAt(j).getCallDuration();
		}
	}

	private void calculateCentroid() {
		centroid = new Centroid(0, 0);
		Cluster c = new Cluster("temp");
		for (int i = 0; i < dataPoints.size(); i++) {
			c.addDataPoint(dataPoints.get(i));
		}
		centroid.setCluster(c);
		centroid.calcCentroid();
	}

	private void calculateValues() {
		value = 0.0;
		for (int j = 0; j < dataPoints.size(); j++) {
			value = value + dataPoints.elementAt(j).getAvgTurnover();
		}
	}

	private void calculateTotalOutlet(final int maxFreq, final int planForWeek) {
		noOfDataPoint = 0.0;
		for (int j = 0; j < dataPoints.size(); j++) {
			noOfDataPoint = noOfDataPoint  + 1;	//getFreqValue(maxFreq, planForWeek, dataPoints.elementAt(j));
		}
	}

	public void calculateTotalTime(final int maxFreq, final int planForWeek, JCA jca, Outlet prevDp) {
		totalTime = 0.0;
		Outlet dpTemp, previousDpTemp = null;
		Double tempBreakPoint = 1.0;
		double tempcoordinateDist = 0.0;
		Double totalTimeVar = 0.0;
		String key = "";
		for (int j = 0; j < dataPoints.size(); j++) {
			dpTemp = dataPoints.elementAt(j);

			//tempBreakPoint = getFreqValue(maxFreq, planForWeek, dpTemp);
			tempBreakPoint = dpTemp.getMultiplier();

			if (previousDpTemp == null) { // 1st outlet : no travel time from distributor

				if (prevDp != null) {

					tempcoordinateDist = jca.calculateLinearDistance(prevDp, dpTemp);

					totalTimeVar = (tempcoordinateDist / (jca.getAvgTravelingSpeed() * 16.667));
					dpTemp.setTravalingTime(totalTimeVar);

					totalTime = totalTime + (totalTimeVar + dpTemp.getVisitDuration()) * tempBreakPoint;

				} else {
					totalTimeVar= 0.0;
					dpTemp.setTravalingTime(totalTimeVar);
					totalTime = totalTime + (dpTemp.getVisitDuration() * tempBreakPoint);
				}

			} else {

				tempcoordinateDist = jca.calculateLinearDistance(previousDpTemp, dpTemp);

				totalTimeVar = (tempcoordinateDist / (jca.getAvgTravelingSpeed() * 16.667));

				dpTemp.setTravalingTime(totalTimeVar);
				totalTime = totalTime + ((totalTimeVar + dpTemp.getVisitDuration()) * tempBreakPoint);
			}
			previousDpTemp = dpTemp;
			totalTimeVar = 0.0;
		}

	}


	/*
	 * public double getFreqValue(final int maxFreq, final int planForWeek, final
	 * Outlet dpTemp) { switch (planForWeek) { case 1: return
	 * (dpTemp.getVisitFrequency() == 4 ? 1.0 : 0.5); case 2: return
	 * (dpTemp.getVisitFrequency()== 2 ? 1.0 : 0.5); case 3: return
	 * (dpTemp.getVisitFrequency() == 8 ? 1.0 : 0.5); case 4: int currfreq =
	 * dpTemp.getVisitFrequency(); switch (maxFreq) { case 8: return (currfreq == 8
	 * ? 1.0 : currfreq == 5 ? 0.625 : currfreq == 4 ? 0.5 : currfreq == 3 ? 0.375 :
	 * currfreq == 2 ? 0.25 : 0.125); case 5: return (currfreq == 5 ? 1.0 : currfreq
	 * == 4 ? 0.8 : currfreq == 3? 0.6 : currfreq == 2 ? 0.4 : 0.2); case 4: return
	 * (currfreq == 4 ? 1.0 : currfreq == 3 ? 0.75 : currfreq == 2 ? 0.5 : 0.25);
	 * case 3: return (currfreq == 3 ? 1.0 : currfreq == 2 ? 0.66 : 0.33); case 2:
	 * return (currfreq == 2? 1.0 : 0.5); case 1: return 1.0; default: }
	 * 
	 * } return 1.0; }
	 */

	public Vector<Outlet> getDataPoints() {
		return dataPoints;
	}

	public int getClusterId() {
		return clusterId;
	}

	public void setDataPoints(Vector<Outlet> dataPoints) {
		this.dataPoints = dataPoints;
	}

	public Double getNoOfDataPoint() {
		return noOfDataPoint;
	}

	public Double getTotalTime() {
		return totalTime;
	}

	public Double getValue() {
		return value;
	}

	public Centroid getCentroid() {
		return centroid;
	}
	
	
	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	
	public double getNewCentoirdToCurrentCetroidDitance() {
		return newCentoirdToCurrentCetroidDitance;
	}

	public void setNewCentoirdToCurrentCetroidDitance(double newCentoirdToCurrentCetroidDitance) {
		this.newCentoirdToCurrentCetroidDitance = newCentoirdToCurrentCetroidDitance;
	}

	@Override
	public String toString() {
		return clusterId + "\t" + noOfDataPoint + "\t" + totalTime + "\t" + value + "\t" + centroid.getCx() + "\t"
				+ centroid.getCy();
	}

}