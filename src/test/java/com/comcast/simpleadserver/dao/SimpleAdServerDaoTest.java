package com.comcast.simpleadserver.dao;

import static org.mockito.MockitoAnnotations.initMocks;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.comcast.simpleadserver.model.AdCampaign;

public class SimpleAdServerDaoTest {
	
	SimpleAdServerDao simpleAdServerDao;

	private static final class Mocking {

		@Mock
		NamedParameterJdbcTemplate jdbcTemplate;

		@InjectMocks
		SimpleAdServerDao simpleAdServerDao;

		private Mocking() {
			initMocks(this);
		}
	}

	@Test
	public void fetchAdCampaign() {
		Mocking m = new Mocking();
		AdCampaign adCampaign = m.simpleAdServerDao.fetchAdCampaign(new String());
		Assertions.assertThat(adCampaign).isNull();

	}

	

}
