package com.planner.territory.proccessor.globalsequence.processor;


import com.planner.territory.model.globalsequence.Customer;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component(value = "LinearDistance")
public class LinearDistance {

	/***
	 * Calculate Distance of all location useing linear distance formula and and set
	 * distance matrix
	 * 
	 * @param customerList = list of location
	 * @return Distance Matrix
	 * @throws Exception
	 */
	public Map<String, Double> calculateDistance(List<Customer> customerList)
			throws Exception {
		int totallocation = customerList.size();

		Map<String, Double> allDistance = new HashMap<String, Double>();

		for (int i = 0; i < totallocation; i++) {
			loppingToLocation(customerList, allDistance, i);
		}
		return allDistance;
	}

	/***
	 * Looping for from to all To locations
	 * 
	 * @param customerList = list of location
	 * @param allDistance   = allDistance matrix to set distance of From-To
	 * @param i             = from location
	 * @throws Exception
	 */
	private void loppingToLocation(List<Customer> customerList, Map<String, Double> allDistance, int i)
			throws Exception {
		try {
			for (int j = i; j < customerList.size(); j++) {
				if (i != j) {
					double distance = 0;
					distance = calculateLinearDistance(customerList.get(i), customerList.get(j));
					allDistance.put(customerList.get(i).getCustomerId() + ":" + customerList.get(j).getCustomerId(),
							distance);
					allDistance.put(customerList.get(j).getCustomerId() + ":" + customerList.get(i).getCustomerId(),
							distance);
				} else {
					allDistance.put(customerList.get(i).getCustomerId() + ":" + customerList.get(j).getCustomerId(),
							0.0);
					allDistance.put(customerList.get(j).getCustomerId() + ":" + customerList.get(i).getCustomerId(),
							0.0);
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	/***
	 * calculate Distance From location - To location use linear distance formula
	 * 
	 * @param from = From location object
	 * @param to   = To location object
	 * @return = Distance
	 */
	public double calculateLinearDistance(Customer from, Customer to) {
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
			return distance;// 1000;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
