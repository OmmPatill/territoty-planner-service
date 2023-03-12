package com.planner.territory.model.globalsequence;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author shubham shinde
 *
 *         Iteration Details class use for store execution deatils it store
 *         deatils pass to next execution
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class IterateDetails {

	int totalPoints;
	double nearestDist;
	int nearestOutlet;
	int nextNearestOutlet;
	int insertionPoint;
	double olddist;
	double totaldist;
	// visited list contain location visited or not is may changes as per visiting
	// sequence
	int[] visited;

	public void setVisitedIndex(int index, int value) {
		this.visited[index] = value;

	}

	public int getVisitedIndex(int index) {
		return this.visited[index];

	}

}
