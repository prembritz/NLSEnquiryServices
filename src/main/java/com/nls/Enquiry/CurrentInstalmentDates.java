package com.nls.Enquiry;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CurrentInstalmentDates {

	private LocalDate startDate;
	private LocalDate endDate;

	public CurrentInstalmentDates(LocalDate startDate, LocalDate endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String toString() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
		return ("[ " + startDate.format(fmt) + "-" + endDate.format(fmt) + "="
				+ (ChronoUnit.DAYS.between(startDate, endDate)) + " ]");
	}

	public int daysBetween() {
		return (int) ChronoUnit.DAYS.between(startDate, endDate);
	}

	public CurrentInstalmentDates() {
		// TODO Auto-generated constructor stub
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

}
