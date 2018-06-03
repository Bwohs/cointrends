package pl.bwohs.kainos.models;

import java.math.BigDecimal;
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
		this.value1 = value1;
		this.value2 = value2;
		this.slope = slope;
		this.oldestMeasureTime = oldestMeasureTime;
	}

	public LocalDateTime getT1() {
		return t1;
	}
	public void setT1(LocalDateTime t1) {
		this.t1 = t1;
	}

	public LocalDateTime getT2() {
		return t2;
	}
	public void setT2(LocalDateTime t2) {
		this.t2 = t2;
	}

	public BigDecimal getValue1() {
		return value1;
	}
	public void setValue1(BigDecimal value1) {
		this.value1 = value1;
	}
	
	public BigDecimal getValue2() {
		return value2;
	}
	public void setValue2(BigDecimal value2) {
		this.value2 = value2;
	}

	public SlopeEnum getSlope() {
		return slope;
	}

	public void setSlope(SlopeEnum slope) {
		this.slope = slope;
	}
	
	public LocalDateTime getOldestMeasureTime() {
		return oldestMeasureTime;
	}

	public void setOldestMeasureTime(LocalDateTime oldestMeasureTime) {
		this.oldestMeasureTime = oldestMeasureTime;
	}

	@Override
	public String toString() {
		return "t1: " + t1 + ", value: " + value1 + ", t2: " + t2 + ", value: " + value2 + ", slope: " + slope;
	}
	
}
