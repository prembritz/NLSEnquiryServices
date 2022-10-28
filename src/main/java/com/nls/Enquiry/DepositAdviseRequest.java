package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitID","accountNumber"})
@Schema(name = "DepositAdviseRequest", description = "Request object with Unit Id & Account Number")
public class DepositAdviseRequest {

	@Schema(required = true, example = "KE0010001", description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitID;
	
	@Schema(required = true, example = "8303010042", description = "Account Number")
	@JsonbProperty("AccountNumber")
	public String accountNumber;

}

