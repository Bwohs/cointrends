package pl.bwohs.kainos.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;

@Repository
public class BitcoinAverageDAO {
	
	@Value("${bitcoinaverage.api.history1}")
	private String apiUrlHistory1;
	
	@Value("${bitcoinaverage.api.history2}")
	private String apiUrlHistory2;
	
	@Value("${bitcoinaverage.api.serverTime}")
	private String apiUrlServerTime;
	
	public List<CurrencyStatisticsModel> getCurrencyHistoricalData(CurrencyEnum currency){
		
		String urlRequest = apiUrlHistory1 + currency + apiUrlHistory2;
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<List<CurrencyStatisticsModel>> response =
		        restTemplate.exchange(urlRequest, HttpMethod.GET, null, new ParameterizedTypeReference<List<CurrencyStatisticsModel>>() {});
		
		List<CurrencyStatisticsModel> currencyList = response.getBody();
		
		return currencyList;
		
	}
	
	public LocalDateTime getServerTime() {
		
		String urlRequest = apiUrlServerTime;
		
		RestTemplate restTemplate = new RestTemplate();
		
		Map<String, Object> res = restTemplate.getForObject(urlRequest, Map.class, new HashMap<>());
		
		LocalDateTime serverTime = LocalDateTime.parse((CharSequence) res.get("iso"));
		
		return serverTime;
		
	}

}
