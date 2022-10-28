package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable
@JsonbPropertyOrder({ "customerId", "year", "month", "accountNumber", "monthlyStatementCount" })
public class AccountCardEstatementObject {

	@JsonbProperty("CustomerId")
	private String customerId;

	@JsonbProperty("Year")
	private String year;

	@JsonbProperty("Month")
	private String month;

	@JsonbProperty("AccountNumber")
	private String accountNumber;

	@JsonbProperty("MonthlyStatementCount")
	public int monthlyStatementCount;

	public AccountCardEstatementObject() {
		this.setCustomerId(customerId);
		this.setYear(year);
		this.setMonth(month);
		this.setAccountNumber(accountNumber);
		this.setMonthlyStatementCount(monthlyStatementCount);
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public int getMonthlyStatementCount() {
		return monthlyStatementCount;
	}

	public void setMonthlyStatementCount(int monthlyStatementCount) {
		this.monthlyStatementCount = monthlyStatementCount;
	}
	
	

}
