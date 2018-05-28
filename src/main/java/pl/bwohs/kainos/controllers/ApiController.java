package pl.bwohs.kainos.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.bwohs.kainos.dao.BitcoinAverageDAO;
import pl.bwohs.kainos.dto.DateTimeRangeForm;
import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.models.CurrenciesModel;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;
import pl.bwohs.kainos.services.CryptocurrenciesService;
import pl.bwohs.kainos.validators.DateTimeRangeFormValidator;


@RestController
@RequestMapping(value="/api")
public class ApiController {
	
	@Autowired
	CryptocurrenciesService cryptocurrenciesService;
	
	@Autowired
	DateTimeRangeFormValidator DTRFValidator;
	
	
	@CrossOrigin
	@GetMapping(value="/historical/start/{start}/end/{end}")
	public ResponseEntity<?> getCurrenciesHistoricalData(
			@ModelAttribute DateTimeRangeForm DTRFrom,
			BindingResult result){

		DTRFValidator.validate(DTRFrom, result);

		if (result.hasErrors()) {
		    return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
		}else {
			cryptocurrenciesService.checkIfCurrent();
			
			Map<CurrencyEnum, List<CurrencyStatisticsModel>> limitedCurrenciesData = cryptocurrenciesService.getCryptocurrenciesFromTo(DTRFrom.getStart(), DTRFrom.getEnd());
			

			return new ResponseEntity<>(limitedCurrenciesData, HttpStatus.OK);
		}
		
	}
	
}
