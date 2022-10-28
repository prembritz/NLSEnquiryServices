package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode", "errorDesc", "unitId", "referenceNum", "custID", "buyfxRate", "sellfxRate", "midRate",
		"dealReferenceNumber", "dealRate", "dealDate", "dealCurrency", "dealCurrencyto", "dealBookingAmount",
		"RateCode" })
public class FXRateEnquiryObject {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("CustomerId")
	private String custID;

	@JsonbProperty("ReferenceNumber")
	private String referenceNum;

	// @JsonbNumberFormat("###0.00")
	@JsonbProperty("BuyFXRate")
	private String buyfxRate;

	// @JsonbNumberFormat("###0.00")
	@JsonbProperty("SellFXRate")
	private String sellfxRate;

	// @JsonbNumberFormat("###0.00")
	@JsonbProperty("MidRate")
	private String midRate;

	@JsonbProperty("DealReferenceNo")
	private String dealReferenceNumber;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DealRate")
	private Double dealRate;

	@JsonbProperty("DealDate")
	private String dealDate;

	@JsonbProperty("DealCurrency")
	private String dealCurrency;

	@JsonbProperty("DealCurrencyTo")
	private String dealCurrencyto;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DealBookingAmount")
	private Double dealBookingAmount;

	@JsonbProperty("RateCode")
	public String rateCode;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public FXRateEnquiryObject() {
		this.setUnitId(unitId);
		this.setReferenceNum(referenceNum);
		this.setDealReferencenumber(dealReferenceNumber);
		this.setBuyfxRate(buyfxRate);
		this.setSellfxRate(sellfxRate);
		this.setMidRate(midRate);
		this.setDealRate(dealRate);
		this.setDealDate(dealDate);
		this.setDealCurrency(dealCurrency);
		this.setDealCurrencyto(dealCurrencyto);
		this.setDealBookingAmount(dealBookingAmount);
		this.setCustID(custID);
		this.setRateCode(rateCode);
	}

	public String getRateCode() {
		return rateCode;
	}

	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getReferenceNum() {
		return referenceNum;
	}

	public void setReferenceNum(String referenceNum) {
		this.referenceNum = referenceNum;
	}

	public String getDealReferencenumber() {
		return dealReferenceNumber;
	}

	public void setDealReferencenumber(String dealReferencenumber) {
		this.dealReferenceNumber = dealReferencenumber;
	}

	public String getBuyfxRate() {
		return buyfxRate;
	}

	public void setBuyfxRate(String buyfxRate) {
		this.buyfxRate = buyfxRate;
	}

	public String getSellfxRate() {
		return sellfxRate;
	}

	public void setSellfxRate(String sellfxRate) {
		this.sellfxRate = sellfxRate;
	}

	public String getMidRate() {
		return midRate;
	}

	public void setMidRate(String midRate) {
		this.midRate = midRate;
	}

	public Double getDealRate() {
		return dealRate;
	}

	public void setDealRate(Double dealRate) {
		this.dealRate = dealRate;
	}

	public String getDealDate() {
		return dealDate;
	}

	public void setDealDate(String dealDate) {
		this.dealDate = dealDate;
	}

	public String getDealCurrency() {
		return dealCurrency;
	}

	public void setDealCurrency(String dealCurrency) {
		this.dealCurrency = dealCurrency;
	}

	public String getDealCurrencyto() {
		return dealCurrencyto;
	}

	public void setDealCurrencyto(String dealCurrencyto) {
		this.dealCurrencyto = dealCurrencyto;
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

	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
	}
}
