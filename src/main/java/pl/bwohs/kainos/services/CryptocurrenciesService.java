package pl.bwohs.kainos.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pl.bwohs.kainos.dao.BitcoinAverageDAO;
import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.enums.SlopeEnum;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;
import pl.bwohs.kainos.models.CurrencyDependencyModel;
import pl.bwohs.kainos.models.CurrencyTrendModel;
import pl.bwohs.kainos.models.ICurrency;
import pl.bwohs.kainos.utilities.Json;

@Service
public class CryptocurrenciesService {
	
//**********************************************************************************
//	Fields
	
	@Autowired
	private BitcoinAverageDAO bitcoinAverageDAO;
	
	private LocalDateTime lastDataTime;
	private Map<CurrencyEnum,List<CurrencyStatisticsModel>> cryptocurrencies = new HashMap<>();
	
	
	
	@Value("#{${historical.data.interval} * ${historical.data.intervalFactor}}")
	private long historicalDataInterval;
	
	@Value("${trend.timespan}")
	private long trendTimespan;
	
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
	
	private Map<CurrencyEnum,List<CurrencyTrendModel>> calculateTrends(LocalDateTime start, LocalDateTime end) {
		Map<CurrencyEnum,List<CurrencyTrendModel>> trendsResult = new HashMap<>();
				
		cryptocurrencies.forEach((key,value) -> {
//			System.out.println("Key: " + key);
			trendsResult.put(key, calculateTrendLines(value,start,end));
//			System.out.println("Key end -----------------------------");
		});
				
		return trendsResult;
	
	}
	
	private List<CurrencyTrendModel> calculateTrendLines(List<CurrencyStatisticsModel> value,
			LocalDateTime startScope,
			LocalDateTime endScope) {
		
		List<CurrencyTrendModel> trendLinesResult = new LinkedList<>();
		
//		System.out.println("\tStart scope: " + startScope + ", end scope: " + endScope);
		
		LocalDateTime end = endScope;
		LocalDateTime start = end.minusSeconds(trendTimespan);
		while(start.isAfter(startScope) || start.isEqual(startScope)) {
//			System.out.println("\tStart: " + start + ", end: " + end);

			
			trendLinesResult.add(calculateTrendLine(value,start,end));

			end = start;
			start = end.minusSeconds(trendTimespan);
		}
		
		
		return trendLinesResult;
	}
	
	
	private CurrencyTrendModel calculateTrendLine(List<CurrencyStatisticsModel> value,
			LocalDateTime start,
			LocalDateTime end) {
		
		List<CurrencyStatisticsModel> list = value.stream()
				.filter( p -> (p.getTime().isAfter(start) && p.getTime().isBefore(end)) || p.getTime().isEqual(end))
				.collect(Collectors.toList());
		
		LocalDateTime min = list.stream().map(x -> x.getTime()).min(LocalDateTime::compareTo).get();

		
		ZoneOffset offset = OffsetDateTime.now().getOffset();
			
		SimpleRegression simpleRegression = new SimpleRegression();
		for (CurrencyStatisticsModel currency : list) {
			simpleRegression.addData(currency.getTime().toEpochSecond(offset), currency.getAverage().doubleValue());
		}

		double b = simpleRegression.getIntercept();
		double a = simpleRegression.getSlope();
		
		SlopeEnum slope;
		if (a > 0) slope = SlopeEnum.rising;
		else if(a < 0) slope = SlopeEnum.falling;
		else slope = SlopeEnum.constant;
		
//		System.out.println("a2: " + a + ", b2: " + b);

		double y1 = simpleRegression.predict(start.toEpochSecond(offset));
		double y2 = simpleRegression.predict(end.toEpochSecond(offset));

		CurrencyTrendModel trendLine = new CurrencyTrendModel(start, end, BigDecimal.valueOf(y1), BigDecimal.valueOf(y2), slope, min);
		
//		System.out.println("\t\tt1: " + list.get(list.size()-1).getTime() + ", value: " + list.get(list.size()-1).getAverage() + ", t2: " + list.get(0).getTime() + ", value: " + list.get(0).getAverage());
//		System.out.println(trendLine.toString());
		
		System.out.println("Liczba pomiarów do regresji liniowej: " + list.size());
		return trendLine;
	}
	
	private Map<CurrencyEnum,List<CurrencyDependencyModel>> calculateCryptocurrenciesDependencies(Map<CurrencyEnum,List<CurrencyTrendModel>> trends){
		Map<CurrencyEnum,List<CurrencyDependencyModel>> cryptocurrenciesDependencies = new HashMap<>();
		
		trends.forEach((key,value)->{
			cryptocurrenciesDependencies.put(key, calculateCryptocurrencyDependencies(key,trends));
		});
		
		return cryptocurrenciesDependencies;
	}
	
	private List<CurrencyDependencyModel> calculateCryptocurrencyDependencies(CurrencyEnum key, Map<CurrencyEnum,List<CurrencyTrendModel>> trends){
		List<CurrencyDependencyModel> cryptocurrencyDependencies = new LinkedList<>();
		
//		System.out.println("\tKey: " + key);
		
		trends.get(key).forEach((item)->{
			cryptocurrencyDependencies.add(calculateCryptocurrencyDependency(item,
					trends.entrySet().stream()
					.filter( x -> !(x.getKey().equals(key)))
					.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()))));
			
		});
		
		
		
		
		return cryptocurrencyDependencies;
	}
	
	private CurrencyDependencyModel calculateCryptocurrencyDependency(CurrencyTrendModel item, Map<CurrencyEnum,List<CurrencyTrendModel>> trendsLimited) {

		Map<CurrencyEnum, SlopeEnum> effects = new HashMap<>();
		trendsLimited.forEach((key,value)->{

			effects.put(key, value.stream()
									.filter( x -> x.getOldestMeasureTime().isEqual(item.getOldestMeasureTime()))
									.findAny()
									.get()
									.getSlope()
						);
		});
		
		CurrencyDependencyModel currencyDependency = new CurrencyDependencyModel();
		
		currencyDependency.setTime(item.getOldestMeasureTime());
		currencyDependency.setSlope(item.getSlope());
		currencyDependency.setEffects(effects);
		
		return currencyDependency;
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
	
	private Map<CurrencyEnum, List<CurrencyStatisticsModel>> getCryptocurrenciesFromTo(LocalDateTime start, LocalDateTime end) {
		Map<CurrencyEnum, List<CurrencyStatisticsModel>> results = new HashMap<>();
		

		cryptocurrencies.forEach((key,value) -> {
			results.put(key, 
					value.stream()
					.filter( p -> (p.getTime().isAfter(start) && p.getTime().isBefore(end)) || p.getTime().isEqual(start) || p.getTime().isEqual(end))
					.collect(Collectors.toList()));
		});

		
		return results;
	}
	

	


	public String getJsonData(LocalDateTime start, LocalDateTime end) {
		
		this.checkIfCurrent();
		Map<CurrencyEnum, List<CurrencyStatisticsModel>> cryptocurrenciesLimited = this.getCryptocurrenciesFromTo(start, end);
		Map<CurrencyEnum,List<CurrencyTrendModel>> trends = this.calculateTrends(start, end);
		Map<CurrencyEnum,List<CurrencyDependencyModel>> cryptocurrenciesDependencies = this.calculateCryptocurrenciesDependencies(trends);
		
		String jsonDataString = Json.objectToJson(cryptocurrenciesLimited);
		String jsonTrendString = Json.objectToJson(trends);
		String jsonCryptocurrenciesDependenciesString = Json.objectToJson(cryptocurrenciesDependencies);
		
		StringBuilder jsonAllStringBuilder = new StringBuilder("{");
		
		jsonAllStringBuilder.append("\"CRYPTOCURRENCIES\" : ");
		jsonAllStringBuilder.append(jsonDataString);
		jsonAllStringBuilder.append(",");
		
		jsonAllStringBuilder.append("\"TRENDS\" : ");
		jsonAllStringBuilder.append(jsonTrendString);
		jsonAllStringBuilder.append(",");
		
		jsonAllStringBuilder.append("\"DEPENDENCIES\" : ");
		jsonAllStringBuilder.append(jsonCryptocurrenciesDependenciesString);
		
		jsonAllStringBuilder.append("}");
		
//		System.out.println(jsonAllStringBuilder.toString());
		
	
		return jsonAllStringBuilder.toString();
		
	}

}
