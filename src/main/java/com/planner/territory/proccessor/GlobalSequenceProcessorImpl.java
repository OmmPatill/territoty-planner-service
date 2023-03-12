package com.planner.territory.proccessor;

import com.planner.territory.model.globalsequence.*;
import com.planner.territory.proccessor.globalsequence.processor.DistanceProcessor;
import com.planner.territory.proccessor.globalsequence.processor.LinearDistance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GlobalSequenceProcessorImpl implements GlobalSequenceProcessor{

    private static final long serialVersionUID = 1L;

    @Autowired
    @Qualifier(value = "DistanceFinder")
    DistanceProcessor distanceProcessor;

    @Autowired
    LinearDistance linearDistance;

    @Autowired
    GlobalSequenceConstants constants;

    @Override
    public GlobalSequenceOutputDataModel process(GlobalSequenceInputDataModel globalSequenceFinderModel)
            throws Exception {

        List<Customer> customerList = globalSequenceFinderModel.getCustomerList();
        // we can change location list as per nearest location is as start point
        Customer startPoint = globalSequenceFinderModel.getStartPoint();
        double[][] allDistance;
        int[] outlets = new int[customerList.size()];
        int[] nnoutlets = new int[customerList.size()];
        int[] visited = new int[customerList.size()];
        if (startPoint == null) {
            log.info("Start Location not found...Please Give Start location if require");
        }
        checkstartLocation(startPoint, customerList);
        List<Customer> distanceCust = new ArrayList<>(customerList);//Lists.mutable.ofAll(customerList);
        if (startPoint != null) {
            distanceCust.add(startPoint);
        }
        long disstart = System.currentTimeMillis();
        DistanceProcessorInput distanceProcessorInput = new DistanceProcessorInput(distanceCust,
                globalSequenceFinderModel.getDistanceType(), globalSequenceFinderModel.getAllDistance());
        DistanceProcessorOutput distanceProcessorOutput = distanceProcessor.process(distanceProcessorInput);
        allDistance = distanceProcessorOutput.getAllDistance();
        long disend = System.currentTimeMillis();
        log.info("Distance Processor Elapsed Time in seconds: " + (disend - disstart) / 1000);
        for (int i = 0; i < customerList.size(); i++) {
            outlets[i] = -1;
            visited[i] = -1;
        }

        IterateDetails details = new IterateDetails(customerList.size(), Double.MAX_VALUE, 0, 0, 0, 0.0, 0.0, visited);
        long start = System.currentTimeMillis();
        sequencingAlgorithms(customerList, allDistance, outlets, details, nnoutlets);
        calculateFinalDistance(startPoint, customerList, allDistance, outlets, details,
                distanceProcessorOutput.getAllDistanceMap());
        long end = System.currentTimeMillis();
        log.info("Sequencing Algorithm Elapsed Time in seconds: " + (end - start) / 1000);
        GlobalSequenceOutputDataModel global_sequence = setGlobalSequence(customerList, outlets, allDistance, startPoint,
                distanceCust, distanceProcessorOutput.getAllDistanceMap());//Lists.immutable.ofAll(distanceCust)

        return global_sequence;
    }

    /***
     * check start point and add distance between start point to first location to
     *   final distance
     * @param startPoint
     * @param customerList
     * @param allDistance
     * @param outlets
     * @param details
     * @param allDistanceMap
     * @throws Exception
     */
    private void calculateFinalDistance(Customer startPoint, List<Customer> customerList, double[][] allDistance,
                                        int[] outlets, IterateDetails details, Map<String, Double> allDistanceMap)
            throws Exception {
        try {
            double distance_final = 0;
            if (startPoint != null)
                distance_final = allDistanceMap
                        .get(startPoint.getCustomerId() + ":" + customerList.get(0).getCustomerId())
                        + getDistance(allDistance, outlets, details, customerList);
            else
                distance_final = getDistance(allDistance, outlets, details, customerList);
            log.info("final Sequence distance from startpoint " + distance_final);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     * run all Sequencing algorithm And Find Optimum Sequence
     *
     * @param customerList
     * @param allDistance
     * @param outlets
     * @param details
     * @param nnoutlets
     * @throws Exception
     */
    private void sequencingAlgorithms(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                      IterateDetails details, int[] nnoutlets) throws Exception {

        nearestneighbour(customerList, allDistance, outlets, details);

        log.info("nearestneighbour ");
        log.info(Arrays.toString(outlets) + "");
        log.info("Distance  is " + getDistance(allDistance, outlets, details, customerList));

        kopt1(customerList, allDistance, outlets, nnoutlets, details);

        log.info("After kopt1");
        log.info(Arrays.toString(outlets) + "");
        log.info("Distance  is " + getDistance(allDistance, outlets, details, customerList));

        kopt2(customerList, allDistance, outlets, nnoutlets, details);

        log.info("After kopt2");
        log.info(Arrays.toString(outlets) + "");
        log.info("Distance  is " + getDistance(allDistance, outlets, details, customerList));

        kopt3(allDistance, outlets, details.getTotalPoints(), customerList, details);

        log.info("After kopt3");
        log.info(Arrays.toString(outlets) + "");
        log.info("Distance  is " + getDistance(allDistance, outlets, details, customerList));

        kopt4(allDistance, outlets, details.getTotalPoints(), customerList, details);

        log.info("After kopt4");
        log.info(Arrays.toString(outlets) + "");
        log.info("Distance  is " + getDistance(allDistance, outlets, details, customerList));

    }

    /***
     * Check start location if start location present then find nearest location
     *
     * @param startPoint
     * @param customerList
     */
    private void checkstartLocation(Customer startPoint, List<Customer> customerList) {

        if (startPoint != null) {
            double NearestDist = Double.MAX_VALUE;
            int NearestOutlet = 0;
            for (int j = 0; j < customerList.size(); j++) {
                if (linearDistance.calculateLinearDistance(startPoint, customerList.get(j)) < NearestDist) {
                    NearestDist = linearDistance.calculateLinearDistance(startPoint, customerList.get(j));
                    NearestOutlet = j;
                }
            }
            Customer temp = customerList.get(0);
            customerList.set(0, customerList.get(NearestOutlet));
            customerList.set(NearestOutlet, temp);
        }
    }

    /***
     *
     * @param allDistance  = all location distance from location - to location
     * @param outlets      = sequence of location
     * @param details      = Iteration execution information details
     * @param customerList
     * @return
     * @throws Exception
     */
    private long getDistance(double[][] allDistance, int[] outlets, IterateDetails details,
                             List<Customer> customerList) throws Exception {
        try {
            double totaldist = 0.0;
            for (int i = 1; i < details.getTotalPoints(); i++) {
                totaldist += allDistance[outlets[i - 1]][outlets[i]];
            }
            return (long) totaldist;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     *
     * @param customerList  = list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param nnoutlets=copy of outlets Previous location Sequence
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void kopt2(List<Customer> customerList, double[][] allDistance, int[] outlets, int[] nnoutlets,
                       IterateDetails details) throws Exception {

        for (int r = 0; r < constants.getK_OPT_METHOD1_ITERATION(); r++) {
            iteratedTotalpoint2(customerList, allDistance, outlets, nnoutlets, details);
            log.info(
                    "Distance After Iteration " + r + " : " + getDistance(allDistance, outlets, details, customerList));
        }
    }

    /***
     * Iterated on basis of Number of outlets (method for k_opt_method1)
     *
     * @param customerList = list of locations
     * @param allDistance   = all location distance from location - to location
     * @param outlets       = sequence of location and maintain updated sequence
     * @param nnoutlets     = copy of outlets Previous location Sequence
     * @param details       = Iteration execution information details
     * @throws Exception
     */

    private void iteratedTotalpoint2(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                     int[] nnoutlets, IterateDetails details) throws Exception {

        for (int i = 0; i < details.getTotalPoints(); i++) {
            nnoutlets[i] = outlets[i];
            outlets[i] = -1;
            details.setVisitedIndex(i, 1);
        }
        details.setOlddist(0.0);
        details.setNearestDist(0.0);
        for (int i = 0; i < details.getTotalPoints(); i++) {
            if (i == 0) {
                outlets[0] = nnoutlets[0];
                details.setVisitedIndex(0, -1);
                details.setNearestOutlet(nnoutlets[0]);
            } else {
                getNextNearestKOptMethod2(customerList, allDistance, outlets, nnoutlets, i, details);
            }
        }

    }

    /***
     * If outlet is not first outlet (method for k_opt_method2)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param i              = Total points iteration count current value
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void getNextNearestKOptMethod2(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                           int[] nnoutlets, int i, IterateDetails details) throws Exception {

        details.setNearestDist(Double.MAX_VALUE);
        findNearestOutletKOptMethod2(customerList, allDistance, nnoutlets, details);
        details.setInsertionPoint(i);
        if (i != 1) {
            checkWithModificationDistanceKOptMethod2(customerList, allDistance, outlets, details, i);
        } else {
            details.setNearestDist(allDistance[details.getNearestOutlet()][details.getNextNearestOutlet()]);
            details.setNearestDist(0.0);
        }
        if (details.getInsertionPoint() > -1)
            outlets[details.getInsertionPoint()] = details.getNextNearestOutlet();

        details.setNearestOutlet(outlets[i]);
        details.setTotaldist(details.getTotaldist() - details.getOlddist() + details.getNearestDist());
    }

    /***
     * If Iteration index not equal to 1 (method for k_opt_method2)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param details        = Iteration execution information details
     * @param i              = Total points iteration count,current value
     */
    private void checkWithModificationDistanceKOptMethod2(List<Customer> customerList, double[][] allDistance,
                                                          int[] outlets, IterateDetails details, int i) {

        details.setNearestDist(Double.MAX_VALUE);
        iterateOutletExceptNNOKOptMethod2(customerList, allDistance, outlets, details, i);
        double addDist = allDistance[details.getNearestOutlet()][details.getNextNearestOutlet()];

        if (addDist < details.getNearestDist()) {
            details.setNearestDist(addDist);
            details.setInsertionPoint(i);
        }
        reArrageOutletsKOptMethod2(outlets, details, i);
    }

    /***
     * Insert and arrange outlets from nearest insertionPoint to NNO (method for
     * k_opt_method2)
     *
     * @param outlets = sequence of location and maintain updated sequence
     * @param details = Iteration execution information details
     * @param i       Total points iteration count,current value
     */

    private void reArrageOutletsKOptMethod2(int[] outlets, IterateDetails details, int i) {

        int p;
        if (details.getInsertionPoint() > -1) {
            for (p = 0; p < details.getTotalPoints(); p++) {
                if (details.getNextNearestOutlet() == outlets[p]) {
                    break;
                }
            }
            if (p < details.getInsertionPoint()) {
                for (int k = p; k < details.getInsertionPoint() - 1; k++) {
                    outlets[k] = outlets[k + 1];
                }
                details.setInsertionPoint(details.getInsertionPoint() - 1);
            } else {
                for (int k = i; k > details.getInsertionPoint(); k--) {
                    outlets[k] = outlets[k - 1];
                }
            }
        }
    }

    /***
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param details        = Iteration execution information details
     * @param i              Total points iteration count,current value
     */
    private void iterateOutletExceptNNOKOptMethod2(List<Customer> customerList, double[][] allDistance,
                                                   int[] outlets, IterateDetails details, int i) {
        double addDist;
        for (int k = 1; k < i; k++) {
            if (outlets[k] != -1) {
                addDist = allDistance[outlets[k - 1]][details.getNextNearestOutlet()]
                        + allDistance[details.getNextNearestOutlet()][outlets[k]]
                        - allDistance[outlets[k - 1]][outlets[k]];
                if (addDist < details.getNearestDist()) {
                    details.setNearestDist(addDist);
                    details.setInsertionPoint(k);
                }
            }
        }
    }

    /***
     * Find closest outlet to nearest Outlet (for k_opt_method2)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void findNearestOutletKOptMethod2(List<Customer> customerList, double[][] allDistance,
                                              int[] nnoutlets, IterateDetails details) throws Exception {
        try {
            int jvisit = -1;
            int lastvisit = 0;

            for (int j = 0; j < nnoutlets.length; j++) {
                if (details.getNearestOutlet() != nnoutlets[j] && details.getVisitedIndex(j) != -1
                        && (allDistance[details.getNearestOutlet()][nnoutlets[j]] < details.getNearestDist())) {
                    details.setNearestDist(allDistance[details.getNearestOutlet()][nnoutlets[j]]);

                    details.setNextNearestOutlet(nnoutlets[j]);// nnoutlets[j]
                    jvisit = j;
                } else if (details.getNearestOutlet() == nnoutlets[j]) {
                    lastvisit = j;
                }
            }
            if (jvisit != -1) {
                details.setVisitedIndex(jvisit, -1);

            } else {
                details.setNextNearestOutlet(lastvisit);
                details.setVisitedIndex(lastvisit, -1);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     * Find next nearest neighbor and compare the next nearest neighbor distance
     * with current point distance Iterate as mention count
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param details        = Iteration execution information details
     * @throws Exception
     */

    private void kopt1(List<Customer> customerList, double[][] allDistance, int[] outlets, int[] nnoutlets,
                       IterateDetails details) throws Exception {

        for (int r = 0; r < constants.getK_OPT_METHOD1_ITERATION(); r++) {
            iteratedTotalpoint(customerList, allDistance, outlets, nnoutlets, details);
            log.info(
                    "Distance After Iteration " + r + " : " + getDistance(allDistance, outlets, details, customerList));
        }
    }

    /***
     * Iterated on basis of Number of outlets (method for k_opt_method1)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void iteratedTotalpoint(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                    int[] nnoutlets, IterateDetails details) throws Exception {

        for (int i = 0; i < details.getTotalPoints(); i++) {
            nnoutlets[i] = outlets[i];
            details.setVisitedIndex(i, 1);
        }
        details.setOlddist(0.0);
        details.setNearestDist(0.0);
        for (int i = 0; i < details.getTotalPoints(); i++) {
            if (i == 0) {
                outlets[0] = nnoutlets[0];
                details.setVisitedIndex(0, -1);
                details.setNearestOutlet(nnoutlets[0]);
            } else {
                getNextNearestKOptMethod1(customerList, allDistance, outlets, nnoutlets, i, details);
            }
        }
    }

    /***
     * If outlet is not first outlet (method for k_opt_method1)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param i              Total points iteration count,current value
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void getNextNearestKOptMethod1(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                           int[] nnoutlets, int i, IterateDetails details) throws Exception {

        details.setNearestDist(Double.MAX_VALUE);
        int jvisit = findNearestOutletKOptMethod1(customerList, allDistance, nnoutlets, details);
        if (i != 1) {
            checkWithModificationDistanceKOptMethod1(customerList, allDistance, outlets, jvisit, details, i);
        } else {
            details.setNearestDist(allDistance[outlets[details.getTotalPoints() - 1]][details.getNextNearestOutlet()]);
            details.setNearestDist(0.0);
        }
        if (details.getInsertionPoint() > -1)
            outlets[details.getInsertionPoint()] = details.getNextNearestOutlet();

        details.setNearestOutlet(details.getNextNearestOutlet());
        details.setTotaldist(details.getTotaldist() - details.getOlddist() + details.getNearestDist());
    }

    /***
     * If Iteration index not equal to 1 (method for k_opt_method1)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param jvisit         = index of next nearest outlet
     * @param details        = Iteration execution information details
     * @param i              Total points iteration count,current value
     * @throws Exception
     */
    private void checkWithModificationDistanceKOptMethod1(List<Customer> customerList, double[][] allDistance,
                                                          int[] outlets, int jvisit, IterateDetails details, int i) throws Exception {

        calculateNearestDist(customerList, allDistance, outlets, jvisit, details);
        details.setOlddist(details.getNearestDist());
        iterateOutletExceptNNO(customerList, allDistance, outlets, details);
        double addDist = allDistance[outlets[details.getTotalPoints() - 1]][details.getNextNearestOutlet()];

        if (addDist < details.getNearestDist()) {
            details.setInsertionPoint(i);
            details.setNearestDist(addDist);
        }
        reArrageOutletsKOptMethod1(outlets, details);
    }

    /***
     * Insert and arrange outlets from nearest insertionPoint to NNO (method for
     * k_opt_method1)
     *
     * @param outlets = sequence of location and maintain updated sequence
     * @param details = Iteration execution information details
     */
    private void reArrageOutletsKOptMethod1(int[] outlets, IterateDetails details) {
        int p;
        if (details.getInsertionPoint() > -1) {
            for (p = 0; p < details.getTotalPoints(); p++) {
                if (details.getNextNearestOutlet() == outlets[p]) {
                    break;
                }
            }
            if (p < details.getInsertionPoint()) {
                for (int k = p; k < details.getInsertionPoint() - 1; k++) {
                    outlets[k] = outlets[k + 1];
                }
                details.setInsertionPoint(details.getInsertionPoint() - 1);
            } else {
                for (int k = p; k > details.getInsertionPoint(); k--) { // for(int k=i; k>insertionPoint; k--){//
                    outlets[k] = outlets[k - 1];
                }
            }
        }
    }

    /***
     * outlets are iterated except next nearest outlet is current outlet or previous
     * outlet (method for k_opt_method1)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param details        = Iteration execution information details
     */
    private void iterateOutletExceptNNO(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                        IterateDetails details) {
        double addDist;
        for (int k = 1; k < details.getTotalPoints(); k++) { // for(int k=1; k<i; k++){ //
            if (details.getNextNearestOutlet() != outlets[k] && details.getNextNearestOutlet() != outlets[k - 1]) {// (outlets[k]!=-1){//

                addDist = allDistance[outlets[k - 1]][details.getNextNearestOutlet()]
                        + allDistance[details.getNextNearestOutlet()][outlets[k]]
                        - allDistance[outlets[k - 1]][outlets[k]];
                if (addDist < details.getNearestDist()) {
                    details.setNearestDist(addDist);
                    details.setInsertionPoint(k);
                }
            }
        }
    }

    /***
     * calculate nearest distance if visited outlet + 1 id less then total outlets
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param outlets        = sequence of location and maintain updated sequence
     * @param jvisit
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void calculateNearestDist(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                      int jvisit, IterateDetails details) throws Exception {
        details.setNearestDist(Double.MAX_VALUE);
        try {
            if (jvisit + 1 < details.getTotalPoints()) {
                details.setNearestDist(allDistance[outlets[jvisit - 1]][details.getNextNearestOutlet()]
                        + allDistance[details.getNextNearestOutlet()][outlets[jvisit + 1]]
                        - allDistance[outlets[jvisit - 1]][outlets[jvisit + 1]]);
            } else {
                details.setNearestDist(allDistance[outlets[jvisit - 1]][details.getNextNearestOutlet()]);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     * Find closest outlet to nearest Outlet (for k_opt_method1)
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param nnoutlets      = copy of outlets Previous location Sequence
     * @param details        = Iteration execution information details
     * @return
     * @throws Exception
     */
    private int findNearestOutletKOptMethod1(List<Customer> customerList, double[][] allDistance,
                                             int[] nnoutlets, IterateDetails details) throws Exception {

        try {
            int jvisit = 0;
            for (int j = 0; j < nnoutlets.length; j++) {
                if (details.getNearestOutlet() != nnoutlets[j] && details.getVisitedIndex(j) != -1
                        && (allDistance[details.getNearestOutlet()][nnoutlets[j]] < details.getNearestDist())) {
                    details.setNearestDist(allDistance[details.getNearestOutlet()][nnoutlets[j]]);
                    details.setNextNearestOutlet(nnoutlets[j]);
                    jvisit = j;
                }
            }
            details.setVisitedIndex(jvisit, -1);
            details.setInsertionPoint(-1);
            return jvisit;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     *
     * @param customerList = list of location
     * @param allDistance   = all location distance from location - to location
     * @param outlets       = sequence of location and maintain updated sequence
     * @param details       = Iteration execution information details
     * @throws Exception
     */

    private void nearestneighbour(List<Customer> customerList, double[][] allDistance, int[] outlets,
                                  IterateDetails details) throws Exception {

        for (int i = 0; i < details.getTotalPoints(); i++) {
            if (i == 0) {
                outlets[0] = 0;
                details.setVisitedIndex(0, 1);
                details.setNearestOutlet(0);
            } else {
                details.setNearestDist(Double.MAX_VALUE);
                getNearestOutlet(customerList, allDistance, details);
                details.setTotaldist(details.getTotaldist() + details.getNearestDist());
                details.setVisitedIndex(details.getNextNearestOutlet(), 1);
                outlets[i] = details.getNextNearestOutlet();
                details.setNearestOutlet(details.getNextNearestOutlet());
            }
        }
    }

    /***
     *
     * @param customerList= list of locations
     * @param allDistance    = all location distance from location - to location
     * @param details        = Iteration execution information details
     * @throws Exception
     */
    private void getNearestOutlet(List<Customer> customerList, double[][] allDistance, IterateDetails details)
            throws Exception {
        try {
            for (int j = 0; j < details.getTotalPoints(); j++) {
                if (details.getNearestOutlet() != j && details.getVisitedIndex(j) == -1
                        && (allDistance[details.getNearestOutlet()][j] < details.getNearestDist())) {
                    details.setNearestDist(allDistance[details.getNearestOutlet()][j]);
                    details.setNextNearestOutlet(j);
                }
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /***
     * All outlet are iterated and compare with each other and with neighbour
     * outlets Two edge distance swapping. .
     *
     * @param allDistance  = all location distance from location - to location
     * @param customerList
     * @param outlets      = sequence of location and maintain updated sequence
     * @param totalPoints  = Iteration execution information details
     * @param details
     * @param customerList
     * @throws Exception
     */
    private void kopt4(double[][] allDistance, int[] outlets, int totalPoints, List<Customer> customerList,
                       IterateDetails details) throws Exception {

        for (int r = 0; r < 200; r++) {
            double cdist[] = new double[] { 0, 0, 0, 0, 0 };
            iterateTargeteOutletskOptMethod4(allDistance, outlets, totalPoints, cdist);
        }

    }

    /***
     * All outlet are iterated and compare with each other
     *
     * @param allDistance
     * @param outlets
     * @param cdist
     * @param totalPoints
     * @param cdist
     */
    private void iterateTargeteOutletskOptMethod4(double[][] allDistance, int[] outlets, int totalPoints,
                                                  double[] cdist) {
        for (int i = 0; i < totalPoints - 2; i++) {
            iterateComparatorOutletskOptMethod4(allDistance, outlets, cdist, totalPoints, i);
        }
    }

    /***
     * check with other outlets and with neighbor outlets .
     *
     * @param allDistance
     * @param outlets
     * @param cdist
     * @param totalPoints
     * @param i
     * @param cdist
     */
    private void iterateComparatorOutletskOptMethod4(double[][] allDistance, int[] outlets, double[] cdist,
                                                     int totalPoints, int i) {
        for (int j = i + 2; j < totalPoints; j++) {
            if (j < totalPoints - 1) {
                cdist[2] = allDistance[outlets[j]][outlets[j + 1]];
                cdist[4] = allDistance[outlets[i + 1]][outlets[j + 1]];
            } else {
                cdist[2] = 0;
                cdist[4] = 0;
            }
            cdist[1] = allDistance[outlets[i]][outlets[i + 1]];
            cdist[3] = allDistance[outlets[i]][outlets[j]];
            if (cdist[1] + cdist[2] > cdist[3] + cdist[4]) {
                int tempPoint = outlets[j];
                outlets[j] = outlets[i + 1];
                outlets[i + 1] = tempPoint;
            }
        }
    }

    /***
     * All outlet are iterated and compare with each other and with neighbour
     * outlets Four points distance swapping.
     *
     * @param allDistance  = all location distance from location - to location
     * @param outlets      = sequence of location and maintain updated sequence
     * @param totalPoints  = Iteration execution information details
     * @param customerList
     * @param details
     * @throws Exception
     */

    private void kopt3(double[][] allDistance, int[] outlets, int totalPoints, List<Customer> customerList,
                       IterateDetails details) throws Exception {

        for (int r = 0; r < 200; r++) {
            iterateTargeteOutletskOptMethod3(allDistance, outlets, totalPoints);

        }

    }

    /***
     * All outlet are iterated and compare with each other
     *
     * @param allDistance
     * @param outlets
     * @param totalPoints
     */
    private void iterateTargeteOutletskOptMethod3(double[][] allDistance, int[] outlets, int totalPoints) {

        for (int i = 1; i < totalPoints - 1; i++) {
            iterateComparatorOutletskOptMethod3(allDistance, outlets, totalPoints, i);
            int j = totalPoints - 1;
            checkOutletAndSwap(
                    allDistance[outlets[i]][outlets[j - 1]] + 0 + allDistance[outlets[j]][outlets[i - 1]]
                            + allDistance[outlets[j]][outlets[i + 1]],
                    allDistance[outlets[i]][outlets[i - 1]] + allDistance[outlets[i]][outlets[i + 1]]
                            + allDistance[outlets[j]][outlets[j - 1]] + 0,
                    i, j, outlets);
        }
    }

    /***
     * check with other outlets and with neighbor outlets .
     *
     * @param allDistance
     * @param outlets
     * @param totalPoints
     * @param i
     */
    private void iterateComparatorOutletskOptMethod3(double[][] allDistance, int[] outlets, int totalPoints, int i) {

        for (int j = i + 1; j < totalPoints - 1; j++) {
            checkOutletAndSwap(
                    allDistance[outlets[i]][outlets[j - 1]] + allDistance[outlets[i]][outlets[j + 1]]
                            + allDistance[outlets[j]][outlets[i - 1]] + allDistance[outlets[j]][outlets[i + 1]],
                    allDistance[outlets[i]][outlets[i - 1]] + allDistance[outlets[i]][outlets[i + 1]]
                            + allDistance[outlets[j]][outlets[j - 1]] + allDistance[outlets[j]][outlets[j + 1]],
                    i, j, outlets);
        }
    }

    /***
     * Swap Given position i & j in outlets
     *
     * @param v1
     * @param v2
     * @param i
     * @param j
     * @param outlets
     */
    private void checkOutletAndSwap(double v1, double v2, int i, int j, int[] outlets) {
        if (v1 < v2) {
            int tempPoint = outlets[j];
            outlets[j] = outlets[i];
            outlets[i] = tempPoint;
        }
    }

    /***
     *
     * @param customerList  = list of location
     * @param outlets        = sequence of location
     * @param allDistance    = all location distance from location - to location
     * @param startPoint
     * @param distanceCust
     * @param allDistanceMap
     * @return GlobalSequenceOutputModel = output sequence object for write excel
     *         file
     * @throws Exception
     *
     */
    private GlobalSequenceOutputDataModel setGlobalSequence(List<Customer> customerList, int[] outlets,
                                                        double[][] allDistance, Customer startPoint, List<Customer> distanceCust,
                                                        Map<String, Double> allDistanceMap) throws Exception {

        List<GlobalVisitSequence> global_sequence = new ArrayList<GlobalVisitSequence>();
        try {
            double distance = 00;
            double totalDistance = 00;
            if (startPoint != null) {
                distance = allDistanceMap.get(startPoint.getCustomerId() + ":" + customerList.get(0).getCustomerId());
                totalDistance += distance;
            }
            int sequence_count = 1;
            for (int i = 0; i < outlets.length; i++) {
                Customer customer = customerList.get(outlets[i]);
                if (i != 0) {
                    distance = allDistance[outlets[i - 1]][outlets[i]];
                    totalDistance += distance;
                }
                GlobalVisitSequence global_visit_sequence = new GlobalVisitSequence(customer.getCustomerId(),
                        customer.getCustomerCode(), customer.getLatitude(), customer.getLongitude(), sequence_count++,
                        distance);
                global_sequence.add(global_visit_sequence);
            }
            GlobalSequenceOutputDataModel Glob_sequence = new GlobalSequenceOutputDataModel(
                    global_sequence, allDistanceMap, distanceCust, totalDistance);//Lists.immutable.ofAll(global_sequence)
            return Glob_sequence;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

}
