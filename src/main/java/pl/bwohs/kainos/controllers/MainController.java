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

@Controller
public class MainController {
	
	@Autowired
	BitcoinAverageDAO bitcoinAverageDAO;
	
	@GetMapping(value= {"/","/home"})
	public String getHomePage(Model model) {
		
		List<CurrencyStatisticsModel> btcusdList = bitcoinAverageDAO.getCurrencyHistoricalData(CurrencyEnum.BTCUSD);
		
		LocalDateTime minDataTime = btcusdList.get(btcusdList.size()-1).getTime();
		LocalDateTime maxDateTime = btcusdList.get(0).getTime();
		
		model.addAttribute("minDateTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(minDataTime));
		model.addAttribute("maxDateTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(maxDateTime));
		
		
		return "home";
	}

}
