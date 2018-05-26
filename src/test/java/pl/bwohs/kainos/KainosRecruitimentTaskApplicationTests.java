package pl.bwohs.kainos;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KainosRecruitimentTaskApplicationTests {

	@Test
	public void testCurrencyModelMappingFromJson() {
		
//		LocalDateTime dateTime = LocalDateTime.of(2018, 5, 26, 18, 13, 0);
//		
//		
//		CurrencyModel expected = new CurrencyModel();
//		expected.setTime(dateTime);
//		expected.setHigh(new BigDecimal("15.33333"));
//		expected.setAverage(new BigDecimal("7.5"));
//		expected.setLow(new BigDecimal("12.02222"));
//		expected.setOpen(new BigDecimal("3.543"));
//		expected.setVolume(new BigDecimal("29.789"));
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected);
//			System.out.println(json);
//		} catch (JsonProcessingException e) {
//			
//			e.printStackTrace();
//		}
//		
//		CurrencyModel actual = new CurrencyModel();
//		
//		assertEquals(expected, actual);
		
	}

}
