package pl.bwohs.kainos.validators;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import pl.bwohs.kainos.dto.DateTimeRangeForm;
import pl.bwohs.kainos.services.CryptocurrenciesService;

@Component
public class DateTimeRangeFormValidator implements Validator{

	@Autowired
	CryptocurrenciesService cryptocurrenciesService;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return DateTimeRangeForm.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		
		DateTimeRangeForm dateTimeRangeForm = (DateTimeRangeForm) object;
		
		LocalDateTime min = cryptocurrenciesService.getMinDateTime();
		LocalDateTime start = dateTimeRangeForm.getStart();
		
		LocalDateTime max = cryptocurrenciesService.getBitcoinAverageAPIServerTime();
		LocalDateTime end = dateTimeRangeForm.getEnd();
		

		if (start == null) {
			errors.rejectValue("start", null, "Date time must be in format 'YYYY-MM-DD HH:mm:ss'");
		}else if (start.isBefore(min)) {
			errors.rejectValue("start", null, "Start date time must be after '" + min + "'");
		}

		if (end == null) {
			errors.rejectValue("end", null, "Date time must be in format 'YYYY-MM-DD HH:mm:ss'");
		}else if (end.isAfter(max)) {
			errors.rejectValue("end", null, "End date time must be before '" + max + "'");
		}
		
		if (start != null && end != null && start.isAfter(end)) {
			errors.rejectValue("start", null, "Start date time must be before end date time");
		}
	}

}
