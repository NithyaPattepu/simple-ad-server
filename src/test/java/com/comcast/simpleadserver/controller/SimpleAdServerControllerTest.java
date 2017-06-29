package com.comcast.simpleadserver.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.comcast.simpleadserver.dao.SimpleAdServerDao;
import com.comcast.simpleadserver.model.AdCampaign;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleAdServerControllerTest {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final class Mocking {

		private MockMvc mockMvc;

		@Mock
		SimpleAdServerDao simpleAdServerDao;

		@InjectMocks
		SimpleAdServerController simpleAdServerController;

		private Mocking() {
			initMocks(this);
			mockMvc = MockMvcBuilders.standaloneSetup(simpleAdServerController).build();
		}
	}

	@Test
	public void getAdCampaign_DoesNotExists() throws Exception {
		Mocking m = new Mocking();
		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(null);

		m.mockMvc.perform(get("/{partnerId}", "1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString("This AdCampaign does not exists")));

	}

	@Test
	public void getAdCampaign_Exipired() throws Exception {
		Mocking m = new Mocking();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -10);

		AdCampaign adCampaign = new AdCampaign();
		adCampaign.setPartnerId("3456");
		adCampaign.setDuration("10");
		adCampaign.setExpireDate(calendar.getTime());

		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(adCampaign);

		m.mockMvc.perform(get("/{partnerId}", "3456")).andExpect(status().isInternalServerError())
				.andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString("Ad Campaign Expired")));

	}

	@Test
	public void getAdCampaign_Valid() throws Exception {
		Mocking m = new Mocking();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 30);

		AdCampaign adCampaign = new AdCampaign();
		adCampaign.setPartnerId("3456");
		adCampaign.setDuration("30");
		adCampaign.setExpireDate(calendar.getTime());

		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(adCampaign);

		m.mockMvc.perform(get("/{partnerId}", "3456")).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().json(MAPPER.writeValueAsString(adCampaign)));

	}

	@Test
	public void postAdCampaign_DoesNotExists() throws Exception {
		Mocking m = new Mocking();

		AdCampaign adCampaign = new AdCampaign();
		adCampaign.setPartnerId("3456");
		adCampaign.setDuration("30");

		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(null);
		doNothing().when(m.simpleAdServerDao).storeAdCampaign(Mockito.any(AdCampaign.class));
		
		m.mockMvc
				.perform(post("/ad").contentType(MediaType.APPLICATION_JSON)
						.content(MAPPER.writeValueAsString(adCampaign)))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString("Successfully posted Ad Campaign")));

	}
	
	@Test
	public void postAdCampaign_Exipired() throws Exception {
		Mocking m = new Mocking();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, -10);

		AdCampaign adCampaign = new AdCampaign();
		adCampaign.setPartnerId("3456");
		adCampaign.setDuration("10");
		adCampaign.setExpireDate(calendar.getTime());

		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(adCampaign);
		doNothing().when(m.simpleAdServerDao).updateAdCampaign(Mockito.any(AdCampaign.class));

		m.mockMvc.perform(post("/ad").contentType(MediaType.APPLICATION_JSON)
				.content(MAPPER.writeValueAsString(adCampaign)))
		.andExpect(status().isOk())
		.andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString("Successfully posted Ad Campaign")));

	}
	
	@Test
	public void postAdCampaign_NotExipired() throws Exception {
		Mocking m = new Mocking();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 30);

		AdCampaign adCampaign = new AdCampaign();
		adCampaign.setPartnerId("3456");
		adCampaign.setDuration("30");
		adCampaign.setExpireDate(calendar.getTime());

		when(m.simpleAdServerDao.fetchAdCampaign(Mockito.anyString())).thenReturn(adCampaign);
		doNothing().when(m.simpleAdServerDao).updateAdCampaign(Mockito.any(AdCampaign.class));

		m.mockMvc.perform(post("/ad").contentType(MediaType.APPLICATION_JSON)
				.content(MAPPER.writeValueAsString(adCampaign)))
		.andExpect(status().isInternalServerError())
		.andExpect(MockMvcResultMatchers.content().string(MAPPER.writeValueAsString("This AdCampaign already exists")));

	}
}
