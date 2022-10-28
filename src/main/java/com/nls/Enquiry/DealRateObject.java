package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitID", "cif", "dealReferenceNo", "dealRate", "DealDate",
		"DealCurrency", "DealCurrencyTo", "dealBookingAmount" })
public class DealRateObject {

	@JsonbProperty("UnitID")
	private String unitID;

	@JsonbProperty("CIF")
	private String cif;

	@JsonbProperty("DealReferenceNo")
	private String dealReferenceNo;

	@JsonbProperty("DealRate")
	private String dealRate;

	@JsonbProperty("dealDate")
	private String DealDate;

	@JsonbProperty("dealCurrency")
	private String DealCurrency;

	@JsonbProperty("dealCurrencyTo")
	private String DealCurrencyTo;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DealBookingAmount")
	private Double dealBookingAmount;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public String getUnitID() {
		return unitID;
	}

	public void setUnitID(String unitID) {
		this.unitID = unitID;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getDealReferenceNo() {
		return dealReferenceNo;
	}

	public void setDealReferenceNo(String dealReferenceNo) {
		this.dealReferenceNo = dealReferenceNo;
	}

	public String getDealRate() {
		return dealRate;
	}

	public void setDealRate(String dealRate) {
		this.dealRate = dealRate;
	}

	public String getDealDate() {
		return DealDate;
	}

	public void setDealDate(String dealDate) {
		DealDate = dealDate;
	}

	public String getDealCurrency() {
		return DealCurrency;
	}

	public void setDealCurrency(String dealCurrency) {
		DealCurrency = dealCurrency;
	}

	public String getDealCurrencyTo() {
		return DealCurrencyTo;
	}

	public void setDealCurrencyTo(String dealCurrencyTo) {
		DealCurrencyTo = dealCurrencyTo;
	}

	public Double getDealBookingAmount() {
		return dealBookingAmount;
	}

	public void setDealBookingAmount(Double dealBookingAmount) {
		this.dealBookingAmount = dealBookingAmount;
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
