package com.planner.territory.model.globalsequence;


import lombok.*;

import java.util.Map;

/**
 * 
 * @author shubham shinde output model class for distance processor
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class DistanceProcessorOutput  {

	double[][] allDistance;
	Map<String,Double> allDistanceMap;

}
