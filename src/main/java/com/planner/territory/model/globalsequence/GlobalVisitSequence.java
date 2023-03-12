package com.planner.territory.model.globalsequence;


import lombok.*;

/***
 * 
 * @author shubham shinde
 * 
 *         In this class we store visiting sequence objects data this data we
 *         can write in globval sequence output excel file
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GlobalVisitSequence {

	private int customerId;
	private String customerCode;
	private double latitude;
	private double longitude;
	private int visitSequence;
	private double distance;
}
