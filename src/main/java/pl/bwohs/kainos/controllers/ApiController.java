package pl.bwohs.kainos.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.bwohs.kainos.dao.BitcoinAverageDAO;
import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.models.CurrenciesModel;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;


@RestController
@RequestMapping(value="/api")
public class ApiController {

	@Autowired
	BitcoinAverageDAO bitcoinAverageDAO;
	
	@GetMapping(value="/historical/start/{start}/stop/{stop}")
	public ResponseEntity<?> getCurrenciesHistoricalData(@PathVariable String start, @PathVariable String stop){

		List<CurrencyStatisticsModel> btcusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.BTCUSD);
		List<CurrencyStatisticsModel> ethusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.ETHUSD);
		List<CurrencyStatisticsModel> ltcusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.LTCUSD);	
		
		List<List<CurrencyStatisticsModel>> currenciesList = new ArrayList<List<CurrencyStatisticsModel>>();
		currenciesList.add(btcusdList);
		currenciesList.add(ethusdList);
		currenciesList.add(ltcusdList);
		
		
		return new ResponseEntity<>(currenciesList, HttpStatus.OK);

	}
	
}
