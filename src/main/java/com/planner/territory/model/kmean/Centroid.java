package com.planner.territory.model.kmean;


import com.planner.territory.model.pjp.Outlet;

/***
 * 
 * pojo class used to hold Clustor Centroid information
 *
 */
public class Centroid {
	private double mCx, mCy;
	private Cluster mCluster;

	public Centroid(double cx, double cy) {
		mCx = cx;
		mCy = cy;
	}

	public void calcCentroid() {
		try {
			int numDP = mCluster.getNumDataPoints();
			if(numDP==0) {
			}
			double tempX = 0, tempY = 0;
			int i;
			/***
			 * caluclating the new Centroid
			 */
			for (i = 0; i < numDP; i++) {
				tempX = tempX + mCluster.getDataPoint(i).getLatitude();
				/***
				 * total for x
				 */
				tempY = tempY + mCluster.getDataPoint(i).getLongitude();
				/**
				 * total for y
				 */
			}
			mCx = tempX / numDP;
			mCy = tempY / numDP;
			/***
			 * calculating the new Euclidean Distance for each Data Point
			 */
			tempX = 0;
			tempY = 0;
			for (i = 0; i < numDP; i++) {
				mCluster.getDataPoint(i).calcEuclideanDistance();
				mCluster.getDataPoint(i).calculateDistance();
			}
			/***
			 * calculate the new Sum of Squares for the Cluster
			 */
			 mCluster.calcSumOfSquares();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void calcCentroidNew() { 
		try {
			int numDP = mCluster.getNumDataPoints();
			double tempX = 0, tempY = 0;
			int i;
			/***
			 * caluclating the new Centroid
			 */
			for (i = 0; i < numDP; i++) {
				tempX = tempX + mCluster.getDataPoint(i).getLatitude();
				/***
				 * total for x
				 */
				tempY = tempY + mCluster.getDataPoint(i).getLongitude();
				/***
				 * total for y
				 */
			}
			mCx = tempX / numDP;
			mCy = tempY / numDP;
			/***
			 * calculating the new Euclidean Distance for each Data Point
			 */
			tempX = 0;
			tempY = 0;
			for (i = 0; i < numDP; i++) {
				mCluster.getDataPoint(i).calcEuclideanDistance();
				mCluster.getDataPoint(i).calculateDistance();
			}
			/***
			 * calculate the new Sum of Squares for the Cluster
			 */
			 mCluster.calcSumOfSquares();
		} catch (Exception e) {
			System.out.println("Cluster " + mCluster.getName() + " Points "
					+ mCluster.getDataPoints().size());
			e.printStackTrace();
		}
	}

	public void setCluster(Cluster c) {
		mCluster = c;
	}

	public double getCx() {
		return mCx;
	}

	public double getCy() {
		return mCy;
	}

	public Cluster getCluster() {
		return mCluster;
	}

	public double testCentroidEuclideanDistance(Outlet dp) {
		return Math.sqrt(Math.pow((getCx() - dp.getLatitude()), 2)
				+ Math.pow((getCy() - dp.getLongitude()), 2));
	}
}
