package com.planner.territory.model.globalsequence;


import lombok.*;

import java.util.List;
import java.util.Map;

/***
 * 
 * @author shubham shinde
 * 
 *         this output model class for global sequencing processor we pass this
 *         class to writer to write excel file
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GlobalSequenceOutputDataModel {

	private List<GlobalVisitSequence> visitSequence;
	private Map<String, Double> allDistance;
	private List<Customer> distanceCust;
	private double totalDistance;

}
