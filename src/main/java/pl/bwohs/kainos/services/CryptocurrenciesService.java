package pl.bwohs.kainos.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.bwohs.kainos.dao.BitcoinAverageDAO;
import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;

@Service
public class CryptocurrenciesService {
	
//**********************************************************************************
//	Fields
	
	@Autowired
	private BitcoinAverageDAO bitcoinAverageDAO;
	
	private LocalDateTime lastDataTime;
	private Map<CurrencyEnum,List<CurrencyStatisticsModel>> cryptocurrencies = new HashMap<CurrencyEnum,List<CurrencyStatisticsModel>>();
	
	@Value("#{${historical.data.interval} * ${historical.data.intervalFactor}}")
	private long historicalDataInterval;
	
//**********************************************************************************
//	Getters & Setters
	public LocalDateTime getLastDataTime() {
		return lastDataTime;
	}

	public Map<CurrencyEnum, List<CurrencyStatisticsModel>> getCryptocurrencies() {
		return cryptocurrencies;
	}
	
//**********************************************************************************
//	Private methods
	@PostConstruct
	private void updateCryptocurrencies() {
		cryptocurrencies.clear();
		List<CurrencyStatisticsModel> btcusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.BTCUSD);
		List<CurrencyStatisticsModel> ethusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.ETHUSD);
		List<CurrencyStatisticsModel> ltcusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.LTCUSD);
		cryptocurrencies.put(CurrencyEnum.BTCUSD, btcusdList);
		cryptocurrencies.put(CurrencyEnum.ETHUSD, ethusdList);
		cryptocurrencies.put(CurrencyEnum.LTCUSD, ltcusdList);
		lastDataTime = cryptocurrencies.get(CurrencyEnum.BTCUSD).get(0).getTime();
	}
	
//**********************************************************************************
//	Public methods
	
	public LocalDateTime getMaxDateTime() {
		LocalDateTime maxDateTime = cryptocurrencies.get(CurrencyEnum.BTCUSD).get(0).getTime();
		return maxDateTime;
	}
	
	public String getMaxDateTimeSpaceSep() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.getMaxDateTime());
	}
	
	public LocalDateTime getMinDateTime() {
		int lastIndex = cryptocurrencies.get(CurrencyEnum.BTCUSD).size() - 1;
		LocalDateTime minDateTime = cryptocurrencies.get(CurrencyEnum.BTCUSD).get(lastIndex).getTime();
		return minDateTime;
	}
	
	public String getMinDateTimeSpaceSep() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.getMinDateTime());
	}
	
	public void checkIfCurrent() {
		LocalDateTime bitcoinAverageAPIServerTime = bitcoinAverageDAO.getServerTime();
		
		long differenceInSeconds = Duration.between(bitcoinAverageAPIServerTime, lastDataTime).abs().getSeconds();

		if(differenceInSeconds > historicalDataInterval) {
			System.out.println("Update start: " + LocalDateTime.now() + "");
			this.updateCryptocurrencies();
			System.out.println("Update end: " + LocalDateTime.now() + "");
		}else {
			System.out.println("No update needed!");
		}
	}
	
	public LocalDateTime getBitcoinAverageAPIServerTime() {
		return bitcoinAverageDAO.getServerTime();
		
	}
	
	public String getBitcoinAverageAPIServerTimeSpaceSep() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(this.getBitcoinAverageAPIServerTime());
		
	}
	
	public Map<CurrencyEnum, List<CurrencyStatisticsModel>> getCryptocurrenciesFromTo(LocalDateTime start, LocalDateTime end) {
		Map<CurrencyEnum, List<CurrencyStatisticsModel>> results = new HashMap<CurrencyEnum,List<CurrencyStatisticsModel>>();
		

		cryptocurrencies.forEach((key,value) -> {
			results.put(key, 
					value.stream().
					filter( p -> (p.getTime().isAfter(start) && p.getTime().isBefore(end)) || p.getTime().isEqual(start) || p.getTime().isEqual(end))
					.collect(Collectors.toList()));
		});
		
		return results;
	}

}
