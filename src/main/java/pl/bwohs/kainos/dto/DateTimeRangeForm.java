package pl.bwohs.kainos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;


public class DateTimeRangeForm {
	
	private LocalDateTime start;
	private LocalDateTime end;
	
	private final DateTimeFormatter FORMATTER_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(String start) {
		try {
			this.start = LocalDateTime.parse(start, FORMATTER_T);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(String end) {
		try {
			this.end = LocalDateTime.parse(end, FORMATTER_T);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public String toString() {
		return "Start: " + start + ", end: " + end;
	}

}
