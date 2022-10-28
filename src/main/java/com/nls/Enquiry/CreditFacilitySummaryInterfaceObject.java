package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbNillable(true)
@JsonbPropertyOrder({ "limitID", "limitDescription", "currency", "customerName", "limitAmount", "totalLiability",
		"limitMarginAmount", "availableSanctionLimit", "availableDrawingLimit", "limitApprovalDate", "limitReviewDate",
		"limitType" })
public class CreditFacilitySummaryInterfaceObject {
	@JsonbProperty("LimitID")
	private String limitID;

	@JsonbProperty("LimitDescription")
	private String limitDescription;

	@JsonbProperty("Currency")
	private String currency;

	@JsonbProperty("CustomerName")
	private String customerName;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LimitAmount")
	private Double limitAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TotalLiability")
	private Double totalLiability;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LimitMarginAmount")
	private Double limitMarginAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("AvailableSanctionLimit")
	private Double availableSanctionLimit;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("AvailableDrawingLimit")
	private Double availableDrawingLimit;

	@JsonbProperty("LimitApprovalDate")
	private String limitApprovalDate;

	@JsonbProperty("LimitReviewDate")
	private String limitReviewDate;

	@JsonbProperty("LimitType")
	private String limitType;

	public String getLimitID() {
		return limitID;
	}

	public void setLimitID(String limitID) {
		this.limitID = limitID;
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {
		this.limitAmount = limitAmount;
	}

	public Double getTotalLiability() {
		return totalLiability;
	}

	public void setTotalLiability(Double totalLiability) {
		this.totalLiability = totalLiability;
	}

	public Double getLimitMarginAmount() {
		return limitMarginAmount;
	}

	public void setLimitMarginAmount(Double limitMarginAmount) {
		this.limitMarginAmount = limitMarginAmount;
	}

	public Double getAvailableSanctionLimit() {
		return availableSanctionLimit;
	}

	public void setAvailableSanctionLimit(Double availableSanctionLimit) {
		this.availableSanctionLimit = availableSanctionLimit;
	}

	public Double getAvailableDrawingLimit() {
		return availableDrawingLimit;
	}

	public void setAvailableDrawingLimit(Double availableDrawingLimit) {
		this.availableDrawingLimit = availableDrawingLimit;
	}

	public String getLimitApprovalDate() {
		return limitApprovalDate;
	}

	public void setLimitApprovalDate(String limitApprovalDate) {
		this.limitApprovalDate = limitApprovalDate;
	}

	public String getLimitReviewDate() {
		return limitReviewDate;
	}

	public void setLimitReviewDate(String limitReviewDate) {
		this.limitReviewDate = limitReviewDate;
	}

	public String getLimitType() {
		return limitType;
	}

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public CreditFacilitySummaryInterfaceObject() {
		this.setLimitID(limitID);
		this.setLimitDescription(limitDescription);
		this.setCurrency(currency);
		this.setCustomerName(customerName);
		this.setLimitAmount(limitAmount);
		this.setTotalLiability(totalLiability);
		this.setLimitMarginAmount(limitMarginAmount);
		this.setAvailableSanctionLimit(availableSanctionLimit);
		this.setAvailableDrawingLimit(availableDrawingLimit);
		this.setLimitApprovalDate(limitApprovalDate);
		this.setLimitReviewDate(limitReviewDate);
		this.setLimitType(limitType);
	}

}
