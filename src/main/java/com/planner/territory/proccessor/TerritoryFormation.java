package com.planner.territory.proccessor;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.planner.territory.model.globalsequence.Customer;
import com.planner.territory.model.globalsequence.GlobalSequenceInputDataModel;
import com.planner.territory.model.globalsequence.GlobalSequenceOutputDataModel;
import com.planner.territory.model.globalsequence.GlobalVisitSequence;
import com.planner.territory.model.kmean.Cluster;
import com.planner.territory.model.kmean.ClusterDetails;
import com.planner.territory.model.pjp.Distributor;
import com.planner.territory.model.pjp.Outlet;
import com.planner.territory.model.pjp.PlanParameter;
import com.planner.territory.model.pjp.Territory;
import com.planner.territory.model.pjp.data.TerritoryPlannerInputDataModel;
import com.planner.territory.model.pjp.data.TerritoryPlannerOutputDataModel;
import org.apache.commons.lang3.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/****
 *
 * The TerritoryFormation implemets all territory formation methods
 *
 */
@Component
public class TerritoryFormation {

    @Autowired
    KeanCalculator meanCalculator;

    @Autowired
    GlobalSequenceProcessorCaller globalSequenceProcessorCaller;
    @Autowired
    TimeBasedTerritoryFormation timeBasedTerritoryFormation;
    @Autowired
    OutletBasedTerritoryFormation outletBasedTerritoryFormation;

    @Autowired
    ValueBasedTerritoryFormation valueBasedTerritoryFormation;

    @Autowired
    OutlierSkipper outlierSkipper;

    @Autowired
    GlobalSequenceProcessor globalSequenceProcessor;

    double minDist = Double.MAX_VALUE;
    int minDistIndex = 0;

    String invalidData[] ;

    /****
     * Method used to create territory based on plan type
     *
     * @param model
     * @param outletList
     * @param distLst
     * @param noOfSalesPersons
     * @param plnParameter
     * @param distributionFlag
     * @param outputModel
     * @return
     * @throws Exception
     */
    public List<Territory> formTerritory(TerritoryPlannerInputDataModel model, List<Outlet> outletList,
                                         List<Distributor> distLst, int noOfSalesPersons, PlanParameter plnParameter,
                                         boolean distributionFlag, TerritoryPlannerOutputDataModel outputModel) throws Exception {

        Distributor distributor = new Distributor();
        List<Territory> territoryLst = new ArrayList<>();

        invalidData = new String[outletList.size() + 1];

        for (int i = 0; i < distLst.size(); i++) {

            distributor = distLst.get(i);

            /***
             * get list of outlets for current distributor
             */
            String distributorcode = distributor.getDistributorCode();
            List<Outlet> outletLst = outletList.stream()
                    .filter(o -> o.getDistributorCode().equalsIgnoreCase(distributorcode))
                    .collect(Collectors.toList());
            /***
             * get maximum frequency from selected distributor outlets
             */
            if (outletLst != null && !outletLst.isEmpty()) {

                if (distributionFlag) {
                    Optional<Outlet> max = outletLst.stream().max(Comparator.comparing(Outlet::getCallFrequency));

                    plnParameter.setMaxFreqency(max.get().getCallFrequency() == 16
                            ? plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()
                            : max.get().getCallFrequency());

                    plnParameter.setMaxFreqency(plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek());

                    int maxFreq = plnParameter.getMaxFreqency();
                    /***
                     * set multiplier in outlet data callfrequency of each outlets/maxFrequency
                     */
                    outletLst.stream().peek(o -> o.setCallFrequencyMultiplier((double) o.getCallFrequency() / maxFreq))
                            .collect(Collectors.toList());

                } else {
                    Optional<Outlet> max = outletLst.stream().max(Comparator.comparing(Outlet::getVisitFrequency));

                    plnParameter.setMainMaxFrequency(max.get().getVisitFrequency());

                    Set<Integer> uniqueFrequency = new HashSet<Integer>();
                    outletLst.stream().filter(p -> uniqueFrequency.add(p.getVisitFrequency())).collect(Collectors.toList());

                    plnParameter.setMaxFreqency(max.get().getVisitFrequency() == 16 ? plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek() : max.get().getVisitFrequency());

                    if(plnParameter.getMaxFreqency() == 12 && uniqueFrequency.contains(8)) {
                        plnParameter.setMaxFreqency(plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek());
                    }


					/*if (plnParameter.getMaxFreqency() == 2) {
						plnParameter.setMaxFreqency(plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek());
					}*/

                    if (plnParameter.getWorkDayInWeek() == 5 && plnParameter.getMaxFreqency() == 8) {
                        plnParameter.setMaxFreqency(plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek());
                    }

//					plnParameter.setMaxFreqency(24);
                    int maxFreq = plnParameter.getMaxFreqency();

                    /***
                     * get & set noOfBeat from max frequency
                     */
                    int noOfBeat = calNoOfBeat(maxFreq);

                    plnParameter.setNoOfBeat(noOfBeat);

                    /***
                     * set multiplier in outlet data visitfrequency of each outlets/maxFrequency
                     */
                    outletLst.stream().peek(o -> o.setMultiplier((double) o.getVisitFrequency() / maxFreq))
                            .collect(Collectors.toList());
                }

            } else {
                throw new Exception("Outlet data not found for given Distributor: " + distributorcode);
            }

            /***
             * four corner points creation logic
             */

            /**
             * Vector<Outlet>[] dataPointsNew = (Vector<Outlet>[]) new Vector[4];
             *
             * createFourCornerDataPoint(dataPointsNew, outletLst);
             *
             */

            List<List<GlobalVisitSequence>> outlets = new ArrayList<List<GlobalVisitSequence>>();

            /***
             * sort outlets by using four corner points
             */
            /**
             * sortUsingGlobalsequence(plnParameter, dataPointsNew, distributor, model,
             * outlets);
             **/

            /***
             * convert global sequence objects to original beat planner outlet object
             */
            /***
             * Map<Integer, GlobalVisitSequence> globalSeqMap =
             * Optional.ofNullable(outlets.get(minDistIndex))
             * .orElse(Lists.mutable.empty()).stream()
             * .collect(Collectors2.toMap(GlobalVisitSequence::getCustomerId, c -> c));
             *
             * outletLst.stream().forEach(o -> { GlobalVisitSequence g =
             * globalSeqMap.get(o.getOutletID()); o.setVisitSequence(g.getVisitSequence());
             * o.setDistance(g.getDistance()); });
             */

            /****
             * sort outlet visit sequence wise
             */
            /**
             * outletLst.sort((Outlet s1, Outlet s2) -> s1.getVisitSequence() -
             * s2.getVisitSequence());
             */

            /***
             * Identify the no of salesperson
             */
            double totalValue = 0;
            double totalTime = 0;

            if (distributionFlag) {
                totalTime = outletLst.stream().collect(Collectors.summingDouble(outlet -> outlet.getCallDuration()*outlet.getCallFrequencyMultiplier()));

                noOfSalesPersons = identifySalesPerson(plnParameter, outletLst, totalValue, totalTime, noOfSalesPersons, distributionFlag);
            } else {
                totalValue = outletLst.stream().collect(Collectors.summingDouble(outlet -> outlet.getAvgTurnover() * outlet.getMultiplier()));

                totalTime = outletLst.stream().collect(Collectors.summingDouble(outlet -> outlet.getVisitDuration() * outlet.getMultiplier()));

                noOfSalesPersons = identifySalesPerson(plnParameter, outletLst, totalValue, totalTime, noOfSalesPersons, distributionFlag);
            }

            /***
             * set calculated salesperson in plan parameter for reuse
             */
            plnParameter.setNoOfSalesperson(noOfSalesPersons);

            /***
             * create main clustor form given distributor outlets and divide each main
             * clustor to subclustor depend on beatGropMode
             */
            List<ClusterDetails> kmeanClusterDetails = meanCalculator.kMeanCalulator(outletLst, outletLst.size(), plnParameter,
                    distributor, plnParameter.getMinClusterSize(), distributionFlag);

            /***
             * sort each subclustor by using its centroid
             */
            kmeanClusterDetails = sequenceUsingCentroid(kmeanClusterDetails, distributor, 0, 1, 1, 0, 1, 1,
                    plnParameter.getBeatGroupMode(), 0d, plnParameter.getPlanForWeek());

            combineCentroidOutlets(kmeanClusterDetails, distributor, model.getAllDistance(), plnParameter);


            Outlet newCentroid = outlierSkipper.calculateNewCentroid(kmeanClusterDetails);
            outlierSkipper.calculateNewCentroidToCurrentCentroidDistance(newCentroid, kmeanClusterDetails);
            double  avgDistance = outlierSkipper.getAvgDistance(kmeanClusterDetails, plnParameter.getMultiplier());
            //double avgDistance = getAvgDistanceOfClusterCentroid(kmeanClusterDetails,distributor);

            List<ClusterDetails> invalidClusterList = new ArrayList<>();
            List<ClusterDetails> validClusterList = new ArrayList<>();
            printCentroidSeq(kmeanClusterDetails, plnParameter,distributor,newCentroid,"CentroidSeq_1");

            int prevInvalidListSize = invalidClusterList.size();
            int nextInvalidListSize = 0;

            if (plnParameter.getPlanMode().equals("Time")) {

                outlierSkipper.recurseToGetAllOutlier(prevInvalidListSize, nextInvalidListSize, avgDistance,invalidClusterList, validClusterList, kmeanClusterDetails, distributor,plnParameter);

                printCentroidSeq(validClusterList, plnParameter,distributor,newCentroid,"CentroidSeq_2");

                kmeanClusterDetails = validClusterList;
            }

            if (invalidClusterList.size() > 0) {

                List<String> msg = new ArrayList<>();
                String header = "OutletID,OutletCode,Latitude,Longitude,VisitFrequency,visitDuration,AvgTurnover,CallFrequency,CallDuration,PrioritySlot,Category,OutletType,DistributorCode,ErrorMessage\n";
                msg.add(header);

                invalidClusterList.stream().forEach(o -> o.getDataPoints().stream().forEach(o1 -> {

                    String newStr = o1.getOutletID() + "," + o1.getOutletCode() + "," + o1.getLatitude() + ","
                            + o1.getLongitude() + "," + o1.getVisitFrequency() + "," + o1.getVisitDuration() + ","
                            + o1.getAvgTurnover() + "," + o1.getCallFrequency() + "," + o1.getCallDuration() + ","
                            + o1.getPrioritySlot() + "," + o1.getCategory() + "," + o1.getOutletType() + ","
                            + o1.getDistributorCode() + "," + "This is an outlying outlet." + "\n";
                    msg.add(newStr);


                }));
                String str[] = msg.toArray(new String[msg.size()]);
                str = Arrays.stream(str).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);

                invalidData = str;

            }

            if(invalidData.length > 0) {
                invalidData = Arrays.stream(invalidData).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
                //outputModel.setInvalidData(invalidData);
            }else {
                //outputModel.setInvalidData(invalidData);
            }

            /*
             * if (distributionFlag) { Map<Integer, List<ClusterDetails>>
             * territoriesMap = new HashMap<>(); territoriesMap.put(0,
             * Lists.mutable.ofAll(kmeanClusterDetails)); return territoryLst =
             * populateTerritoriesOutlets(territoriesMap, model, plnParameter); }
             */

            /***
             * Create Create the territories using subclusters the according to the Plan
             * Mode
             */

            double size = 0;
            if (distributionFlag) {
                size = outletList.stream().collect(Collectors.summingDouble(Outlet::getCallFrequencyMultiplier));
            } else {
                size = outletList.stream().collect(Collectors.summingDouble(Outlet::getMultiplier));
            }

            Map<Integer, List<ClusterDetails>> territoriesMap = createTerritories(kmeanClusterDetails,
                    plnParameter, size, totalValue, totalTime, distributor.getTravellingSpeed(), distributionFlag,
                    model.getAllDistance(),distributor);

            /***
             * Populate all the actual outlets for each territory and sequenced them using
             * global sequence.
             */
            territoryLst = populateTerritoriesOutlets(territoriesMap, model, plnParameter);
        }

        return territoryLst;
    }

    /***
     * Used to populate actual outlets in Territories
     *
     * @param territoriesMap
     * @param model
     * @param plnParameter
     * @return
     */
    private List<Territory> populateTerritoriesOutlets(
            Map<Integer, List<ClusterDetails>> territoriesMap, TerritoryPlannerInputDataModel model,
            PlanParameter plnParameter) {
        Vector<Outlet> dataPoints = new Vector<Outlet>();
        List<Territory> territoryLst = new ArrayList<>();
        for (Map.Entry<Integer, List<ClusterDetails>> map : territoriesMap.entrySet()) {

            for (int i = 0; i < map.getValue().size(); i++) {
                dataPoints.addAll(map.getValue().get(i).getDataPoints());
            }

            Territory t = new Territory(map.getKey(), dataPoints);

            List<Outlet> outletLst = Collections.list(dataPoints.elements());
            dataPoints = new Vector<Outlet>();

            /****
             * global sequenece in territory
             */
/*			GlobalSequenceFinderModel globalseqInputModel = createGlobalSequenceInputModel(t.getOutletPoints(),
					plnParameter.getDistanceType(), model.getDistanceMatrixLst());

			GlobalSequenceOutputModel globalseqOutputModel = globalSequenceFinderProcessor.apply(globalseqInputModel);

			List<GlobalVisitSequence> globalSeqLst = globalseqOutputModel.getVisitSequence().toList().toList();

			*//***
             * convert global sequence objects to original Territory object
             *//*
			Map<Integer, GlobalVisitSequence> globalSeqMap = Optional.ofNullable(globalSeqLst)
					.orElse(Lists.mutable.empty()).stream()
					.collect(Collectors2.toMap(GlobalVisitSequence::getCustomerId, c -> c));

			outletLst.stream().forEach(o -> {
				GlobalVisitSequence g = globalSeqMap.get(o.getOutletID());
				o.setVisitSequence(g.getVisitSequence());
				o.setDistance(g.getDistance());
			});
*/
            /****
             * sort outlet on visitsequence
             */
            outletLst.sort((Outlet s1, Outlet s2) -> s1.getVisitSequence() - s2.getVisitSequence());

            /***
             * set final sorted territory outlet list in Territory object
             */
            t.setOutletPoints(new Vector<Outlet>(outletLst));
            territoryLst.add(t);
        }
        return territoryLst;
    }


    /***
     * used to get noOfBeat to be created
     *
     * @param maxFreq
     * @return
     */
    private int calNoOfBeat(int maxFreq) {
        int beat = 0;
        switch (maxFreq) {
            case 1:
                beat = 1;
                break;
            case 2:
                beat = 2;
                break;
            case 3:
                beat = 3;
                break;
            case 4:
                beat = 4;
                break;
            case 5:
                beat = 5;
                break;
            case 6:
                beat = 6;
                break;
            case 7:
            case 8:
            case 9:
                beat = 8;
                break;
            case 10:
                beat = 11;
                break;
            case 11:
            case 12:
            case 13:
                beat = 12;
                break;
            case 14:
            case 15:
                beat = 15;
                break;
            case 16:
                beat = 16;
                break;
            case 17:
                beat = 17;
                break;
            case 18:
                beat = 18;
                break;
            case 19:
                beat = 19;
                break;
            case 20:
                beat = 20;
                break;
            case 21:
                beat = 21;
                break;
            case 22:
                beat = 22;
                break;
            case 23:
                beat = 23;
                break;
            case 24:
                beat = 24;
                break;
            default:
                break;
        }
        return beat;
    }

    /****
     * used to identify number of salesperson
     *
     * @param plnParameter
     * @param outletLst
     * @param totalValue
     * @param totalTime
     * @param noOfSalesPersons
     * @param distributionFlag
     */
    public int identifySalesPerson(PlanParameter plnParameter, List<Outlet> outletLst, double totalValue,
                                   double totalTime, int noOfSalesPersons, boolean distributionFlag) {
        if (distributionFlag) {

            if (plnParameter.getTelecallingPlanMode().equalsIgnoreCase("Outlet")) {
                if(plnParameter.getTelecallingPlanMode().equalsIgnoreCase("Outlet") && plnParameter.getTelecallingCount() == 0){
                    int maxFrequency = outletLst.stream().mapToInt(Outlet::getCallFrequency).max().orElse(0);
                    maxFrequency = maxFrequency == 16 ? plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()
                            : maxFrequency;

                    double freqOutlet = outletLst.stream().collect(Collectors.summingDouble(outlet -> outlet.getCallFrequencyMultiplier()));

                    double outletsPerSP = plnParameter.getNoOfCallsPerBeat() *
                            (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()/maxFrequency);

//					double numOfSalesPersonsInDouble = (outletLst.size()*maxFrequency) / (plnParameter.getWorkDayInWeek() *
//							plnParameter.getPlanForWeek() * plnParameter.getNoOfCallsPerBeat());

                    double numOfSalesPersonsInDouble = freqOutlet/outletsPerSP;

                    return noOfSalesPersons = getSp(numOfSalesPersonsInDouble);
                }
                else{
                    double noSp = plnParameter.getTelecallingCount();

                    return getSp(noSp);
                }

///				Optional<Outlet> max = outletLst.stream().max(Comparator.comparing(Outlet::getCallFrequency));
//
//				double callFreqMultipler = outletLst.stream()
//						.collect(Collectors.summingDouble(Outlet::getCallFrequencyMultiplier));
//
//				double n = (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek())
//						/ max.get().getCallFrequency();
//
//				double noSp = plnParameter.getTelecallingCount();
//
//				// callFreqMultipler / (plnParameter.getTelecallingCount())
//
//				return getSp(noSp);
            } else {

                double n = (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek())
                        / plnParameter.getMaxFreqency();

                double noSp = totalTime  / (plnParameter.getPerBeatCallTime() * n);
                // (plnParameter.getMaxTime() * plnParameter.getWorkDayInWeek() *
                // plnParameter.getPlanForWeek());

                return getSp(noSp);
            }

        } else {
            if (plnParameter.getPlanMode().equalsIgnoreCase("Outlet")) {
                if(plnParameter.getPlanMode().equalsIgnoreCase("Outlet") && plnParameter.getNoOfSalesperson() == 0){
                    int maxFrequency = outletLst.stream().mapToInt(Outlet::getVisitFrequency).max().orElse(0);
                    maxFrequency = maxFrequency == 16 ? plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()
                            : maxFrequency;

                    double freqOutlet = outletLst.stream().collect(Collectors.summingDouble(outlet -> outlet.getMultiplier()));

                    double outletsPerSP = plnParameter.getNoOfOutletPerBeat() *
                            (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()/maxFrequency);

//					double numOfSalesPersonsInDouble = (outletLst.size()*maxFrequency) / (plnParameter.getWorkDayInWeek() *
//							plnParameter.getPlanForWeek() * plnParameter.getNoOfOutletPerBeat());

                    double numOfSalesPersonsInDouble = freqOutlet/outletsPerSP;

                    return noOfSalesPersons = getSp(numOfSalesPersonsInDouble);
                }
                else
                {
                    return noOfSalesPersons = plnParameter.getNoOfSalesperson();
                }
            } else if (plnParameter.getPlanMode().equalsIgnoreCase("Value")) {

                double n = (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()) / plnParameter.getMaxFreqency();

                double noSp = (totalValue / (plnParameter.getValuePerBeat() * n));

                noSp = (int) (((noSp * 10) % 10 > 4) ? (Math.ceil(noSp)) : (Math.floor(noSp)));
                noSp = noSp == 0 ? 1 : noSp;

                return getSp(noSp);

            } else if (plnParameter.getPlanMode().equalsIgnoreCase("Time")) {

                double n = (plnParameter.getWorkDayInWeek() * plnParameter.getPlanForWeek()) / plnParameter.getMaxFreqency();

                double noSp = (totalTime * plnParameter.getDistanceFactor()) / (plnParameter.getMaxTime() * n);
                // (plnParameter.getMaxTime() * plnParameter.getWorkDayInWeek() *
                // plnParameter.getPlanForWeek());
                int actualSp = getSp(noSp);

                /**
                 * re-calculate max time as per no of sales person
                 */
//				plnParameter.setMaxTime(getSp((totalTime * plnParameter.getDistanceFactor()) / actualSp));

                return actualSp;
            }
        }

        return 0;
    }

    /***
     * used to create territories using sub clusters according to the Plan Mode
     *
     * @param kmeanClusterDetails
     * @param plnParameter
     * @param totalTime
     * @param totalValue
     * @param size
     * @param distributorAvgSpeed
     * @param distributionFlag
     * @param distributor
     */
    private Map<Integer, List<ClusterDetails>> createTerritories(List<ClusterDetails> kmeanClusterDetails,
                                                                               PlanParameter plnParameter, double size, double totalValue, double totalTime, int distributorAvgSpeed,
                                                                               boolean distributionFlag, Map<String,Double> allDistance, Distributor distributor) {

        String planMode;
        double territoryThreshold = 0d;
        if (distributionFlag){
            planMode = plnParameter.getTelecallingPlanMode();
        }else{
            planMode = plnParameter.getPlanMode();
        }

        double halfDays = 0;
        int sp = 0;
        if(plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
            switch(plnParameter.getWorkDayInWeek()) {
                case 6:
                    sp = 6;
                    halfDays = 5.5;
                    break;
                case 5:
                    sp = 5;
                    halfDays = 4.5;
                    break;
                case 4:
                    sp = 4;
                    halfDays = 3.5;
                    break;
            }
        }

        try {
            if (planMode.equalsIgnoreCase("Time")){

                if(plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
                    plnParameter.setHalfDays(halfDays);

                    double sp1 = plnParameter.getNoOfSalesperson();

                    plnParameter.setSp(sp1);
                    plnParameter.setNoOfSalesperson(sp);
                    territoryThreshold = totalTime/halfDays;
                }else {
                    territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalTime);
                }

                return timeBasedTerritoryFormation.timeBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, totalTime,
                        distributorAvgSpeed, plnParameter, distributionFlag, allDistance, distributor);

            }else if(planMode.equalsIgnoreCase("Outlet")){

                if(plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
                    plnParameter.setHalfDays(halfDays);

                    double sp1 = plnParameter.getNoOfSalesperson();

                    plnParameter.setSp(sp1);
                    plnParameter.setNoOfSalesperson(sp);
                    territoryThreshold = size/halfDays;
                }else {
                    territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), size);
                }

                return outletBasedTerritoryFormation.outletBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, plnParameter,
                        distributionFlag);

            }else if(planMode.equalsIgnoreCase("Value")){

                if(plnParameter.getBeatGroupMode().equalsIgnoreCase("Weekday") && plnParameter.getHalfDayFlag()) {
                    plnParameter.setHalfDays(halfDays);

                    double sp1 = plnParameter.getNoOfSalesperson();

                    plnParameter.setSp(sp1);
                    plnParameter.setNoOfSalesperson(sp);
                    territoryThreshold = totalValue/halfDays;
                }else {
                    territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalValue);
                }

                return valueBasedTerritoryFormation.valueBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, totalValue,
                        plnParameter.getNoOfSalesperson(), plnParameter.getWorkDayInWeek(),plnParameter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

/*		if (distributionFlag){
			territoryThreshold = 0d;
			if (plnParameter.getTelecallingPlanMode().equalsIgnoreCase("Outlet")) {
				territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), size);
				return outletBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, plnParameter,
						distributionFlag);

			} else if (plnParameter.getTelecallingPlanMode().equalsIgnoreCase("Time")) {
				territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalTime);
				try {
					return timeBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, totalTime,
							distributorAvgSpeed, plnParameter, distributionFlag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			territoryThreshold = 0d;
			if (plnParameter.getPlanMode().equalsIgnoreCase("Outlet")) {
				territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), size);
				return outletBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, plnParameter,
						distributionFlag);

			} else if (plnParameter.getPlanMode().equalsIgnoreCase("Value")) {
				territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalValue);
				return valueBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, totalValue,
						plnParameter.getNoOfSalesperson(), plnParameter.getWorkDayInWeek());

			} else if (plnParameter.getPlanMode().equalsIgnoreCase("Time")) {
				territoryThreshold = calTerritoryThreshold(plnParameter.getNoOfSalesperson(), totalTime);
				try {
					return timeBasedTerritoriesAllocation(kmeanClusterDetails, territoryThreshold, totalTime,
							distributorAvgSpeed, plnParameter, distributionFlag);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
*/
        return new HashMap<Integer, List<ClusterDetails>>();
    }

    /****
     * used to check gap between outlet point
     *
     * @param totalWithNext
     * @param valueOfNext
     * @param threshold
     * @return
     */
    public boolean isGapIsShortWithNext(double totalWithNext, double valueOfNext, double threshold) {
        boolean result = true;

        result = (totalWithNext - threshold) < (threshold - (totalWithNext - valueOfNext));

        return result;
    }



    /****
     * used to calculate linear distance
     *
     * @param from
     * @param to
     * @return
     */
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
                    + Math.cos(lat1) * Math.cos(lat2) * Math.sin(difflong / 2) * Math.sin(difflong / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = 6378137 * c; // metres
            return distance;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Double distanceBetweenTwoOutlets(Outlet o1, Outlet o2, Map<String,Double> allDistance){
        String key = o1.getOutletID() + ":" + o2.getOutletID();
        return allDistance.get(key);
    }

    /***
     * used to get Threshold value
     *
     * @param noOfSalesperson
     * @param var
     * @return
     */
    public double calTerritoryThreshold(int noOfSalesperson, double var) {
        return (var / noOfSalesperson);
    }

    /***
     * get salesperson after rounding in Time And Value based plan mode
     *
     * @param noSp
     * @return
     */
    private int getSp(double noSp) {

        double precision = noSp - (int) (noSp);

        if (precision > .3d) {
            return (int) Math.ceil(noSp);
        } else {
            return (int)Math.floor(noSp)==0?1:(int)Math.floor(noSp);
        }
    }

    /****
     * used to print centroid sequenece output in csv file
     * @param kmeanClusterDetails
     * @param plnParameter
     * @param distributor
     * @param newCentroid
     * @param fileName
     */
    private void printCentroidSeq(List<ClusterDetails> kmeanClusterDetails, PlanParameter plnParameter,
                                  Distributor distributor, Outlet newCentroid, String fileName) {

        // TODO: comment before commit, print centroid seq in file
        StringBuilder ab = new StringBuilder();
        ab.append("Lat\tLong\tcentroidID\tseq\tdistance\tnewDitance\tx\ty\n");
        int count = 1;
        double distance = 0;
        Outlet prevOutlet = null;
        Outlet currentOutlet = null;
        double distanceSum = 0;
        for (ClusterDetails clusterDetails : kmeanClusterDetails) {

            currentOutlet = new Outlet(clusterDetails.getCentroid().getCx(), clusterDetails.getCentroid().getCy());

            if (prevOutlet != null) {
                distance = calculateLinearDistance(prevOutlet, currentOutlet);
            }

            distanceSum += distance;

            prevOutlet = currentOutlet;
            ab.append(clusterDetails.getCentroid().getCx() + "\t" + clusterDetails.getCentroid().getCy() + "\t"
                    + clusterDetails.getClusterId() + "\t" + count + "\t" + distance + "\t"
                    + clusterDetails.getNewCentoirdToCurrentCetroidDitance() + "\t" +newCentroid.getLatitude() + "\t"
                    + newCentroid.getLongitude() + "\n");
            count++;
        }

        double avgDistance = distanceSum/count;

        System.out.println("distanceSum: "+distanceSum+" avgDistance: "+avgDistance+" count: "+count);



        File f = new File(fileName);
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            FileWriter fw = new FileWriter(
                    f + "/" + "CentroidSeq_Output" + "_" + "Max_Frequency_" + plnParameter.getMaxFreqency()
                            + LocalDateTime.now().toString().replaceAll(":", "-") + "_" + ".csv",
                    false);
            fw.write(ab.toString());

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * used to sequence each cluster by using its centroid
     *
     * @param kmeanClusterDetails
     * @param distributor
     * @param planId
     * @param distanceToCentroid
     * @param noOfClusterNeeded
     * @param clusterCount
     * @param noOfPointsInCluster
     * @param planBy
     * @param PlanRunTypeflag
     * @param costValue
     * @param planForWeek
     * @return
     */
    private List<ClusterDetails> sequenceUsingCentroid(List<ClusterDetails> kmeanClusterDetails,
                                                       Distributor distributor, int planId, int distanceToCentroid, int noOfClusterNeeded, int clusterCount,
                                                       int noOfPointsInCluster, int planBy, String PlanRunTypeflag, double costValue, int planForWeek) {

        Cluster centroidOutletCluster = new Cluster("temp");
        for (int i = 0; i < kmeanClusterDetails.size(); i++) {
            centroidOutletCluster.getmDataPoints().add(new Outlet(kmeanClusterDetails.get(i).getCentroid().getCx(),
                    kmeanClusterDetails.get(i).getCentroid().getCy(), kmeanClusterDetails.get(i).getClusterId()));
        }

        List<Integer> clusterIdLst = getDataPointSortByCombingPlan(centroidOutletCluster, distributor, planId,
                distanceToCentroid, noOfClusterNeeded, clusterCount);

        return sortkmeanClusterDetailsByIndex(kmeanClusterDetails, clusterIdLst);
    }

    /****
     * uesd to sort each subcluster by its indexwise given by global sequence
     *
     * @param kmeanClusterDetails
     * @param clusterIdLst
     * @return
     */
    private List<ClusterDetails> sortkmeanClusterDetailsByIndex(List<ClusterDetails> kmeanClusterDetails,
                                                                List<Integer> clusterIdLst) {
        List<ClusterDetails> tempKMCD = new ArrayList<ClusterDetails>(kmeanClusterDetails.size());
        Map<Integer, ClusterDetails> tempKMCDMap = Optional.ofNullable(kmeanClusterDetails)
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(ClusterDetails::getClusterId, c -> c));

        for (int i = 0; i < clusterIdLst.size(); i++) {
            tempKMCD.add(tempKMCDMap.get(clusterIdLst.get(i)));
        }
        return tempKMCD;
    }

    /****
     * used to sort cluster using cluster centroid
     *
     * @param clusterStore
     * @param distributor
     * @param planId
     * @param distanceToCentroid
     * @param noOfClusterNeeded
     * @param clusterCount
     * @return
     */
    private List<Integer> getDataPointSortByCombingPlan(Cluster clusterStore, Distributor distributor,
                                                               int planId, int distanceToCentroid, int noOfClusterNeeded, int clusterCount) {

        List<Integer> centroidIdLst = new ArrayList<>();

        List<List<GlobalVisitSequence>> outlets = new ArrayList<List<GlobalVisitSequence>>();

        /***
         * four corner points creation logic
         */
        Vector<Outlet>[] dataPointsNew = (Vector<Outlet>[]) new Vector[4];
        createFourCornerDataPoint(dataPointsNew, new ArrayList<Outlet>(clusterStore.getDataPoints()));

        /***
         * sort outlets by using four corner points
         */
        PlanParameter plnParameter = new PlanParameter();
        plnParameter.setDistanceType(1);

        sortUsingGlobalsequence(plnParameter, dataPointsNew, distributor, new TerritoryPlannerInputDataModel(),
                outlets);

        /***
         * convert global sequence output to beat planner output
         */
        Map<Integer, Integer> globalSeqIndexMap = Optional.ofNullable(outlets.get(minDistIndex))
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.toMap(GlobalVisitSequence::getCustomerId, c -> c.getVisitSequence()));

        Optional.ofNullable(outlets.get(minDistIndex)).orElse(new ArrayList<>()).stream().forEach(o -> {
            centroidIdLst.add(o.getCustomerId());
        });

        return centroidIdLst;
    }

    /***
     * used to sort outlet using global sequence
     *
     * @param plnParameter
     * @param dataPointsNew
     * @param distributor
     * @param model
     * @param outlets
     */
    private void sortUsingGlobalsequence(PlanParameter plnParameter, Vector<Outlet>[] dataPointsNew,
                                         Distributor distributor, TerritoryPlannerInputDataModel model,
                                         List<List<GlobalVisitSequence>> outlets) {
        for (int j = 0; j < 4; j++) {
            /***
             * create globalsequence input object
             */
            GlobalSequenceInputDataModel globalseqInputModel = globalSequenceProcessorCaller.createGlobalSequenceInputModel(dataPointsNew[0],
                    plnParameter.getDistanceType(), model.getAllDistance());

            /***
             * call globalsequence processor to sequence outlets very 1st time
             */
            GlobalSequenceOutputDataModel globalseqOutputModel = null;
            try {
                globalseqOutputModel = globalSequenceProcessor.process(globalseqInputModel);
            } catch (Exception e) {
                e.printStackTrace();
            }

            outlets.add(globalseqOutputModel.getVisitSequence());

            double totalDistance = globalseqOutputModel.getVisitSequence().stream()
                    .collect(Collectors.summingDouble(GlobalVisitSequence::getDistance));

            /***
             * check for minimum distance global sequence outlets
             */
            if (minDist > totalDistance) {
                minDist = totalDistance;
                minDistIndex = j;
            }
        }
    }

    /***
     * used to combine result of sorted subcluster
     *
     * @param kmeanClusterDetails
     * @param distributor
     * @param distanceMatrixMap
     * @param planParameter
     */
    private void combineCentroidOutlets(List<ClusterDetails> kmeanClusterDetails, Distributor distributor,
                                        Map<String, Double> distanceMatrixMap, PlanParameter planParameter) throws Exception {

        if (kmeanClusterDetails != null && !kmeanClusterDetails.isEmpty()) {
            ClusterDetails temp;
            List<ClusterDetails> kList = new ArrayList<>();
            Map<Integer, List<ClusterDetails>> finalResultMap = new HashMap<>();

            int indexId = 0;
            Customer startPoint = new Customer();
            startPoint.setLatitude(distributor.getLatitude());
            startPoint.setLongitude(distributor.getLongitude());
            startPoint.setCustomerId(-1);

            for (int i = 0; i < kmeanClusterDetails.size(); i++) {
                temp = kmeanClusterDetails.get(i);
                if (temp.getDataPoints().size() > 1) {
                    ClusterDetails gsOutput = new ClusterDetails();

                    Vector<Outlet> outletsInCurrentSlot = temp.getDataPoints();
                    Vector<Outlet> dataPoints = new Vector<>();
                    Map<Integer, Outlet> outletIdMap = new HashMap<>();
                    outletIdMap = temp.getDataPoints().stream().collect(Collectors.toMap(Outlet::getOutletID,
                            Function.identity()));

                    /**
                     * create global sequence input model
                     */
                    GlobalSequenceInputDataModel globalSequenceFinderModel = new GlobalSequenceInputDataModel();
                    globalSequenceFinderModel.setDistanceType(planParameter.getDistanceType());
                    globalSequenceFinderModel.setAllDistance(distanceMatrixMap);
                    globalSequenceFinderModel.setStartPoint(startPoint);

                    List<Customer> locationsList = new ArrayList<>();
                    for (int loc = 0; loc < outletsInCurrentSlot.size(); loc++) {
                        Outlet currentOutlet = outletsInCurrentSlot.get(loc);
                        locationsList.add(loc,
                                new Customer(currentOutlet.getOutletID(), currentOutlet.getOutletCode(),
                                        currentOutlet.getLatitude(),
                                        currentOutlet.getLongitude()));
                    }

                    globalSequenceFinderModel.setCustomerList(locationsList);

                    /**
                     * Apply global sequence algo
                     */
                    GlobalSequenceOutputDataModel globalSequenceOutputModel = globalSequenceProcessor
                            .process(globalSequenceFinderModel);
                    List<GlobalVisitSequence> visitSequenceList = globalSequenceOutputModel.getVisitSequence();

                    /**
                     * Extract outlets from global sequence output
                     */
                    for (GlobalVisitSequence globalVisitSequence : visitSequenceList) {
                        int iIndex = 0;
                        dataPoints.add(iIndex++, outletIdMap.get(globalVisitSequence.getCustomerId()));
                    }
                    gsOutput.setDataPoints(dataPoints);
                    kList.add(gsOutput);

                    /**
                     * Make last outlet the starting point
                     */
                    Outlet lastOutletDetail = gsOutput.getDataPoints().get(gsOutput.getDataPoints().size() - 1);
                    startPoint.setLongitude(lastOutletDetail.getLongitude());
                    startPoint.setLatitude(lastOutletDetail.getLatitude());
                    startPoint.setCustomerId(lastOutletDetail.getOutletID());

                    finalResultMap.put(indexId, kList);
                    indexId++;
                    kList = new ArrayList<ClusterDetails>();
                } else if (temp.getDataPoints().size() == 1) {
                    kList.add(temp);
                    finalResultMap.put(indexId, kList);
                    indexId++;
                    kList = new ArrayList<ClusterDetails>();

                    Outlet lastOutletDetail = temp.getDataPoints().get(0);
                    startPoint.setLongitude(lastOutletDetail.getLongitude());
                    startPoint.setLatitude(lastOutletDetail.getLatitude());
                    startPoint.setCustomerId(lastOutletDetail.getOutletID());
                }
            }
        }
    }

    /***
     * used to create four corner point
     *
     * @param dataPointsNew
     * @param outletLst
     */
    private void createFourCornerDataPoint(Vector<Outlet>[] dataPointsNew, List<Outlet> outletLst) {
        dataPointsNew[0] = new Vector<Outlet>(outletLst);
        Collections.copy(dataPointsNew[0], outletLst);
        Collections.sort(dataPointsNew[0], Outlet.DataPointXComparator);

        dataPointsNew[1] = new Vector<Outlet>(outletLst);
        Collections.copy(dataPointsNew[1], outletLst);
        Collections.sort(dataPointsNew[1], Outlet.DataPointXComparator);

        Collections.reverse(dataPointsNew[1]);
        dataPointsNew[2] = new Vector<Outlet>(outletLst);
        Collections.copy(dataPointsNew[2], outletLst);
        Collections.sort(dataPointsNew[2], Outlet.DataPointYComparator);

        dataPointsNew[3] = new Vector<Outlet>(outletLst);
        Collections.copy(dataPointsNew[3], outletLst);
        Collections.sort(dataPointsNew[3], Outlet.DataPointYComparator);
        Collections.reverse(dataPointsNew[3]);
    }

}

