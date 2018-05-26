package pl.bwohs.kainos.models;

import java.math.BigDecimal;

import pl.bwohs.kainos.enums.CurrencyEnum;

public class CurrencyModel {
	
	private CurrencyEnum name;
	private BigDecimal value;
	
	public CurrencyModel(CurrencyEnum name, BigDecimal value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public CurrencyEnum getName() {
		return name;
	}
	public void setName(CurrencyEnum name) {
		this.name = name;
	}

	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Name: " + this.name + ", average value: " + this.value;
	}
	

}
