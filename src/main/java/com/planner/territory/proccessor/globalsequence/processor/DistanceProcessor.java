package com.planner.territory.proccessor.globalsequence.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.territory.model.globalsequence.Customer;
import com.planner.territory.model.globalsequence.DistanceProcessorInput;
import com.planner.territory.model.globalsequence.DistanceProcessorOutput;
import com.planner.territory.model.globalsequence.GlobalSequenceConstants;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author saurabh
 * 
 *         DistanceProcessor use find out distance between two location. It take
 *         list of locations find out all possible location route pair From
 *         location to To location
 * 
 *         Find Distance between two location using different formula like 1.
 *         linearDistance formula 2.Google API
 * 
 */
@Slf4j
@Component(value = "DistanceFinder")
public class DistanceProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	LinearDistance linearDistance;

	@Autowired
	OsrmDistance osrmDistance;

	@Autowired
	GlobalSequenceConstants constants;

	//@Autowired
	//Properties properties;

	public DistanceProcessorOutput process(DistanceProcessorInput distanceProcessorInput)
			throws  Exception {

		DistanceProcessorOutput distanceProcessorOutput = new DistanceProcessorOutput();
		Map<String, Double> allDistanceMap;
		switch (distanceProcessorInput.getDistanceType()) {
		case 1:
			/**
			 * calculate distance from linear distance
			 */
			allDistanceMap = linearDistance.calculateDistance(distanceProcessorInput.getLocationsList());
			break;

		case 2:
			/***
			 * read excel file
			 */
			allDistanceMap = distanceProcessorInput.getAllDistance();
			break;

		case 4:

			/**
			 * calculate distance using OSRM matrix this method implementation
			 */
			Map<String, Object> baseurl = getMap(constants.getOSRM_BASE_URL());
			String OSRM_BASE_URL = baseurl.get(constants.getCountry()).toString();
			//log.info("OSRM Server Country " + properties.getProperties().get("country") + " url = " + OSRM_BASE_URL);
			allDistanceMap = osrmDistance.getOsrmDistance(distanceProcessorInput.getLocationsList(), OSRM_BASE_URL);
			break;
		default:
			allDistanceMap = linearDistance.calculateDistance(distanceProcessorInput.getLocationsList());
			break;
		}
		distanceProcessorOutput.setAllDistance(convertDistanceIntoArray(allDistanceMap, distanceProcessorInput.getLocationsList(),
				distanceProcessorInput.getLocationsList().size()));
		distanceProcessorOutput.setAllDistanceMap(allDistanceMap);
		return distanceProcessorOutput;
	}

	/**
	 * This Method Store Distance in 2D Array Format For Execution
	 * 
	 * @param allDistance
	 * @param locationList
	 * @param totallocation
	 * @return
	 */
	private double[][] convertDistanceIntoArray(Map<String, Double> allDistance,
												List<Customer> locationList, int totallocation) {

		double[][] alldistance = new double[totallocation][totallocation];
		for(int i=0;i<totallocation;i++)
		{
			for(int j=0;j<totallocation;j++)
			{
				int from =locationList.get(i).getCustomerId();
				int to =locationList.get(j).getCustomerId();
				alldistance[i][j]=allDistance.get(from+":"+to);
			}
		}
		return alldistance;
	}

	private Map<String, Object> getMap(String oSRM_BASE_URL) throws Exception {
		Map<String, Object> result = new ObjectMapper().readValue(oSRM_BASE_URL, new TypeReference<Map<String, Object>>(){});

		return result;
	}

}
