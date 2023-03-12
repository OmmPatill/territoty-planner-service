package com.planner.territory.model.globalsequence;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Customer {

    private int customerId;
    private String customerCode;
    private double latitude;
    private double longitude;

}
