package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public class CreditCardStatementRequest {
	@Schema(required = true, example = "AB99898", description = "Product Code")
	@JsonbProperty("ProcCode")
	public String procCode;
	
	@Schema(required = true, example = "KE0010001", description = "Unit ID")
	@JsonbProperty("UnitID")
	public String unitID;
	
	@Schema(required = true, example = "7897XXXXXXXX5478", description = "Credit CardNumber")
	@JsonbProperty("CreditCardNumber")
	public String creditCardNumber;
	
	@Schema(required = true, example = "20052022", description = "From Date")
	@JsonbProperty("FromDate")
	public String fromDate;
	
	@Schema(required = true, example = "15062022", description = "To Date")
	@JsonbProperty("ToDate")
	public String toDate;

}
