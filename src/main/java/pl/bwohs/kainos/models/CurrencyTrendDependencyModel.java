package pl.bwohs.kainos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.enums.SlopeEnum;

public class CurrencyTrendDependencyModel implements ICurrency {
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;
	private SlopeEnum slope;
	private Map<CurrencyEnum, SlopeEnum> effect = new HashMap<>();
	
	
	
	
	
	public CurrencyTrendDependencyModel(LocalDateTime time, SlopeEnum slope, Map<CurrencyEnum, SlopeEnum> effect) {
		super();
		this.time = time;
		this.slope = slope;
		this.effect = effect;
	}
	
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public SlopeEnum getSlope() {
		return slope;
	}
	public void setSlope(SlopeEnum slope) {
		this.slope = slope;
	}
	public Map<CurrencyEnum, SlopeEnum> getEffect() {
		return effect;
	}
	public void setEffect(Map<CurrencyEnum, SlopeEnum> effect) {
		this.effect = effect;
	}
	
	

}
