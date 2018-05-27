package pl.bwohs.kainos.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DateTimeRangeForm {
	
	@NotNull
	private LocalDateTime start;
	@NotNull
	private LocalDateTime end;
	
	private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
	
	public LocalDateTime getStart() {
		return start;
	}
//	public void setStart(LocalDateTime start) {
//		this.start = start;
//	}
	public LocalDateTime getEnd() {
		return end;
	}
//	public void setEnd(LocalDateTime end) {
//		this.end = end;
//	}
	
	
	public void setStart(String start) {
		this.start = LocalDateTime.parse(start, FORMATTER);
	}
	
	public void setEnd(String end) {
		this.end = LocalDateTime.parse(end, FORMATTER);
	}
	
	@Override
	public String toString() {
		return "Start: " + start + ", end: " + end;
	}

}
