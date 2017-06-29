package com.comcast.simpleadserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * This is a configuration class
 * which defines beans
 * 
 * @author nithya
 *
 */
@Configuration
public class SimpleAdServerConfiguration {
	/*
	 * Create a new DriverManagerDataSource with the JDBC URL with default username and password
	 * @return - JdbcTemplate with a Table AD_CAMPAIGN created
	 */
	@Bean("simpleAdServerJdbcTemplate")
	public NamedParameterJdbcTemplate getSimpleAdServerJdbcTemplate() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource("jdbc:hsqldb:mem:dev", "sa", "");
    	dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
    	
    	JdbcTemplate template = new JdbcTemplate(dataSource);
    	template.execute("CREATE TABLE AD_CAMPAIGN(PARTNER_ID VARCHAR(20) UNIQUE,"
    			+ " DURATION NUMERIC(20),"
    			+ " AD_CONTENT VARCHAR(100),"
    			+ " EXPIRE_DATE TIMESTAMP)");
    	
    	return new NamedParameterJdbcTemplate(template.getDataSource());
		
	}

}
