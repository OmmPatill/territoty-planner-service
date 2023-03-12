package com.planner.territory.model.globalsequence;

import lombok.*;

import java.util.List;
import java.util.Map;


/***
 *
 * @author shubham shinde
 *
 *         read and store input data for global sequencing we read data from
 *         excel and store in this class
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class GlobalSequenceInputDataModel {
    List<Customer> customerList;// location list change as per nearest location. we set at first index so that
    // why we take it as mutable
    Map<String, Double> allDistance;
    Customer startPoint;
    Integer distanceType;
}
