package pl.bwohs.kainos.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pl.bwohs.kainos.dao.BitcoinAverageDAO;
import pl.bwohs.kainos.enums.CurrencyEnum;
import pl.bwohs.kainos.models.CurrencyStatisticsModel;
import pl.bwohs.kainos.services.CryptocurrenciesService;

@Controller
public class MainController {
	
	@Autowired
	CryptocurrenciesService cryptocurrenciesService;
	
	@GetMapping(value= {"/","/home"})
	public String getHomePage(Model model) {
		
		cryptocurrenciesService.checkIfCurrent();
		
		model.addAttribute("minDateTime", cryptocurrenciesService.getMinDateTimeSpaceSep());
		model.addAttribute("maxDateTime", cryptocurrenciesService.getBitcoinAverageAPIServerTimeSpaceSep());
		
		return "home";
	}

}
