package com.planner.territory.model.kmean;

import com.planner.territory.model.pjp.Outlet;

import java.util.*;

/***
 * java class hold main kmean clustering logic
 */
public class JCA{
	public Cluster[] clusters;
	private final int miter;
	private Vector<Outlet> mDataPoints = new Vector<Outlet>();
	private Vector<Outlet> mOutlets = new Vector<Outlet>();
	private double mSWCSS;
	private Map<Integer, SortedMap<Integer, Integer>> salp = new HashMap<Integer, SortedMap<Integer, Integer>>();
	private SortedMap<Integer, Integer> aSalp = new TreeMap<Integer, Integer>();
	private SortedMap<Integer, Integer> rSalp = new TreeMap<Integer, Integer>();
	private List list = null;
	private final SortedMap<Integer, Integer> unitSalp = new TreeMap<Integer, Integer>();
	private int noOfPointsInCluster;
	private int noToAdd = 0;
	private int noToDelete = 0;
	public int noOfClusters;
	private int noOfShops;
	private double tolFac;
	private double costValue;
	private int planBy;
	private double[][] distances;
	int vehCnt;
	int OutletsCnt = 0;
	SortedMap<Double, Double> myMapLastVehPer;
	double[] lastVehPerList;
	public Map<String, Double> mapA = new HashMap<String, Double>();
	Integer distributor_id;
	String criteria;
	double avgTravelingSpeed;
	int planWay;
	Outlet distDp;
	int mixedFreqFlag;
	int planForWeek;
	int beatNameFlag;
	double maxTime;
	double minTime;
	int reuseVehicle;
	int orderSplit;
	int custLoadType;
	int vehUniqueVal = 3;
	public List<List<Integer>> vehCombMainSet = new ArrayList<List<Integer>>();
	public List<Integer> vehCombSet = new ArrayList<Integer>();
	public List<Double> vehCombWeightSet = new ArrayList<Double>();
	public double vehCombWeight;
	public Map<String, Double> mapD = new HashMap<String, Double>();
	public List<Cluster> clusterList;
	public Map<String, Double> CustWiseTotalOrder;
	List<int[]> outletsList = new ArrayList<int[]>();
	int repeatDay[];
	private int workingDays = 0;
	private int planDays = 365;
	public TreeSet<Integer> visitStartDays = new TreeSet<Integer>();
	public int totalSP = 0;
	public boolean WINDOW_FLAG = false;

	public int getNoToAdd() {
		return noToAdd;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public void setNoToAdd(int noToAdd) {
		this.noToAdd = noToAdd;
	}

	public int getNoToDelete() {
		return noToDelete;
	}

	public void setNoToDelete(int noToDelete) {
		this.noToDelete = noToDelete;
	}

	public int getNoOfPointsInCluster() {
		return noOfPointsInCluster;
	}

	public SortedMap<Integer, Integer> getASalp() {
		return aSalp;
	}

	public void setASalp(SortedMap<Integer, Integer> salp) {
		aSalp = salp;
	}

	public JCA(int iter, List<Cluster> clusterLst, Outlet distDp, int avgTravelingSpeed) {
		this.distributor_id = distDp.getOutletID();
		clusters = new Cluster[clusterLst.size()];
		this.clusters = clusterLst.toArray(clusters);
		this.avgTravelingSpeed = avgTravelingSpeed;
		miter = iter;
	}

	private void calcSWCSS() {
		double temp = 0;
		for (int i = 0; i < clusters.length; i++) {
			temp = temp + clusters[i].getSumSqr();
		}
		mSWCSS = temp;
	}
	
	void resetPoint(){
        /***
         * assign DataPoint to clusters
         */
        int n = 0;
		loop1: while (true) {
            for (int l = 0; l < clusters.length; l++) 
            {
            	if (clusters[l].isDisolved()){
            		while(n < mDataPoints.size()){
	            		if(!mDataPoints.elementAt(n).isbAssigned()){
	            			clusters[l].addDataPoint(mDataPoints.elementAt(n));
		            		n++;
	            			break;
	            		}
	            		n++;
            		}
            	}
                if (n >= mDataPoints.size())
                    break loop1;
            }
            if (n == 0)
                break loop1;
        }

    }

	public double getTotalDistance() {
		double distTotal = 0.0;
		for (int i = 0; i < clusters.length; i++) {
			if (i != 0) {
				distTotal = distTotal + calculateLinearDistance(
						new Outlet(clusters[i - 1].getCentroid().getCx(), clusters[i - 1].getCentroid().getCy()),
						new Outlet(clusters[i].getCentroid().getCx(), clusters[i].getCentroid().getCy()));
			}
		}
		return distTotal;
	}

	public double[][] getMaxMinCentroidCoodinate() {

		double x = 0, y = 0;
		/***
		    x : [ min , max ]
		    y : [ min , max ]
		 */
		double[][] co = { { clusters[0].getCentroid().getCx(), clusters[0].getCentroid().getCx() },
				{ clusters[0].getCentroid().getCy(), clusters[0].getCentroid().getCy() } };

		for (int i = 0; i < clusters.length; i++) {
			x = clusters[i].getCentroid().getCx();
			y = clusters[i].getCentroid().getCy();

			if (x < co[0][0])
				co[0][0] = x;
			if (x > co[0][1])
				co[0][1] = x;

			if (y < co[1][0])
				co[1][0] = y;
			if (y > co[1][1])
				co[1][1] = y;

		}

		return co;
	}

	public void startAnalysisNew() {
		try {

			/*
			 * double[][] allDistance;
			 * 
			 * allDistance = new double[totalPoints][totalPoints];
			 * 
			 * for (int i = 0; i < totalPoints; i++) { for (int j = 0; j < totalPoints; j++)
			 * { allDistance[i][j] = calculateLinearDistance(mOutlets.elementAt(i),
			 * mOutlets.elementAt(j)); } }
			 */
			
			/*
			 * distances = new double[totalPoints][totalPoints]; double totaldist = 0.0; for
			 * (int i = 0; i < totalPoints; i++) { totaldist = 0; for (int j = 0; j <
			 * totalPoints; j++) { totaldist +=
			 * calculateLinearDistance(mOutlets.elementAt(i), mOutlets.elementAt(j));
			 * //allDistance[i][j] distances[i][j] = totaldist; } }
			 */

			for (int i = 0; i < clusters.length; i++) {
				//calcCentroidNew(clusters[i]);
				clusters[i].getCentroid().calcCentroid();
			}
			
			calcSWCSS();

			startKmeansNew();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void startKmeansNew() {
		for (int i = 0; i < 100; i++) {
			/****
			 * enter the loop for cluster 1
			 */
			for (int j = 0; j < clusters.length; j++) {
				for (int k = 0; k < clusters[j].getNumDataPoints(); k++) {

					/***
					 * pick the first element of the first cluster
					 */
					/***
					 * get the current Euclidean distance
					 */
					double tempEuDt = clusters[j].getDataPoint(k).getCurrentEuDt();
					double tempDt = clusters[j].getDataPoint(k).getCentroidDist();
					Cluster tempCluster = null;
					boolean matchFoundFlag = false;
					boolean insertAtStart = false;

					/***
					 * call testEuclidean distance for all clusters
					 */
					for (int l = 0; l < clusters.length; l++) {
						if (tempEuDt > clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid())) {
							tempEuDt = clusters[j].getDataPoint(k).testEuclideanDistance(clusters[l].getCentroid());
							if (tempDt < 0)
								insertAtStart = true;
							else
								insertAtStart = false;
							tempDt = Math.abs(tempDt);

							tempCluster = clusters[l];
							matchFoundFlag = true;
						}

					} /*** for variable 'l' - Looping between different Clusters for matching a Data
						// Point. */

					/***
					 * add Outlet to the cluster and calcSWCSS
					 */
					if (matchFoundFlag) {
						Outlet dp = clusters[j].getDataPoint(k);
						clusters[j].removeDataPoint(dp);

						if (insertAtStart)
							tempCluster.addDataPoint(dp, 0);
						else
							tempCluster.addDataPoint(dp);

						/***
						 *  for variable 'm' - Recalculating centroids for all Clusters
						 */
						for (int m = 0; m < clusters.length; m++) {
							clusters[m].getCentroid().calcCentroid();
						}

						calcSWCSS();
					}
					/***
					 * if statement - A Data Point is eligible for transfer between Clusters.
					 */
				} /***
				 for variable 'k' - Looping through all Data Points of the current Cluster.
				 */
			} /***
			 for variable 'j' - Looping through all the Clusters.
			 */
		} /***
		for variable 'i' - Number of iterations.
		*/
	}

	
	public Vector[] getClusterOutput() {
		Vector v[] = new Vector[clusters.length];
		for (int i = 0; i < clusters.length; i++) {
			v[i] = clusters[i].getDataPoints();
		}
		return v;
	}

	public int getKValue() {
		return clusters.length;
	}

	public int getIterations() {
		return miter;
	}

	public int getTotalOutlets() {
		return mOutlets.size();
	}

	public Cluster getCluster(int pos) {
		return clusters[pos];
	}


	public void sort() {
		for (int i = 0; i < clusters.length; i++) {
			clusters[i].sort();
		}
	}

	public void avg() {
		for (int i = 0; i < clusters.length; i++) {
			clusters[i].avg();
		}
	}

	public void calcCentroid() {
		for (int i = 0; i < clusters.length; i++) {
			clusters[i].getCentroid().calcCentroid();
		}
	}

	public Map<Integer, SortedMap<Integer, Integer>> getSalp() {
		return salp;
	}

	public void setSalp(Map<Integer, SortedMap<Integer, Integer>> salp) {
		this.salp = salp;
	}

	public SortedMap<Integer, Integer> getRSalp() {
		return rSalp;
	}

	public void setRSalp(SortedMap<Integer, Integer> salp) {
		rSalp = salp;
	}

	public void calculateDistanceFromCentroid() {
		for (int i = 0; i < clusters.length; i++) {
			clusters[i].calculateDistanceFromCentroid();
			clusters[i].avg();
		}
	}

	public SortedMap<Integer, Integer> getUnitSalp() {
		return unitSalp;
	}

	public double calculateDistance(Centroid c1, Centroid c2) {
		return Math.sqrt(Math.pow((c1.getCx() - c2.getCx()), 2) + Math.pow((c1.getCy() - c2.getCy()), 2));
	}

	public Centroid getCentroid(int start, int end) {
		double x = 0;
		double y = 0;
		for (int j = start; j <= end; j++) {
			x += clusters[j].getCentroid().getCx();
			y += clusters[j].getCentroid().getCy();
		}
		return new Centroid(x / (end - start + 1), y / (end - start + 1));
	}

	public double getCostValue() {
		return costValue;
	}

	public void setCostValue(double costValue) {
		this.costValue = costValue;
	}

	public void createClustersNew(int[] outlets) {
		Outlet dpTemp = null;
		int totalPoints, i = 0, j = 0;

		int noofDp = (int) Math.ceil(outlets.length / (double) noOfClusters);

		for (int m = 0; m < outlets.length; m++, j++) {
			dpTemp = mOutlets.elementAt(outlets[m]);
			if (noofDp != j) {
				clusters[i].addDataPoint(dpTemp);
			} else {
				i++;
				j = 0;
				clusters[i].addDataPoint(dpTemp);
			}
		}
	}

	public int getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(int workingDays) {
		this.workingDays = workingDays;
	}

	public int getPlanDays() {
		return planDays;
	}

	public void setPlanDays(int planDays) {
		this.planDays = planDays;
	}

	public Vector<Outlet> getmOutlets() {
		return mOutlets;
	}

	public void setmOutlets(Vector<Outlet> mOutlets) {
		this.mOutlets = mOutlets;
	}

	public double getAvgTravelingSpeed() {
		return avgTravelingSpeed;
	}

	public void setAvgTravelingSpeed(double avgTravelingSpeed) {
		this.avgTravelingSpeed = avgTravelingSpeed;
	}

	public double calculateLinearDistance(Outlet from, Outlet to) {
		try {
			double b = Math.PI / 180;
			double lat1 = from.getLatitude() * b;
			double long1 = from.getLongitude() * b;
			double lat2 = to.getLatitude() * b;
			double long2 = to.getLongitude() * b;
			double difflat = Math.abs(lat2 - lat1);

			double difflong = Math.abs(long2 - long1);
			double a = Math.sin(difflat / 2) * Math.sin(difflat / 2)
					+ Math.cos(lat1) * Math.cos(lat2) * Math.sin(difflong / 2)
					* Math.sin(difflong / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double distance = 6378137 * c;  // metres
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	//shubham
	public JCA(int k, int iter, Vector<Outlet> dataPoints, 
			int noOfPointsInCluster,int distanceToCentroid, int planBy, String PlanRunTypeflag, double costValue,int distributor_id, String criteria, int mixedFreqFlag, int planForWeek, int beatNameFlag, double maxTime, Outlet distDp, double avgTravelingSpeed, double minTime, int planWay) {
		if(distanceToCentroid!=0){
			dataPoints=removeFarPoint(dataPoints,distanceToCentroid);
		}
		this.distributor_id=distributor_id;
		this.criteria=criteria;
    	tolFac=0.05;
    	noOfClusters = k;
    	noOfShops = noOfPointsInCluster;
    	this.costValue = costValue;
    	this.planBy = planBy;
    	this.mixedFreqFlag = mixedFreqFlag;
    	this.planForWeek = planForWeek;
    	this.beatNameFlag = beatNameFlag;
    	this.maxTime = maxTime;
    	this.minTime = minTime;
    	this.distDp = distDp;
    	this.avgTravelingSpeed = avgTravelingSpeed;
    	this.planWay = planWay;
    	
    	clusters = new Cluster[noOfClusters];
		this.noOfPointsInCluster = noOfPointsInCluster;
		for (int i = 0; i < noOfClusters; i++) {
			clusters[i] = new Cluster("cluster"+ i) ;
		}
		miter = iter;
		mDataPoints = dataPoints;
		mOutlets = dataPoints;
        for (int i = 0; i < dataPoints.size(); i++) {
        	this.mDataPoints.elementAt(i).setiIndex(i);
        	this.mOutlets.elementAt(i).setiIndex(i);
        }
	}
	
	public JCA(int i, String beatGroupMode, int planForWeek, int maxTime, Outlet distDp2, int travellingSpeed) {
		this.distributor_id=distributor_id;
    	this.avgTravelingSpeed = avgTravelingSpeed;
    	this.planWay = planWay;
    	clusters = new Cluster[noOfClusters];
		miter = i;
	}

	private  Vector<Outlet> removeFarPoint(Vector<Outlet> dataPoints,int distanceToCentroid){
		double mCx=0, mCy=0;
		
		for (Outlet dataPoint : dataPoints) {
			mCx=mCx+dataPoint.getLatitude();
			mCy=mCy+dataPoint.getLongitude();
		}
		mCx=mCx/dataPoints.size();
		mCy=mCy/dataPoints.size();
		Iterator<Outlet> itr=dataPoints.iterator();
		Outlet dt=null;
		while(itr.hasNext()){
			dt=itr.next();
			if(dt.calculateLinearDistance(mCx, mCy)>distanceToCentroid){
				System.out.println("removed "+dt.getOutletID());
				itr.remove();
			}
		}
		return dataPoints;
	}
}
