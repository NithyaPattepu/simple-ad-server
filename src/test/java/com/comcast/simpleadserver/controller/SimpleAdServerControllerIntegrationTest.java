package com.comcast.simpleadserver.controller;

import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.comcast.simpleadserver.main.SimpleAdServerApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleAdServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleAdServerControllerIntegrationTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void adCampaignIntergationTest() throws Exception {
		String input = "{\"partner_id\": \"1234\",\"duration\": \"60\",\"ad_content\": \"ad content\"}";
		JsonNode jsonNode = MAPPER.readTree(input);

		// Checking if an ad campaign exists
		makeGetRestCallAndValidate("1234",HttpStatus.INTERNAL_SERVER_ERROR, null, "This AdCampaign does not exists");
		

		// Adding an ad campaign
		makePostRestCallAndValidate(HttpStatus.OK, jsonNode, "Successfully posted Ad Campaign");
		
		// Checking if the added ad campaign exists or not
		makeGetRestCallAndValidate("1234",HttpStatus.OK, jsonNode, null);
		
		//Posting the same ad campaign again before it expires
		makePostRestCallAndValidate(HttpStatus.INTERNAL_SERVER_ERROR, jsonNode, "This AdCampaign already exists");
		
		//Sleeping so that ad campaign expires
		Thread.sleep(60000);
        
		//Checking if the ad campaign is expired or not
		makeGetRestCallAndValidate("1234",HttpStatus.INTERNAL_SERVER_ERROR, null, "Ad Campaign Expired");
		
		//Posting same ad campaign after the existing ad campaign expired 
		makePostRestCallAndValidate(HttpStatus.OK, jsonNode, "Successfully posted Ad Campaign");
		
		// Checking if the added ad campaign exists or not
		makeGetRestCallAndValidate("1234",HttpStatus.OK, jsonNode, null);
		

	}
	
	private void makeGetRestCallAndValidate(String partnerId, HttpStatus httpStatus,
			JsonNode jsonNode, String message) throws JsonProcessingException, JSONException {
		ResponseEntity<Object> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/simpleadserver/"+partnerId,
				Object.class);
		validateRespone(responseEntity, httpStatus, jsonNode, message);
	}
	
	
	private void makePostRestCallAndValidate(HttpStatus httpStatus,
			JsonNode jsonNode, String message) throws JsonProcessingException, JSONException {
		ResponseEntity<Object> responseEntity = restTemplate.postForEntity("http://localhost:" + port + "/simpleadserver/ad", jsonNode,
				Object.class);
		
		validateRespone(responseEntity, httpStatus, jsonNode, message);
	}
	

	
	private void validateRespone(ResponseEntity<Object> responseEntity, HttpStatus httpStatus, JsonNode jsonNode,
			String message) throws JsonProcessingException, JSONException {
		
		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(httpStatus);

		if (message != null) {
			Assertions.assertThat(responseEntity.getBody()).isEqualTo(message);
		}  else if (jsonNode != null) {
			JSONAssert.assertEquals(MAPPER.writeValueAsString(responseEntity.getBody()),
					MAPPER.writeValueAsString(jsonNode), false);
		}
		
	}

	

}
