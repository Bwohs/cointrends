package pl.bwohs.kainos.models;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;


public class CurrenciesModel {
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime time;
	private List<CurrencyModel> curriencies = new LinkedList<CurrencyModel>();
	
	public LocalDateTime getTime() {
		return time;
	}
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	public List<CurrencyModel> getCurriencies() {
		return curriencies;
	}
	public void setCurriencies(List<CurrencyModel> curriencies) {
		this.curriencies = curriencies;
	}
	public boolean addCurrency(CurrencyModel currencyModel) {
		return curriencies.add(currencyModel);
	}

}
