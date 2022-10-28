package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "depositAccNo", "depositTypeCode", "depositAmount", "bookingDate", "currency", "tenor",
		"maturityDate", "maturityAmount", "rateofInterest", "depositBalance", "depositDescription" })
public class DepositSummaryObject {

	@JsonbProperty("DepositAccountNumber")
	private String depositAccNo;
	
	@JsonbProperty("UnitId")
	private String unitId;

	@JsonbProperty("DepositTypeCode")
	private String depositTypeCode;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DepositAmount")
	private Double depositAmount;

	@JsonbProperty("BookingDate")
	private String bookingDate;

	@JsonbProperty("Currency")
	private String currency;

	@JsonbProperty("Tenor")
	private String tenor;

	@JsonbProperty("MaturityDate")
	private String maturityDate;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("MaturityAmount")
	private Double maturityAmount;

	@JsonbProperty("RateOfInterest")
	private Double rateofInterest;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DepositBalance")
	private Double depositBalance;

	@JsonbProperty("DepositDescription")
	private String depositDescription;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE erCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String erMsg = "Account Found";

	public DepositSummaryObject() {
		this.setDepositAccNo(depositAccNo);
		this.setDepositTypeCode(depositTypeCode);
		this.setDepositAmount(depositAmount);
		this.setBookingDate(bookingDate);
		this.setCurrency(currency);
		this.setTenor(tenor);
		this.setMaturityDate(maturityDate);
		this.setMaturityAmount(maturityAmount);
		this.setRateofInterest(rateofInterest);
		this.setDepositBalance(depositBalance);
		this.setDepositDescription(depositDescription);
		this.setUnitId(unitId);
	}

	public String getDepositAccNo() {
		return depositAccNo;
	}

	public void setDepositAccNo(String depositAccNo) {
		this.depositAccNo = depositAccNo;
	}

	public String getDepositTypeCode() {
		return depositTypeCode;
	}

	public void setDepositTypeCode(String depositTypeCode) {
		this.depositTypeCode = depositTypeCode;
	}

	public Double getDepositAmount() {
		return depositAmount;
	}

	public void setDepositAmount(Double depositAmount) {
		this.depositAmount = depositAmount;
	}

	public String getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTenor() {
		return tenor;
	}

	public void setTenor(String tenor) {
		this.tenor = tenor;
	}

	public String getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Double getMaturityAmount() {
		return maturityAmount;
	}

	public void setMaturityAmount(Double maturityAmount) {
		this.maturityAmount = maturityAmount;
	}

	public Double getRateofInterest() {
		return rateofInterest;
	}

	public void setRateofInterest(Double rateofInterest) {
		this.rateofInterest = rateofInterest;
	}

	public Double getDepositBalance() {
		return depositBalance;
	}

	public void setDepositBalance(Double depositBalance) {
		this.depositBalance = depositBalance;
	}

	public String getDepositDescription() {
		return depositDescription;
	}

	public void setDepositDescription(String depositDescription) {
		this.depositDescription = depositDescription;
	}

	public ERROR_CODE getErCode() {
		return erCode;
	}

	public void setErCode(ERROR_CODE erCode) {
		this.erCode = erCode;
	}

	public String getErMsg() {
		return erMsg;
	}

	public void setErMsg(String erMsg) {
		this.erMsg = erMsg;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	
	

}
