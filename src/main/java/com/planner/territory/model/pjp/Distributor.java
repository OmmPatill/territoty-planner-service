package com.planner.territory.model.pjp;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Distributor {
    private String distributorCode;
    private double latitude;
    private double longitude;
    private int travellingSpeed;
    private int distributorId;

}
