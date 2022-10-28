package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "procCode", "unitID", "accountNo", "chequeNo", "fromDate", "toDate", "startChequeNo",
		"endChequeNo" })
@Schema(name = "CustomerChequeStatusRequest", description = "Request object with Unit Id and Account Number & ValueDate")
public class CustomerChequeStatusRequest {

	@Schema(required = false, example = "PROD2323", description = "Product Code")
	@JsonbProperty("ProcCode")
	public String procCode;

	@Schema(required = true, example = "KE0010001", description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitID;

	@Schema(required = true, example = "6493320028", description = "Account Number")
	@JsonbProperty("AccountNo")
	public String accountNo;

	@Schema(required = true, example = "7623", description = "Cheque Number")
	@JsonbProperty("ChequeNo")
	public String chequeNo;

	@Schema(required = true, example = "06052022", description = "From Date")
	@JsonbProperty("FromDate")
	public String fromDate;

	@Schema(required = true, example = "22062022", description = "To Date")
	@JsonbProperty("ToDate")
	public String toDate;

	@Schema(required = true, example = "100", description = "Start Cheque Number")
	@JsonbProperty("StartChequeNo")
	public String startChequeNo;

	@Schema(required = true, example = "1000", description = "End Cheque Number")
	@JsonbProperty("EndChequeNo")
	public String endChequeNo;
}
