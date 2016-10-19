package com.droidablebee.springboot.rest.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class JsonDatesTest {
	private static final Logger logger = LoggerFactory.getLogger(JsonDatesTest.class);
	
	/**
	 * Use the same json parser configured by spring with automatically registered well-known modules 
	 * (i.e. jackson-datatype-jsr310: support for Java 8 Date & Time API types)
	 * http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/http/converter/json/Jackson2ObjectMapperFactoryBean.html
	 */
	@Autowired
	ObjectMapper objectMapper;
	
	@Test
	public void serialize() throws Exception {
		
		long millis = System.currentTimeMillis();
		ZonedDateTime zonedDateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault());
		
		ClassWithDates classWithDates = new ClassWithDates();
		
		classWithDates.setLocalDate(zonedDateTime.toLocalDate());
		classWithDates.setLocalDateTime(zonedDateTime.toLocalDateTime());
		classWithDates.setSqlDate(new java.sql.Date(millis));
		classWithDates.setSqlTimestamp(new Timestamp(millis));
		classWithDates.setUtilDate(new java.util.Date(millis));

		String json = objectMapper.writeValueAsString(classWithDates);
		
		if (logger.isDebugEnabled()) {
			//for some reason objectMapper.isEnabled() always returns false
//			logger.debug(String.format("serialize: %s=%s json=%s", 
//					SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, objectMapper.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS), json));
			logger.debug(String.format("serialize: json=%s", json));
		}
		
		//serialize: json={"localDate":[2016,10,19],"localDateTime":[2016,10,19,15,17,34,751000000],"utilDate":1476908254751,"sqlDate":"2016-10-19","sqlTimestamp":1476908254751}
	}

	@SuppressWarnings("unused")
	private static class ClassWithDates {

		private LocalDate localDate;
		private LocalDateTime localDateTime;
		private java.util.Date utilDate;
		private java.sql.Date sqlDate;
		private java.sql.Timestamp sqlTimestamp;
		
		public LocalDate getLocalDate() {
			return localDate;
		}
		public void setLocalDate(LocalDate localDate) {
			this.localDate = localDate;
		}
		
		public LocalDateTime getLocalDateTime() {
			return localDateTime;
		}
		public void setLocalDateTime(LocalDateTime localDateTime) {
			this.localDateTime = localDateTime;
		}
		
		public java.util.Date getUtilDate() {
			return utilDate;
		}
		public void setUtilDate(java.util.Date utilDate) {
			this.utilDate = utilDate;
		}
		
		public java.sql.Date getSqlDate() {
			return sqlDate;
		}
		public void setSqlDate(java.sql.Date sqlDate) {
			this.sqlDate = sqlDate;
		}
		
		public java.sql.Timestamp getSqlTimestamp() {
			return sqlTimestamp;
		}
		public void setSqlTimestamp(java.sql.Timestamp sqlTimestamp) {
			this.sqlTimestamp = sqlTimestamp;
		}
	}

}
