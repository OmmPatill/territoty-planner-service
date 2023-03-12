package com.planner.territory.proccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.planner.territory.model.kmean.Cluster;
import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.kmean.JCA;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import org.springframework.stereotype.Component;

/****
 *
 * The KmeanCalulator implements K-mean cluster formation methods
 *
 */
@Component
public class KeanCalculator {

    /****
     * used to create k-mean based cluster
     *
     * @param outletLst
     * @param noOfPointsInCluster
     * @param plnParameter
     * @param distributor
     * @param subClusterDivisor
     * @param distributionFlag
     * @return
     */
    public List<ClusterDetails> kMeanCalulator(List<Outlet> outletLst, int noOfPointsInCluster,
                                               PlanParameter plnParameter, Distributor distributor, int subClusterDivisor, boolean distributionFlag) {

        Outlet distDp = new Outlet(distributor.getLatitude(), distributor.getLongitude());

        List<JCA> jcas = new ArrayList<>();
        JCA jca = createKmeanCluster(new Vector(outletLst), noOfPointsInCluster, plnParameter, distributor, distDp,
                subClusterDivisor, true);

        for (int i = 0; i < jca.clusters.length; i++) {
            jcas.add(createKmeanCluster(jca.clusters[i].getmDataPoints(), noOfPointsInCluster, plnParameter,
                    distributor, distDp, plnParameter.getMinClusterSize(), false));
        }

        if (plnParameter.getPlanType().equalsIgnoreCase("Territory Creation")) {

            List<Cluster> clusterLst = new ArrayList<>();
            try {
                clusterLst = recurseKmean(jcas, noOfPointsInCluster, plnParameter, distributor, distDp,
                        plnParameter.getMinClusterSize(), clusterLst, false);// plnParameter.getMinClusterSize()
            } catch (Exception e) {
                e.printStackTrace();
            }

            JCA jcaNew = new JCA(100, clusterLst, distDp, distributor.getTravellingSpeed());

            List<JCA> jcasNew = new ArrayList<>();
            jcasNew.add(jcaNew);

            return filterZeroOutletCluster(jcasNew, jcaNew, plnParameter, distributionFlag);
        }
        return filterZeroOutletCluster(jcas, jca, plnParameter, distributionFlag);
    }

    /****
     * Create kmean cluster
     *
     * @param vector
     * @param noOfPointsInCluster
     * @param plnParameter
     * @param distributor
     * @param distDp
     * @param kmeanMultiLoop
     * @param mainCluster
     * @return
     */
    private JCA createKmeanCluster(Vector<Outlet> vector, int noOfPointsInCluster, PlanParameter plnParameter,
                                   Distributor distributor, Outlet distDp, double kmeanMultiLoop, boolean mainCluster) {

        int loopCnt = getLoopCount(15, plnParameter.getNoOfSalesperson(), vector.size(),
                plnParameter.getBeatGroupMode(), kmeanMultiLoop, mainCluster, plnParameter.getClusterLoop());

        JCA jca = new JCA(loopCnt, 100, vector, noOfPointsInCluster, plnParameter.getDistanceToCentroid() * 1000, 0,
                plnParameter.getBeatGroupMode(), 0d, 0, "", 0, plnParameter.getPlanForWeek(), plnParameter.getMaxTime(),
                kmeanMultiLoop, distDp, distributor.getTravellingSpeed(), plnParameter.getMinTime(), 0);

        int[] outletsArr = new int[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            outletsArr[i] = i;
        }
        kMeanJCA(jca, distributor, outletsArr);

        return jca;
    }

    /****
     * Used divide cluster into sub-cluster until each sub-cluster size is no less
     * than MinClusterSize
     *
     * @param jcas
     * @param noOfPointsInCluster
     * @param plnParameter
     * @param distributor
     * @param distDp
     * @param minClusterSize
     * @param clusterLst
     * @param flag
     * @return
     */
    private List<Cluster> recurseKmean(List<JCA> jcas, int noOfPointsInCluster, PlanParameter plnParameter,
                                              Distributor distributor, Outlet distDp, int minClusterSize, List<Cluster> clusterLst, boolean flag) {

        List<Cluster> jcasWith = clusterLst;

        for (int j = 0; j < jcas.size(); j++) {
            for (int i = 0; i < jcas.get(j).clusters.length; i++) {
                if (jcas.get(j).clusters[i].getDataPoints().size() > plnParameter.getMinClusterSize()) {

                    JCA currJca = createKmeanCluster(jcas.get(j).clusters[i].getmDataPoints(),
                            jcas.get(j).clusters[i].getmDataPoints().size(), plnParameter, distributor, distDp,
                            plnParameter.getMinClusterSize(), flag);

                    List<JCA> currList = new ArrayList<>();
                    currList.add(currJca);
                    try {
                        recurseKmean(currList, noOfPointsInCluster, plnParameter, distributor, distDp,
                                plnParameter.getMinClusterSize(), jcasWith, flag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    jcasWith.add(jcas.get(j).clusters[i]);
                }
            }
        }
        return jcasWith;
    }

    /****
     * used to filter out cluster with zero outlet
     *
     * @param jcas
     * @param jca
     * @param plnParameter
     * @param distributionFlag
     * @return
     */
    private List<ClusterDetails> filterZeroOutletCluster(List<JCA> jcas, JCA jca, PlanParameter plnParameter,
                                                         boolean distributionFlag) {

        ClusterDetails temp;
        List<ClusterDetails> kmeanClusterDetails = new ArrayList<ClusterDetails>();
        int count = 0;
        Outlet prevDP = null;
        for (int j = 0; j < jcas.size(); j++) {
            prevDP = null;
            for (int i = 0; i < jcas.get(j).clusters.length; i++) {

                if (distributionFlag) {

                    temp = new ClusterDetails(count, jcas.get(j).clusters[i].getDataPoints(),
                            plnParameter.getMaxFreqency(), jca, jcas.get(j).clusters[i].getCentroid(), prevDP);

                } else {

                    temp = new ClusterDetails(count, jcas.get(j).clusters[i].getDataPoints(),
                            plnParameter.getMaxFreqency(), plnParameter.getPlanForWeek(), jca,
                            jcas.get(j).clusters[i].getCentroid(), prevDP);

                }

                if (temp.getNoOfDataPoint() > 0) {
                    kmeanClusterDetails.add(temp);
                    count++;
                }
            }

        }
        return kmeanClusterDetails;
    }

    /****
     * used to get number cluster to be created
     *
     * @param loopCountDividsor
     * @param sp
     * @param dataPointSize
     * @param beatGroupMode
     * @param kmeanMultiLoop
     * @param mainCluster
     * @return
     */
    private int getLoopCount(int loopCountDividsor, int sp, int dataPointSize, String beatGroupMode,
                             double kmeanMultiLoop, boolean mainCluster, int clusterLoop) {

        int loopCount = 1;

        if (mainCluster) {
            loopCount = sp;
        } else {

            loopCount = (int) Math.ceil(dataPointSize / kmeanMultiLoop);

            if (beatGroupMode.equalsIgnoreCase("Salesperson")) {

                int threshold = (int) Math.round((double) clusterLoop / sp);
                if (loopCount > threshold) {
                    loopCount = threshold;
                }
            } else {
                int threshold = (int) Math.round(2.0 * clusterLoop / sp);
                if (loopCount > threshold) {
                    loopCount = threshold;
                }
            }
        }
        return loopCount > 0 ? loopCount : 1;
    }

    /****
     *
     * @param jca
     * @param distributor
     * @param outletsArr
     */
    private void kMeanJCA(JCA jca, Distributor distributor, int[] outletsArr) {
        jca.createClustersNew(outletsArr);
        jca.startAnalysisNew();
    }
}

