package com.planner.territory.proccessor.globalsequence.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpUrlProcessor extends Exception {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	/***
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	
	// TODO Update this method to supply RhythmDataModel as both Input and output
	// we can supply lambda function as a part of input..which will convert Output to RhythmDataModel
	public StringBuffer getOSRMMatrix(StringBuilder url) throws Exception {
		log.info("url is " + url);
		try {
			
			URL url1 = new URL(url.toString());
			HttpURLConnection urlconnection = null;
			urlconnection = (HttpURLConnection) url1.openConnection();
			urlconnection.setRequestMethod("GET");
			urlconnection.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(urlconnection.getOutputStream());
			out.close();

			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response;

		} catch (Exception e) {
			throw new Exception(e.getMessage() + "OSRM Request fail please check servers");
		}
	}

}
