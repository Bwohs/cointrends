package pl.bwohs.kainos.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.enums.SlopeEnum;

public class CurrencyDependencyModel{
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime time;
	private SlopeEnum slope;
	private Map<CurrencyEnum, SlopeEnum> effects = new HashMap<>();
	
	
	
	public CurrencyDependencyModel(LocalDateTime time, SlopeEnum slope, Map<CurrencyEnum, SlopeEnum> effect) {
		super();
		this.time = time;
		this.slope = slope;
		this.effects = effect;
	}
	
	public LocalDateTime getTime() {
		return time;
	}

	public SlopeEnum getSlope() {
		return slope;
	}

	public Map<CurrencyEnum, SlopeEnum> getEffects() {
		return effects;
	}

	
	

}
