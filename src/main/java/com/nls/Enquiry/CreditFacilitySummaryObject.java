package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "unitId", "cif", "limitCategory", "limitDescription", "currency", "limitExpiryDate", "riskAmount",
		"limitStatus", "availableAmount", "debitExposure","creditExposure" ,"exposure",
		"guaranteeAmt","collateralAmt","utilizationPercentage", "erCode", "erMsg" })
public class CreditFacilitySummaryObject {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("CIF")
	private String cif;

	@JsonbProperty("LimitCategory")
	private String limitCategory;

	@JsonbProperty("LimitDescription")
	private String limitDescription;

	@JsonbProperty("Currency")
	private String currency;

	@JsonbProperty("LimitExpiryDate")
	private String limitExpiryDate;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("RiskAmount")
	private Double riskAmount;

	@JsonbProperty("LimitStatus")
	private String limitStatus;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("AvailableAmount")
	private Double availableAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DebitExposure")
	private Double debitExposure;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CreditExposure")
	private Double creditExposure;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Exposure")
	private Double exposure;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("GuaranteesAmount")
	private Double guaranteeAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("CollateralAmount")
	private Double collateralAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("UtilizationPercentage")
	private Double utilizationPercentage;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE erCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String erMsg = "CustomerId Found";

	public String getLimitCategory() {
		return limitCategory;
	}

	public void setLimitCategory(String limitCategory) {
		this.limitCategory = limitCategory;
	}

	public String getLimitDescription() {
		return limitDescription;
	}

	public void setLimitDescription(String limitDescription) {
		this.limitDescription = limitDescription;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLimitExpiryDate() {
		return limitExpiryDate;
	}

	public void setLimitExpiryDate(String limitExpiryDate) {
		this.limitExpiryDate = limitExpiryDate;
	}

	public Double getRiskAmount() {
		return riskAmount;
	}

	public void setRiskAmount(Double riskAmount) {
		this.riskAmount = riskAmount;
	}

	public String getLimitStatus() {
		return limitStatus;
	}

	public void setLimitStatus(String limitStatus) {
		this.limitStatus = limitStatus;
	}

	public Double getAvailableAmount() {
		return availableAmount;
	}

	public void setAvailableAmount(Double availableAmount) {
		this.availableAmount = availableAmount;
	}

	public Double getDebitExposure() {
		return debitExposure;
	}

	public void setDebitExposure(Double debitExposure) {
		this.debitExposure = debitExposure;
	}

	public Double getUtilizationPercentage() {
		return utilizationPercentage;
	}

	public void setUtilizationPercentage(Double utilizationPercentage) {
		this.utilizationPercentage = utilizationPercentage;
	}

	public CreditFacilitySummaryObject() {
		this.setLimitCategory(limitCategory);
		this.setLimitDescription(limitDescription);
		this.setCurrency(currency);
		this.setLimitExpiryDate(limitExpiryDate);
		this.setRiskAmount(riskAmount);
		this.setLimitStatus(limitStatus);
		this.setAvailableAmount(availableAmount);
		this.setDebitExposure(debitExposure);
		this.setUtilizationPercentage(utilizationPercentage);
		this.setExposure(exposure);
		this.setCollateralAmt(collateralAmt);
		this.setGuaranteeAmt(guaranteeAmt);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
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

	public Double getExposure() {
		return exposure;
	}

	public void setExposure(Double exposure) {
		this.exposure = exposure;
	}

	public Double getGuaranteeAmt() {
		return guaranteeAmt;
	}

	public void setGuaranteeAmt(Double guaranteeAmt) {
		this.guaranteeAmt = guaranteeAmt;
	}

	public Double getCollateralAmt() {
		return collateralAmt;
	}

	public void setCollateralAmt(Double collateralAmt) {
		this.collateralAmt = collateralAmt;
	}

	public Double getCreditExposure() {
		return creditExposure;
	}

	public void setCreditExposure(Double creditExposure) {
		this.creditExposure = creditExposure;
	}

}
