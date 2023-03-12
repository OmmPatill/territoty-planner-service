package com.planner.territory.model.globalsequence;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author shubham shinde
 * store sheetname configration details
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Configuration
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
public class GlobalSequenceConstants {

	@Value("${global.sequencing.LOCATIONS}")
	private String LOCATIONS;
	@Value("${global.sequencing.DISTANCE_TYPE}")
	private String DISTANCE_TYPE;
	@Value("${global.sequencing.DISTANCE_MATRIX}")
	private String DISTANCE_MATRIX;
	@Value("${global.sequencing.START_LOCATION}")
	private String START_LOCATION;
	@Value("${global.sequencing.K_OPT_METHOD1_ITERATION}")
	private int K_OPT_METHOD1_ITERATION;
	@Value("${global.sequencing.K_OPT_METHOD3_ITERATION}")
	private int K_OPT_METHOD3_ITERATION;
	@Value("${global.sequencing.K_OPT_METHOD4_ITERATION}")
	private int K_OPT_METHOD4_ITERATION;
	@Value("${global.sequencing.OSRM_TABLE_SECONDARY_URL}")
	private String OSRM_TABLE_SECONDARY_URL;
	@Value("${OSRM.BASE.URL}")
	private String OSRM_BASE_URL;
	@Value("${OSRM.SET.VALUE}")
	private int OSRM_SET_VALUE;
	@Value("${OSRM.country}")
	private String country;
	
	
}
