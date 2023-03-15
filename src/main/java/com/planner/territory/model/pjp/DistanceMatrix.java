package com.planner.territory.model.pjp;


import lombok.*;

/**
 *
 * @author shubham shinde
 *
 *         DistanceMatrix store both location From and To location And distance
 *         between them
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class DistanceMatrix{

    private Integer distanceMatrixId;
    @EqualsAndHashCode.Include
    private String fromLocationCode;
    @EqualsAndHashCode.Include
    private String toLocationCode;
    private double distance;
    private String distributorCode;
}
