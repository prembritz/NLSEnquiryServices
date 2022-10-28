package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitId", "referenceNum", "rateCode", "buyCurrency", "sellCurrency", "customerNum", "amount","dealReferenceNumber"})
@Schema(name = "FXRateEnquiryRequest", description = "Request object with Unit Id ,Reference Number,Rate Code,Buy Currency,Sell Currency,Customer Number,Amount and Deal ReferenceNumber")
public class FXRateEnquiryRequest {

	@Schema(required = true, example = "KE0010001", description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "20220421OLEF", description = "Reference Number")
	@JsonbProperty("ReferenceNumber")
	public String referenceNum;

	@Schema(required = false, example = "101", description = "Rate Code")
	@JsonbProperty("RateCode")
	public String rateCode;

	@Schema(required = true, example = "KES", description = "Buy Currency")
	@JsonbProperty("BuyCurrency")
	public String buyCurrency;

	@Schema(required = true, example = "USD", description = "Sell Currency")
	@JsonbProperty("SellCurrency")
	public String sellCurrency;

	@Schema(required = true, example = "775724", description = "CustomerNumber")
	@JsonbProperty("CustomerNumber")
	public String customerNum;

	@Schema(required = false, example = "200.00", description = "Amount")
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Amount")
	public double amount;

	@Schema(required = true, example = "FX2011280006", description = "Deal Reference Number")
	@JsonbProperty("DealReferenceNumber")
	public String dealReferenceNumber;

}
