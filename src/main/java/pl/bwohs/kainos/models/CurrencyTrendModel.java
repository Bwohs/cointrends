package pl.bwohs.kainos.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import pl.bwohs.kainos.enums.SlopeEnum;

public class CurrencyTrendModel implements ICurrency {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime t1;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime t2;
	@JsonIgnore
	private LocalDateTime oldestMeasureTime;
	
	private int decimalPlaces = 4;
	
	private BigDecimal value1;
	private BigDecimal value2;
	private SlopeEnum slope;
	
	public CurrencyTrendModel() {
		super();
	}
	
	public CurrencyTrendModel(LocalDateTime t1, LocalDateTime t2, BigDecimal value1, BigDecimal value2,
			SlopeEnum slope, LocalDateTime oldestMeasureTime) {
		super();
		this.t1 = t1;
		this.t2 = t2;
		this.value1 = value1.setScale(decimalPlaces, RoundingMode.HALF_UP);
		this.value2 = value2.setScale(decimalPlaces, RoundingMode.HALF_UP);
		this.slope = slope;
		this.oldestMeasureTime = oldestMeasureTime;
	}

	public LocalDateTime getT1() {
		return t1;
	}

	public LocalDateTime getT2() {
		return t2;
	}

	public BigDecimal getValue1() {
		return value1;
	}
	
	public BigDecimal getValue2() {
		return value2;
	}

	public SlopeEnum getSlope() {
		return slope;
	}

	public LocalDateTime getOldestMeasureTime() {
		return oldestMeasureTime;
	}



	@Override
	public String toString() {
		return "t1: " + t1 + ", value: " + value1 + ", t2: " + t2 + ", value: " + value2 + ", slope: " + slope;
	}
	
}
