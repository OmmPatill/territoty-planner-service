package com.planner.territory.model.pjp;

import com.planner.territory.model.kmean.Centroid;
import com.planner.territory.model.kmean.Cluster;
import lombok.*;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Outlet{
    private int outletID;
    private String outletCode;
    private double latitude;
    private double longitude;
    private int visitFrequency;
    private int visitDuration;
    private double avgTurnover;
    private int callFrequency;
    private int callDuration;
    private int prioritySlot;
    private String category;
    private String outletType;
    private String distributorCode;
    private double multiplier;
    private int visitSequence;
    private double distance;
    private double callFrequencyMultiplier;
    private String outletName;

    private boolean bAssigned;
    private Cluster cluster;
    private double linearDistance;
    private double mEuDt;
    private double centroidDist;
    private int iIndex;
    private String area;
    private int storeId;
    private double useNext;
    private double pendingAllocation;
    private double myInterval;
    private boolean callFlag;
    private boolean visitFlag;
    private int callBeatId;
    private int visitBeatId;
    private double travalingDistance;
    private double travalingTime;
    private List<Integer> callVisitArr;

    public Outlet(double latitude, double longitude, Integer outletID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = "";
        this.outletID = outletID;
    }

    public Outlet(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = "";
    }

 /*   public Outlet(MutableMap<String, String> data) {
        this.outletID = Integer.valueOf(data.get("Consumer_ID"));
        this.outletCode = data.get("Consumer_Code");
        this.latitude = Double.valueOf(data.get("Latitude"));
        this.longitude = Double.valueOf(data.get("Longitude"));
        this.visitFrequency = Integer.valueOf(data.get("Visit_Frequency"));
        this.visitDuration = Integer.valueOf(data.get("Visit_Duration"));
        this.avgTurnover = Double.valueOf(data.get("Avg_Turnover"));
        this.callFrequency = Integer.valueOf(data.get("Call_Frequency"));
        this.callDuration = Integer.valueOf(data.get("Call_Duration"));
        this.prioritySlot = Integer.valueOf(data.get("Priority_Slot"));
        this.category = data.get("Consumer_Channel");
        this.outletType = data.get("Consumer_Type_Id");
        this.distributorCode = data.get("Distributor_Code");
        this.pendingAllocation = this.visitFrequency;
        this.outletName = data.get("Consumer_Name");
    }*/

    @Override
    public Outlet clone() throws CloneNotSupportedException {
        // TODO Auto-generated method stub
        return new Outlet(this.outletID, this.outletCode, this.latitude, this.longitude, this.visitFrequency,
                this.visitDuration, this.avgTurnover, this.callFrequency, this.callDuration, this.prioritySlot,
                this.category, this.outletType, this.distributorCode, this.multiplier, this.visitSequence,
                this.distance, this.callFrequencyMultiplier, this.outletName, this.bAssigned, this.cluster,
                this.linearDistance, this.mEuDt, this.centroidDist, this.iIndex, this.area, this.storeId, this.useNext,
                this.pendingAllocation, this.myInterval, this.callFlag,this.visitFlag, this.callBeatId, this.visitBeatId, this.travalingDistance,
                this.travalingTime, this.callVisitArr);
    }

    public static Comparator<Outlet> DataPointXComparator = new Comparator<Outlet>() {
        public int compare(Outlet dp1, Outlet dp2) {
            Double lat1 = dp1.getLatitude();
            Double lat2 = dp2.getLatitude();
            // ascending order
            return lat1.compareTo(lat2);
        }
    };

    public static Comparator<Outlet> DataPointYComparator = new Comparator<Outlet>() {
        public int compare(Outlet dp1, Outlet dp2) {
            Double lat1 = dp1.getLongitude();
            Double lat2 = dp2.getLongitude();
            // ascending order
            return lat1.compareTo(lat2);
        }
    };

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
        this.bAssigned = true;
        calcEuclideanDistance();
        calculateDistance();
    }

    public void removeCluster() {
        this.bAssigned = false;
    }

    public void calcEuclideanDistance() {

        // called when DP is added to a cluster or when a Centroid is
        // recalculated.
        mEuDt = Math.sqrt(Math.pow((latitude - cluster.getCentroid().getCx()), 2)
                + Math.pow((longitude - cluster.getCentroid().getCy()), 2));
    }

    public void calculateDistance() {

        // called when DP is added to a cluster or when a Centroid is
        // recalculated.

        // mEuDt = Math.sqrt(Math.pow((mX - mCluster.getCentroid().getCx()), 2)
        // + Math.pow((mY - mCluster.getCentroid().getCy()), 2));

        linearDistance = calculateLinearDistance(cluster.getCentroid().getCx(),
                cluster.getCentroid().getCy());

    }

    public double calculateLinearDistance(double lat2, double long2) {
        try {
            double b = Math.PI / 180;
            double lat1 = latitude * b;
            double long1 = longitude * b;
            lat2 = lat2 * b;
            long2 = long2 * b;
            double difflat = Math.abs(lat2 - lat1);
            // System.out.println("diffflat is:=" + difflat);
            double difflong = Math.abs(long2 - long1);
            // System.out.println("difflong is:=" + difflong);
            double a = Math.sin(difflat / 2) * Math.sin(difflat / 2)
                    + Math.cos(lat1) * Math.cos(lat2) * Math.sin(difflong / 2)
                    * Math.sin(difflong / 2);
            // System.out.println("a is:=" + a);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            // System.out.println("c is:=" + c);
            double distance = 6378137 * c;
            // System.out.println("Distance is:=" + distance);
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("Error in calculateLinearDistance", e);
            return -1;
        }
    }

    public double calculateLinearDistance(Outlet to) {
        try {
            double b = Math.PI / 180;
            double lat1 = latitude * b;
            double long1 = longitude * b;
            double lat2 = to.getLatitude() * b;
            double long2 = to.getLongitude() * b;
            double difflat = Math.abs(lat2 - lat1);
            // System.out.println("diffflat is:=" + difflat);
            double difflong = Math.abs(long2 - long1);
            // System.out.println("difflong is:=" + difflong);
            double a = Math.sin(difflat / 2) * Math.sin(difflat / 2)
                    + Math.cos(lat1) * Math.cos(lat2) * Math.sin(difflong / 2)
                    * Math.sin(difflong / 2);
            // System.out.println("a is:=" + a);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            // System.out.println("c is:=" + c);
            double distance = 6378137 * c;
            // System.out.println("Distance is:=" + distance);
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
            // logger.error("Error in calculateLinearDistance", e);
            return -1;
        }
    }

    public double getCurrentEuDt() {
        return mEuDt;
    }

    public boolean isbAssigned() {
        return bAssigned;
    }

    public void setbAssigned(boolean bAssigned) {
        this.bAssigned = bAssigned;
    }


    public double testLinearDistance(Centroid c) {
        return calculateLinearDistance(c.getCx(), c.getCy());
        // Math.sqrt(Math.pow((mX - c.getCx()), 2) + Math.pow((mY - c.getCy()),
        // 2));
    }

    public double testEuclideanDistance(Centroid c) {
        return Math.sqrt(Math.pow((latitude - c.getCx()), 2)
                + Math.pow((longitude - c.getCy()), 2));
    }

    public double getCentroidDist() {
        return centroidDist;
    }

    public void setCentroidDist(double centroidDist) {
        this.centroidDist = centroidDist;
    }

    public int getiIndex() {
        return iIndex;
    }

    public void setiIndex(int iIndex) {
        this.iIndex = iIndex;
    }

    public String getArea() {
        return area;
    }

    public double getUseNext() {
        return useNext;
    }

    public void setUseNext(double useNext) {
        this.useNext = useNext;
    }

    public double getPendingAllocation() {
        return pendingAllocation;
    }

    public void setPendingAllocation(double pendingAllocation) {
        this.pendingAllocation = pendingAllocation;
    }
    public double getMyInterval() {
        return myInterval;
    }

    public void setMyInterval(double myInterval) {
        this.myInterval = myInterval;
    }

    @Override
    public String toString() {
        return outletID + "," + outletCode + "," + latitude + ","
                + longitude + "," + visitFrequency + "," + visitDuration
                + "," + avgTurnover + "," + callFrequency + "," + callDuration
                + "," + prioritySlot + "," + category + "," + outletType
                + "," + distributorCode + ", " + multiplier + ","
                + visitSequence + "," + distance + "," + bAssigned + ","  + linearDistance + "," + mEuDt + "," + centroidDist
                + "," + iIndex + "," + area + "," + storeId + "\t";
    }

    public void allocate(){
        this.pendingAllocation--;
    }
}
