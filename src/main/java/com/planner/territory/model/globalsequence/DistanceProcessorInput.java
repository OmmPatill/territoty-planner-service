package com.planner.territory.model.globalsequence;


import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author shubbham shinde
 *
 *         read and store input data forDistance processing we read data from
 *         excel and store in this class
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class DistanceProcessorInput  {

	List<Customer> locationsList;
	int distanceType;
	Map<String,Double> allDistance;

}
