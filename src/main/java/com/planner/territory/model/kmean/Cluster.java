package com.planner.territory.model.kmean;


import com.planner.territory.model.pjp.Outlet;

import java.util.*;


/***
 * pojo class used to store cluster data
 */
public class Cluster {
	private String mName;
	private Centroid mCentroid;
	private double mSumSqr;
	private Vector<Outlet> mDataPoints;
	private double avg;
	private SortedMap<String, Integer> cName = new TreeMap<String, Integer>();
	private int needToChange;
	private int divideInTo = 1;
	private List<Integer> NotTransferTo = new ArrayList<Integer>();
	private int PlanId;
	private int ClusterId;
    private boolean Disolved;
    private boolean Block;
    private double totalCost;
    private double centerDist;
    private double vPoints;
    private String vehicleName;
    private double vehicleWeight;
    private double vehicleVolume;
    private int vehicleOutlet;
    private double vehicleTime;
    private double orderTime;
    private int vehicleId;
	private final double LinearPer= 0.01;
	public Map<String, Double>  mapA = new HashMap<String, Double>();
	public Map<String, Double>  mapD = new HashMap<String, Double>();
	private int beatNo;
	private int visitDay;
	private double requiredVisitedTime;
	private boolean isInvalidBeat;
	private int salesPersonId;
	
    public double getvPoints() {
		return vPoints;
	}

	public double getCenterDist() {
		return centerDist;
	}

	public void setCenterDist(double centerDist) {
		this.centerDist = centerDist;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public boolean isBlock() {
		return Block;
	}

	public void setBlock(boolean block) {
		Block = block;
	}

	public boolean isDisolved() {
		return Disolved;
	}

	public void setDisolved(boolean disolved) {
		if (disolved){
			int dataCount ;
			dataCount = this.mDataPoints.size();

			for (int l = 0; l < dataCount; l++)
				removeDataPoint((Outlet)this.mDataPoints.elementAt(0));
		}
		Disolved = disolved;
	}

	public int getPlanId() {
		return PlanId;
	}

	public void setPlanId(int planId) {
		PlanId = planId;
	}

	public int getClusterId() {
		return ClusterId;
	}

	public void setClusterId(int clusterId) {
		ClusterId = clusterId;
	}

	public List<Integer> getNotTransferTo() {
		return NotTransferTo;
	}

	public void setNotTransferTo(List<Integer> notTransferTo) {
		NotTransferTo = notTransferTo;
	}

	public int getDivideInTo() {
		return divideInTo;
	}

	public void setDivideInTo(int divideInTo) {
		this.divideInTo = divideInTo;
	}

	public int getNeedToChange() {
		return needToChange;
	}

	public void setNeedToChange(int needToChange) {
		this.needToChange = needToChange;
	}

	public SortedMap<String, Integer> getCName() {
		return cName;
	}

	public void setCName(SortedMap<String, Integer> name) {
		cName = name;
	}

	public Cluster(String name) {
		divideInTo = 1;
		needToChange = 0;
		mName = name;
		mCentroid = null; // will be set by calling setCentroid()
		mDataPoints = new Vector<Outlet>();
        this.Disolved=true;
        this.Block = false;
        this.vPoints=0.0;
	}
	
	public Cluster(String name, String vehicleName, double vehicleWeight, double vehicleVolume, int vehicleOutlet, int vehicleId, double vehicleTime) {
		divideInTo = 1;
		needToChange = 0;
		mName = name;
		mCentroid = null; // will be set by calling setCentroid()
		mDataPoints = new Vector<Outlet>();
        this.Disolved=true;
        this.Block = false;
        this.vPoints=0.0;
        this.setVehicleName(vehicleName);
        this.setVehicleWeight(vehicleWeight);
        this.vehicleVolume=vehicleVolume;
        this.vehicleOutlet=vehicleOutlet;
        this.setVehicleId(vehicleId);
        this.vehicleTime = vehicleTime;
	}

	public void setCentroid(Centroid c) {
		mCentroid = c;
	}

	public Centroid getCentroid() {
		if(mCentroid==null){
			mCentroid=new Centroid(0, 0);
			mCentroid.setCluster(this);
		}
		return mCentroid;
	}

	public void addDataPoint(Outlet dp) { // called from CAInstance
		dp.setCluster(this); // initiates a inner call to calcEuclideanDistance()
							// in DP.
		mDataPoints.addElement(dp);
		calcSumOfSquares();
		if(dp.getVisitFrequency() == 2){
			vPoints+=0.5;
		}
		else{
			vPoints+=1;
		}
	}

	public void removeDataPoint(Outlet dp) {
		mDataPoints.removeElement(dp);
        dp.removeCluster();
		calcSumOfSquares();
		if(dp.getVisitFrequency() == 2){
			vPoints-=0.5;
		}
		else{
			vPoints-=1;
		}
	}

    public void addDataPoint(Outlet dp, int index) { //called from CAInstance
        dp.setCluster(this); //initiates a inner call to   calcEuclideanDistance() in DP.
        this.mDataPoints.add(index, dp);
        calcSumOfSquares();
		if(dp.getVisitFrequency() == 2 ){
			vPoints+=0.5;
		}
		else{
			vPoints+=1;
		}
    }

    public void removeDataPoint(Outlet dp, int index) {
        this.mDataPoints.removeElementAt(index);
        dp.removeCluster();
        calcSumOfSquares();
        if(dp.getVisitFrequency() == 2 ){
			vPoints+=0.5;
		}
		else{
			vPoints+=1;
		}
    }

	public int getNumDataPoints() {
		return mDataPoints.size();
	}

	public Outlet getDataPoint(int pos) {
		return mDataPoints.elementAt(pos);
	}

	public void calcSumOfSquares() { // called from Centroid
		try {
			int size = mDataPoints.size();
			double temp = 0;
			for (int i = 0; i < size; i++) {
				temp = temp + (mDataPoints.elementAt(i)).getCurrentEuDt();
			}
			mSumSqr = temp;
		} catch (Exception e) {
			e.printStackTrace();
//			logger.error("Error in k", e);
		}
	}

	public double getSumSqr() {
		return mSumSqr;
	}

	public String getName() {
		return mName;
	}

	public Vector<Outlet> getDataPoints() {
		return mDataPoints;
	}

	public void setDataPoints(Vector<Outlet> dp) {
		mDataPoints = dp;
	}

	public double testCentroidEuclideanDistance(Cluster c) {
		return Math.sqrt(Math.pow((this.getCentroid().getCx() - c.getCentroid()
				.getCx()), 2)
				+ Math.pow((this.getCentroid().getCy() - c.getCentroid()
						.getCy()), 2));
	}

	public void transferPoints(int points, Cluster c) {
		try {
			int movePoint = 0;
			double moveDistance = 0;
			for (int i = 0; i < points && c.getNumDataPoints() > 0; i++) {
				movePoint = 0;
				moveDistance = this.getCentroid()
						.testCentroidEuclideanDistance(c.getDataPoint(0));
				for (int j = 1; j < c.getNumDataPoints(); j++) {
					if (moveDistance > this.getCentroid()
							.testCentroidEuclideanDistance(c.getDataPoint(j))) {
						movePoint = j;
						moveDistance = this.getCentroid()
								.testCentroidEuclideanDistance(
										c.getDataPoint(j));
					}
				}
				addDataPoint(c.getDataPoint(movePoint));
				c.removeDataPoint(c.getDataPoint(movePoint));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getDirection(Cluster c) {
		/***
		 *  0-left,1-left:up,2-up,3-up:right,4-right,5right:bottom,6-bottom,7-bottom:left
		 */
		try {
			int direction = 5;
			if (c.getCentroid().getCy() > this.getCentroid().getCy()) {
				direction = 2;
			} else if (c.getCentroid().getCy() < this.getCentroid().getCy()) {
				direction = 8;
			}
			if (c.getCentroid().getCx() > this.getCentroid().getCx()) {
				direction--;
			} else if (c.getCentroid().getCx() < this.getCentroid().getCx()) {
				direction++;
			}
			return direction;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean checkNotTransferTo(int checkClusterId) {
		for (int i = 0; i < NotTransferTo.size(); i++) {
			if (checkClusterId == NotTransferTo.get(i)) {
				return false;
			}
		}
		return true;
	}

	public void sort() {
		try {
			Outlet temp;
			Outlet cur;
			int max = -1;
			for (int i = 0; i < mDataPoints.size(); i++) {
				max = -1;
				temp = mDataPoints.get(i);
				for (int j = i + 1; j < mDataPoints.size(); j++) {
					cur = mDataPoints.get(j);
					if (temp.getLinearDistance() < cur.getLinearDistance()) {
						max = j;
					}
				}
				if (max >= 0) {
					mDataPoints.set(i, mDataPoints.get(max));
					mDataPoints.set(max, temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void avg() {
		avg = 0;
		Outlet cur;
		for (int i = 0; i < mDataPoints.size(); i++) {
			cur = mDataPoints.get(i);
			avg += cur.getLinearDistance();
		}
		avg = avg / mDataPoints.size();
	}

	public void calculateDistanceFromCentroid() {
		for (int i = 0; i < mDataPoints.size(); i++) {
			mDataPoints.get(i).calculateDistance();
		}
	}

	public double getAvg() {
		return avg;
	}

	public void setAvg(double avg) {
		this.avg = avg;
	}

	public String getMName() {
		return mName;
	}

	public void setMName(String name) {
		mName = name;
	}

	public double getMSumSqr() {
		return mSumSqr;
	}

	public void setMSumSqr(double sumSqr) {
		mSumSqr = sumSqr;
	}

	public void calcCost() {
		try {
			int size = mDataPoints.size();
			double temp = 0;
			for (int i = 0; i < size; i++) {
				temp = temp + (mDataPoints.elementAt(i)).getAvgTurnover();
			}
			totalCost = temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public double getVehicleWeight() {
		return vehicleWeight;
	}

	public void setVehicleWeight(double vehicleWeight) {
		this.vehicleWeight = vehicleWeight;
	}

	public double getVehicleVolume() {
		return vehicleVolume;
	}

	public void setVehicleVolume(double vehicleVolume) {
		this.vehicleVolume = vehicleVolume;
	}

	public int getVehicleOutlet() {
		return vehicleOutlet;
	}

	public void setVehicleOutlet(int vehicleOutlet) {
		this.vehicleOutlet = vehicleOutlet;
	}

	public double getVehicleTime() {
		return vehicleTime;
	}

	public void setVehicleTime(double vehicleTime) {
		this.vehicleTime = vehicleTime;
	}
	
	public double getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(double orderTime) {
		this.orderTime = orderTime;
	}

	/**
	 * @return the beatNo
	 */
	public int getBeatNo() {
		return beatNo;
	}

	/**
	 * @param beatNo the beatNo to set
	 */
	public void setBeatNo(int beatNo) {
		this.beatNo = beatNo;
	}

	/**
	 * @return the visitDay
	 */
	public int getVisitDay() {
		return visitDay;
	}

	/**
	 * @param visitDay the visitDay to set
	 */
	public void setVisitDay(int visitDay) {
		this.visitDay = visitDay;
	}

	public double getRequiredVisitedTime() {
		return requiredVisitedTime;
	}

	public void setRequiredVisitedTime(double requiredVisitedTime) {
		this.requiredVisitedTime = requiredVisitedTime;
	}

	public boolean getIsInvalidBeat() {
		return isInvalidBeat;
	}

	public void setInvalidBeat(boolean isInvalidBeat) {
		this.isInvalidBeat = isInvalidBeat;
	}

	public int getSalesPersonId() {
		return salesPersonId;
	}

	public void setSalesPersonId(int salesPersonId) {
		this.salesPersonId = salesPersonId;
	}


	public Vector<Outlet> getmDataPoints() {
		return mDataPoints;
	}

	public void setmDataPoints(Vector<Outlet> mDataPoints) {
		this.mDataPoints = mDataPoints;
	}

	@Override
	public Cluster clone() throws CloneNotSupportedException {
		return new Cluster(this.mName, this.vehicleName, this.vehicleWeight,this.vehicleVolume, this.vehicleOutlet, this.vehicleId, new Double(this.vehicleTime) == null ? 0.0:vehicleTime );
	}
	 private Integer clusterCountFlag;
	 private Integer clusterWeekDay;

	public Integer getClusterCountFlag() {
		return clusterCountFlag;
	}

	public void setClusterCountFlag(Integer clusterCountFlag) {
		this.clusterCountFlag = clusterCountFlag;
	}

	public Integer getClusterWeekDay() {
		return clusterWeekDay;
	}

	public void setClusterWeekDay(Integer clusterWeekDay) {
		this.clusterWeekDay = clusterWeekDay;
	}
}