
package com.comcast.simpleadserver.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.comcast.simpleadserver.model.AdCampaign;

/**
 * Dao with database operations
 * 
 * @author nithya
 *
 */
@Repository
@DependsOn("simpleAdServerJdbcTemplate")
public class SimpleAdServerDao {

	private final String insertAdCampaign = "INSERT INTO AD_CAMPAIGN(PARTNER_ID,DURATION,AD_CONTENT, EXPIRE_DATE) VALUES (:partnerId, :duration, :adContent, :expireDate)";

	private final String selectAdCampaign = "SELECT PARTNER_ID,DURATION,AD_CONTENT, EXPIRE_DATE FROM AD_CAMPAIGN WHERE PARTNER_ID = :partnerId";

	private final String updateAdCampaign = "UPDATE AD_CAMPAIGN SET DURATION = :duration,AD_CONTENT = :adContent, EXPIRE_DATE = :expireDate WHERE PARTNER_ID = :partnerId";

	@Autowired
	@Qualifier("simpleAdServerJdbcTemplate")
	NamedParameterJdbcTemplate jdbcTemplate;

	/*
	 * Inserts the Ad Campaign in DB
	 */

	public void storeAdCampaign(AdCampaign adCampaign) {

		jdbcTemplate.update(insertAdCampaign, new BeanPropertySqlParameterSource(adCampaign));
	}

	/*
	 * Updates the Ad Campaign in DB
	 */
	public void updateAdCampaign(AdCampaign adCampaign) {
		jdbcTemplate.update(updateAdCampaign, new BeanPropertySqlParameterSource(adCampaign));
	}

	/*
	 * Retrieves the Ad Campaign
	 * 
	 * @param - partnerId
	 * 
	 * @return - AdCampaign
	 */
	public AdCampaign fetchAdCampaign(String partnerId) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource("partnerId", partnerId);
		List<AdCampaign> adCampaignList = jdbcTemplate.query(selectAdCampaign, paramSource,
				new BeanPropertyRowMapper<AdCampaign>(AdCampaign.class));
		if (adCampaignList.isEmpty()) {
			return null;
		}
		return adCampaignList.get(0);
	}

}
