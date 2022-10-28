package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "procCode", "unitID", "uniqueTransactionLegNo", "instrumentReferenceNumber" })
@Schema(name="Account Transaction Advise Request",description="Request object with procCode ,unitID,uniqueTransactionLegNo and instrumentReferenceNumber")
public class AccountTransactionAdviseRequest {
	@Schema(required = false, example = "PRDT002", description = "Product Code")
	@JsonbProperty("ProcCode")
	public String procCode;

	@Schema(required = true, example = "KE0010001", description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitID;

	@Schema(required = true, example = "188970434852455.100001", description = "Unique Transaction Leg No")
	@JsonbProperty("UniqueTransactionLegNo")
	public String uniqueTransactionLegNo;

	@Schema(required = true, example = "FT0832700274", description = "Instrument Reference Number")
	@JsonbProperty("InstrumentReferenceNumber")
	public String instrumentReferenceNumber;
} 
