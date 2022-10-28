package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "depositAccNo", "depositCurrency", "deposittype",
		"startdate", "originalprincipalamount", "tenor", "maturitydate", "maturityamount", "interestonmaturity",
		"rateofinterest", "depositprincipalaccountNumber", "interestsettlementaccount", "fundingaccountnumber"})
public class DepositDetailsObject {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("DepositAccountNumber")
	private String depositAccNo;

	@JsonbProperty("DepositCurrency")
	private String depositCurrency;

	@JsonbProperty("DepositType")
	private String depositType;

	@JsonbProperty("Startdate")
	private String startDate;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OriginalPrincipalAmount")
	private Double originalPrincipalAmount;

	@JsonbProperty("Tenor")
	private String tenor;

	@JsonbProperty("MaturityDate")
	private String maturityDate;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("MaturityAmount")
	private Double maturityAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("InterestonMaturity")
	private Double interestonMaturity;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("RateofInterest")
	private Double rateofInterest;

	@JsonbProperty("DepositPrincipalAccountNumber")
	private String depositPrincipalAccountNumber;

	@JsonbProperty("InterestsettlementAccount")
	private String interestSettlementAccount;

	@JsonbProperty("FundingAccountNumber")
	private String fundingAccountNumber;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public DepositDetailsObject() {
		this.setUnitId(unitId);
		this.setDepositAccNo(depositAccNo);
		this.setDepositType(depositType);
		this.setStartDate(startDate);
		this.setOriginalPrincipalAmount(originalPrincipalAmount);
		this.setTenor(tenor);
		this.setMaturityDate(maturityDate);
		this.setMaturityAmount(maturityAmount);
		this.setInterestonMaturity(interestonMaturity);
		this.setRateofInterest(rateofInterest);
		this.setDepositPrincipalAccountNumber(depositPrincipalAccountNumber);
		this.setInterestSettlementAccount(interestSettlementAccount);
		this.setFundingAccountNumber(fundingAccountNumber);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getDepositAccNo() {
		return depositAccNo;
	}

	public void setDepositAccNo(String depositAccNo) {
		this.depositAccNo = depositAccNo;
	}

	public String getDepositcurrency() {
		return depositCurrency;
	}

	public void setDepositcurrency(String depositCurrency) {
		this.depositCurrency = depositCurrency;
	}

	public String getDepositType() {
		return depositType;
	}

	public void setDepositType(String depositType) {
		this.depositType = depositType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public Double getOriginalPrincipalAmount() {
		return originalPrincipalAmount;
	}

	public void setOriginalPrincipalAmount(Double originalPrincipalAmount) {
		this.originalPrincipalAmount = originalPrincipalAmount;
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

	public Double getInterestonMaturity() {
		return interestonMaturity;
	}

	public void setInterestonMaturity(Double interestonMaturity) {
		this.interestonMaturity = interestonMaturity;
	}

	public Double getRateofInterest() {
		return rateofInterest;
	}

	public void setRateofInterest(Double rateofInterest) {
		this.rateofInterest = rateofInterest;
	}

	public String getDepositPrincipalAccountNumber() {
		return depositPrincipalAccountNumber;
	}

	public void setDepositPrincipalAccountNumber(String depositPrincipalAccountNumber) {
		this.depositPrincipalAccountNumber = depositPrincipalAccountNumber;
	}

	public String getInterestSettlementAccount() {
		return interestSettlementAccount;
	}

	public void setInterestSettlementAccount(String interestSettlementAccount) {
		this.interestSettlementAccount = interestSettlementAccount;
	}

	public String getFundingAccountNumber() {
		return fundingAccountNumber;
	}

	public void setFundingAccountNumber(String fundingAccountNumber) {
		this.fundingAccountNumber = fundingAccountNumber;
	}

	public ERROR_CODE getErrCode() {
		return errCode;
	}

	public void setErrCode(ERROR_CODE errCode) {
		this.errCode = errCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

}
