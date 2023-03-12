package com.planner.territory.proccessor.globalsequence;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algorhythm.core.ex.RhythmApplicationException;
import com.algorhythm.globalsequence.base.common.utility.HttpUrlProcessor;
import com.algorhythm.planner.globalsequence.constants.Constants;
import com.algorhythm.planner.globalsequence.model.Customer;
import com.algorhythm.planner.production.common.model.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(value = "OsrmDistance")
public class OsrmDistance {

	@Autowired
	HttpUrlProcessor httpUrlProcessor;
	@Autowired
	Constants constants;
	@Autowired
	LinearDistance linearDistance;
	@Autowired
	Properties properties;

	/**
	 * Get OSRM distance of given customer list
	 * 
	 * @param CustomerList
	 * @param oSRM_BASE_URL
	 * @return
	 * @throws RhythmApplicationException
	 */
	public MutableMap<String, Double> getOsrmDistance(MutableList<Customer> CustomerList, String oSRM_BASE_URL)
			throws RhythmApplicationException {

		MutableList<MutableList<Customer>> CustomerListSet = getDividedDataPoint(CustomerList);
		CustomerListSet.stream().forEach(o -> log.info("" + o.size()));

		MutableMap<String, Double> allDistance = getCoodDistDuration(CustomerListSet, oSRM_BASE_URL);

		return allDistance;

	}

	/**
	 * 
	 * @param customerListSet
	 * @param oSRM_BASE_URL
	 * @return
	 * @throws RhythmApplicationException
	 */
	private MutableMap<String, Double> getCoodDistDuration(MutableList<MutableList<Customer>> customerListSet,
			String oSRM_BASE_URL) throws RhythmApplicationException {
		MutableMap<String, Double> allDistance = new UnifiedMap<String, Double>();

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
	 * @throws RhythmApplicationException
	 */
	private MutableMap<String, Double> getOneToOneDPGroupMap(MutableList<MutableList<Customer>> customerListSet,
			MutableMap<String, Double> allDistance, String oSRM_BASE_URL) throws RhythmApplicationException {

		MutableList<Customer> CombinSet = new FastList<>();
		for (int i = 0; i < customerListSet.size(); i++) {
			for (int j = i; j < customerListSet.size(); j++) {
				if (j != i) {
					CombinSet.addAll(customerListSet.get(i));
					CombinSet.addAll(customerListSet.get(j));
					log.info("" + LocalDateTime.now() + "\t" + CombinSet.size() + " Request Start " + i + ":" + j);
					allDistance.putAll(calculateOSRMDistance(CombinSet, oSRM_BASE_URL));
					log.info(LocalDateTime.now() + "\t" + CombinSet.size() + " Request End " + i + ":" + j);
					log.info(LocalDateTime.now() + "\t" + allDistance.size() + " connection End");
					CombinSet = new FastList<>();
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
	private MutableList<MutableList<Customer>> getDividedDataPoint(MutableList<Customer> customerList) {

		MutableList<MutableList<Customer>> CustomerListSet = new FastList<>();

		if (customerList != null && !customerList.isEmpty()) {
			int count = 0;
			MutableList<Customer> set = new FastList<>();
			for (int i = 0; i < customerList.size(); i++, count++) {
				if (customerList.get(i).getLatitude() == 0.0 || customerList.get(i).getLongitude() == 0.0)
					continue;
				if (count == constants.getOSRM_SET_VALUE()) {
					count = 0;
					CustomerListSet.add(set);
					set = new FastList<>();
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
	 * @param locationsList
	 * @param OSRM_BASE_URL
	 * @return
	 * @throws RhythmApplicationException
	 */
	public MutableMap<String, Double> calculateOSRMDistance(MutableList<Customer> customerList, String OSRM_BASE_URL)
			throws RhythmApplicationException {

		MutableMap<String, Double> allDistance = new UnifiedMap<String, Double>();

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
		} catch (RhythmApplicationException e) {
			throw new RhythmApplicationException(e.getMessage());
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
	 * @param locationsList
	 * @return
	 */
	private StringBuilder getDynamicURL(StringBuilder url, String OSRM_BASE_URL, MutableList<Customer> customerList) {
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
	 * @param locationsList
	 * @param allDistance
	 * @return
	 * @throws RhythmApplicationException
	 */
	private MutableMap<String, Double> setDistanceMatrix(StringBuffer response, MutableList<Customer> customerList,
			MutableMap<String, Double> allDistance) throws RhythmApplicationException {
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
	 * @throws RhythmApplicationException
	 */
	private void destinationIteration(int i, JSONArray source, MutableMap<String, Double> allDistance,
			MutableList<Customer> customerList) throws RhythmApplicationException {
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
			throw new RhythmApplicationException(e.getMessage());
		}
	}

	/***
	 * return distance between two customer
	 * 
	 * @param from
	 * @param to
	 * @return
	 * @throws RhythmApplicationException
	 */
	public double getdistanceBetweenTwoCustomer(Customer from, Customer to) throws RhythmApplicationException {
		ImmutableMap<String, Object> baseurl = getMap(constants.getOSRM_BASE_URL());
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
	private ImmutableMap<String, Object> getMap(String oSRM_BASE_URL) {

		JSONObject json = new JSONObject(oSRM_BASE_URL);
		ImmutableMap<String, Object> baseurl = Maps.immutable.ofAll(json.toMap());

		return baseurl;
	}

}
