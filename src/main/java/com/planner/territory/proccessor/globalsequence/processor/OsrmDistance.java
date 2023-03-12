package com.planner.territory.proccessor.globalsequence.processor;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.territory.model.globalsequence.Customer;
import com.planner.territory.model.globalsequence.GlobalSequenceConstants;
import com.planner.territory.proccessor.globalsequence.utility.HttpUrlProcessor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(value = "OsrmDistance")
public class OsrmDistance {

	@Autowired
	HttpUrlProcessor httpUrlProcessor;
	@Autowired
	GlobalSequenceConstants constants;
	@Autowired
	LinearDistance linearDistance;

	/**
	 * Get OSRM distance of given customer list
	 * 
	 * @param CustomerList
	 * @param oSRM_BASE_URL
	 * @return
	 * @throws Exception
	 */
	public Map<String, Double> getOsrmDistance(List<Customer> CustomerList, String oSRM_BASE_URL)
			throws Exception {

		List<List<Customer>> CustomerListSet = getDividedDataPoint(CustomerList);
		CustomerListSet.stream().forEach(o -> log.info("" + o.size()));

		Map<String, Double> allDistance = getCoodDistDuration(CustomerListSet, oSRM_BASE_URL);

		return allDistance;

	}

	/**
	 * 
	 * @param customerListSet
	 * @param oSRM_BASE_URL
	 * @return
	 * @throws Exception
	 */
	private Map<String, Double> getCoodDistDuration(List<List<Customer>> customerListSet,
			String oSRM_BASE_URL) throws Exception {
		Map<String, Double> allDistance = new HashMap<String, Double>();

		if (customerListSet != null && !customerListSet.isEmpty()) {

			for (int i = 0; i < customerListSet.size(); i++) {
				log.info(LocalDateTime.now() + "\t" + customerListSet.get(i).size() + " Request Start");
				allDistance.putAll(calculateOSRMDistance(customerListSet.get(i), oSRM_BASE_URL));
				log.info(LocalDateTime.now() + "\t" + customerListSet.get(i).size() + " Request End");
				log.info(LocalDateTime.now() + "\t" + allDistance.size() + " connection End");
			}
		}

		allDistance = getOneToOneDPGroupMap(customerListSet, allDistance, oSRM_BASE_URL);

		return allDistance;
	}

	/**
	 * 
	 * @param customerListSet
	 * @param allDistance
	 * @param oSRM_BASE_URL
	 * @return
	 * @throws Exception
	 */
	private Map<String, Double> getOneToOneDPGroupMap(List<List<Customer>> customerListSet,
			Map<String, Double> allDistance, String oSRM_BASE_URL) throws Exception {

		List<Customer> CombinSet = new ArrayList<>();
		for (int i = 0; i < customerListSet.size(); i++) {
			for (int j = i; j < customerListSet.size(); j++) {
				if (j != i) {
					CombinSet.addAll(customerListSet.get(i));
					CombinSet.addAll(customerListSet.get(j));
					log.info("" + LocalDateTime.now() + "\t" + CombinSet.size() + " Request Start " + i + ":" + j);
					allDistance.putAll(calculateOSRMDistance(CombinSet, oSRM_BASE_URL));
					log.info(LocalDateTime.now() + "\t" + CombinSet.size() + " Request End " + i + ":" + j);
					log.info(LocalDateTime.now() + "\t" + allDistance.size() + " connection End");
					CombinSet = new ArrayList<>();
				}
			}
		}
		return allDistance;
	}

	/**
	 * 
	 * @param customerList
	 * @return
	 */
	private List<List<Customer>> getDividedDataPoint(List<Customer> customerList) {

		List<List<Customer>> CustomerListSet = new ArrayList<>();

		if (customerList != null && !customerList.isEmpty()) {
			int count = 0;
			List<Customer> set = new ArrayList<>();
			for (int i = 0; i < customerList.size(); i++, count++) {
				if (customerList.get(i).getLatitude() == 0.0 || customerList.get(i).getLongitude() == 0.0)
					continue;
				if (count == constants.getOSRM_SET_VALUE()) {
					count = 0;
					CustomerListSet.add(set);
					set = new ArrayList<>();
				}
				set.add(customerList.get(i));
			}
			if (!set.isEmpty()) {
				if (set.size() == 1) {
					if (CustomerListSet.size() > 0) {
						CustomerListSet.get(0).addAll(set);
					} else {
						CustomerListSet.add(set);
					}
				} else {
					CustomerListSet.add(set);
				}
			}
		}
		return CustomerListSet;
	}

	/***
	 * Calculate Distance between all location using OSRM API
	 * 
	 * @param customerList
	 * @param OSRM_BASE_URL
	 * @return
	 * @throws Exception
	 */
	public Map<String, Double> calculateOSRMDistance(List<Customer> customerList, String OSRM_BASE_URL)
			throws Exception {

		Map<String, Double> allDistance = new HashMap<String, Double>();

		try {
			if (customerList != null && !customerList.isEmpty()) {
				StringBuilder url = new StringBuilder();
				url = getPrefix(url, OSRM_BASE_URL);
				url = getDynamicURL(url, OSRM_BASE_URL, customerList);
				url = getPostFix(url);
				StringBuffer response = httpUrlProcessor.getOSRMMatrix(url);
				allDistance = setDistanceMatrix(response, customerList, allDistance);
			}
			return allDistance;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/***
	 * add postFix to URL
	 * 
	 * @param url
	 * @return
	 */
	private StringBuilder getPostFix(StringBuilder url) {
		url.append("?annotations=distance");
		return url;
	}

	/***
	 * add preFix to URL
	 * 
	 * @param url
	 * @param OSRM_BASE_URL
	 * @return
	 */
	private StringBuilder getPrefix(StringBuilder url, String OSRM_BASE_URL) {
		url.append(OSRM_BASE_URL);
		url.append(constants.getOSRM_TABLE_SECONDARY_URL());
		return url;
	}

	/***
	 * create dynamic URL as per locations
	 * 
	 * @param url
	 * @param OSRM_BASE_URL
	 * @param customerList
	 * @return
	 */
	private StringBuilder getDynamicURL(StringBuilder url, String OSRM_BASE_URL, List<Customer> customerList) {
		String origins = new String("");
		DecimalFormat df = new DecimalFormat("###.#####");
		for (int i = 0; i < customerList.size(); i++) {
			origins = df.format(customerList.get(i).getLongitude()) + ","
					+ df.format(customerList.get(i).getLatitude());
			url.append(origins);
			if (i != customerList.size() - 1)
				url.append(";");
		}
		return url;
	}

	/***
	 * Take OSRM
	 * 
	 * @param response
	 * @param customerList
	 * @param allDistance
	 * @return
	 * @throws Exception
	 */
	private Map<String, Double> setDistanceMatrix(StringBuffer response, List<Customer> customerList,
			Map<String, Double> allDistance) throws Exception {
		JSONObject myResponse = new JSONObject(response.toString());
		JSONArray distance = myResponse.getJSONArray("distances");
		JSONArray source;
		for (int i = 0; i < distance.length(); i++) {
			source = distance.getJSONArray(i);
			destinationIteration(i, source, allDistance, customerList);
		}
		return allDistance;
	}

	/***
	 * 
	 * @param i            source location index
	 * @param source       source to destination distance array
	 * @param allDistance
	 * @param customerList
	 * @throws Exception
	 */
	private void destinationIteration(int i, JSONArray source, Map<String, Double> allDistance,
			List<Customer> customerList) throws Exception {
		try {
			for (int j = 0; j < source.length(); j++) {
				double distance = source.getDouble(j);// 1000;
				if (distance == 0.0) {
					distance = linearDistance.calculateLinearDistance(customerList.get(i), customerList.get(j));
					allDistance.put(customerList.get(i).getCustomerId() + ":" + customerList.get(j).getCustomerId(),
							distance);
				} else {
					allDistance.put(customerList.get(i).getCustomerId() + ":" + customerList.get(j).getCustomerId(),
							distance);
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/***
	 * return distance between two customer
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws Exception
	 */
	public double getdistanceBetweenTwoCustomer(Customer from, Customer to) throws Exception {
		Map<String, Object> baseurl = getMap(constants.getOSRM_BASE_URL());
		String OSRM_BASE_URL = baseurl.get(constants.getCountry()).toString();
		double distance = 0;
		if (from != null && to != null) {
			StringBuilder url = new StringBuilder();
			url = getPrefix(url, OSRM_BASE_URL);
			url = url.append(
					from.getLongitude() + "," + from.getLatitude() + ";" + to.getLongitude() + "," + to.getLatitude());
			url = getPostFix(url);
			StringBuffer response = httpUrlProcessor.getOSRMMatrix(url);
			JSONObject myResponse = new JSONObject(response.toString());
			JSONArray distanceArray = myResponse.getJSONArray("distances");
			JSONArray source = distanceArray.getJSONArray(0);
			distance = source.getDouble(1);
		}
		if (distance == 0.0) {
			distance = linearDistance.calculateLinearDistance(from, to);
			log.info("OSRM return distance is 0 Then Use linear distance method");
			return distance;
		} else {
			return distance;
		}
	}

	/**
	 * get base URL map
	 * 
	 * @param oSRM_BASE_URL
	 * @return
	 */
	private Map<String, Object> getMap(String oSRM_BASE_URL) throws Exception {
		Map<String, Object> result = new ObjectMapper().readValue(oSRM_BASE_URL, new TypeReference<Map<String, Object>>(){});

		return result;
	}

}
