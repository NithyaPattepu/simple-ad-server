
package com.comcast.simpleadserver.controller;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.comcast.simpleadserver.dao.SimpleAdServerDao;
import com.comcast.simpleadserver.model.AdCampaign;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This controller processes restful requests.
 * 
 * @author nithya
 *
 */
@RestController
public class SimpleAdServerController {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleAdServerController.class);

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Autowired
	SimpleAdServerDao simpleAdServerDao;

	/*
	 * End point to POST an Ad Campaign
	 * 
	 * @param - The input for an Ad Campaign
	 * 
	 * @return - ResponseEntity
	 */
	@PostMapping("/ad")
	public ResponseEntity<String> postAdCampaign(@RequestBody AdCampaign inputAdCampaign) {
		ResponseEntity<String> response = null;

		try {
			response = processPostAdCampaign(inputAdCampaign);
		} catch (Exception ex) {
			LOG.error("An error occured while posting AdCampaign", ex);
			response = new ResponseEntity<String>(ExceptionUtils.getRootCauseMessage(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

	/*
	 * End point to GET an Ad Campaign
	 * 
	 * @param - The partner Id of the Ad Campaign
	 * 
	 * @return - ResponseEntity
	 */
	@GetMapping("/{partnerId}")
	public ResponseEntity<String> getAdCampaign(@PathVariable("partnerId") String partnerId)
			throws JsonProcessingException {
		ResponseEntity<String> response = null;
		try {
			response = processGetAdCampaign(partnerId);
		} catch (Exception ex) {
			LOG.error("An error occured while getting AdCampaign", ex);
			response = new ResponseEntity<String>(ExceptionUtils.getRootCauseMessage(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return response;

	}

	/*
	 * Processes an Ad Campaign on GET Request. Checks if the Ad Campaign exists
	 * or not based on the partner ID. It checks the duration of the AD and
	 * indicates if the Ad is expired or active
	 * 
	 * @param - The partner Id for an Ad Campaign
	 * 
	 * @return - ResponseEntity
	 */
	private ResponseEntity<String> processGetAdCampaign(String partnerId) throws Exception {
		ResponseEntity<String> response = null;

		AdCampaign storedAdCampaign = simpleAdServerDao.fetchAdCampaign(partnerId);

		if (storedAdCampaign == null) {
			response = new ResponseEntity<String>(MAPPER.writeValueAsString("This AdCampaign does not exists"),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			if (isAdCampaignExpired(storedAdCampaign)) {
				LOG.info("Ad Campaign Expired");
				response = new ResponseEntity<String>(MAPPER.writeValueAsString("Ad Campaign Expired"),
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				LOG.info("There is an Existing Ad Campaign");

				response = new ResponseEntity<String>(MAPPER.writeValueAsString(storedAdCampaign), HttpStatus.OK);
			}
		}

		return response;
	}

	/*
	 * Processes an Ad Campaign on POST Request. Checks if the Ad Campaign
	 * exists or not based on the partner ID. It checks if the Ad Campaign
	 * exists or not and alerts the user. If Ad Campaign is expired it will
	 * update the AD Campaign
	 * 
	 * @param - The input Ad Campaign
	 * 
	 * @return - ResponseEntity
	 */
	private ResponseEntity<String> processPostAdCampaign(AdCampaign inputAdCampaign) throws Exception {
		ResponseEntity<String> responseEntity = null;

		AdCampaign storedAdCampaign = simpleAdServerDao.fetchAdCampaign(inputAdCampaign.getPartnerId());

		if (storedAdCampaign == null) {
			responseEntity = addAdCampaign(inputAdCampaign);

		} else {
			if (isAdCampaignExpired(storedAdCampaign)) {
				responseEntity = updateAdCampaign(inputAdCampaign);
			} else {
				LOG.info("This AdCampaign already exists");
				responseEntity = new ResponseEntity<String>(MAPPER.writeValueAsString("This AdCampaign already exists"),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}

		}

		return responseEntity;
	}

	/*
	 * To add an Ad Campaign on POST Request. It sets the Expire duration of the
	 * Ad and adds the Ad Campaign
	 * 
	 * @param - The input Ad Campaign
	 * 
	 * @return - Response- Added the Ad Campaign to the DB
	 */
	private ResponseEntity<String> addAdCampaign(AdCampaign inputAdCampaign) throws JsonProcessingException {
		setAdCampaignExpiryDate(inputAdCampaign);
		simpleAdServerDao.storeAdCampaign(inputAdCampaign);
		return new ResponseEntity<String>(MAPPER.writeValueAsString("Successfully posted Ad Campaign"), HttpStatus.OK);
	}

	/*
	 * To update an Ad Campaign on POST Request. It updates the Ad Campaign if
	 * it is added with the same partner Id and after the duration is expired
	 * 
	 * @param - The input Ad Campaign
	 * 
	 * @return - Response - Added the Ad Campaign to the DB
	 */
	private ResponseEntity<String> updateAdCampaign(AdCampaign inputAdCampaign) throws JsonProcessingException {
		setAdCampaignExpiryDate(inputAdCampaign);
		simpleAdServerDao.updateAdCampaign(inputAdCampaign);
		return new ResponseEntity<String>(MAPPER.writeValueAsString("Successfully posted Ad Campaign"), HttpStatus.OK);
	}

	/*
	 * To check if the Ad Campaign expired or not.
	 * 
	 * @param - The input Ad Campaign
	 * 
	 * @return - boolean
	 */
	private boolean isAdCampaignExpired(AdCampaign adCampaign) {
		Date expireDate = adCampaign.getExpireDate();
		Calendar calender = Calendar.getInstance();
		return expireDate.before(calender.getTime());
	}

	/*
	 * Sets the expire duration of the Ad.
	 * 
	 * @param - The input Ad Campaign
	 * 
	 * @return
	 */
	private void setAdCampaignExpiryDate(AdCampaign inputAdCampaign) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, Integer.valueOf(inputAdCampaign.getDuration()));
		inputAdCampaign.setExpireDate(calendar.getTime());

	}

}
