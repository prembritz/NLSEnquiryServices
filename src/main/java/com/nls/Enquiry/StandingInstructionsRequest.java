package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "referenceNum", "unitId", "customerId", "accountNumber","requestTime" })
@Schema(name = "StandingInstructionRequest", description = "Request object with ReferenceNumber ,unitID ,CustomerId, AccountNumber & Request Time")
public class StandingInstructionsRequest {
	
	@Schema(required = true, example = "20220410EWSF", description = "Reference Number")
	@JsonbProperty("ReferenceNumber")
	public String referenceNum;

	@Schema(required = true, example = "KE0010001", description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "171154", description = "Customer Id")
	@JsonbProperty("CustomerId")
	public String customerId;

	@Schema(required = true, example = "1711730045", description = "Account Number")
	@JsonbProperty("AccountNumber")
	public String accountNumber;

	@Schema(required = false, example = "08112021112500", description = "Request Time")
	@JsonbProperty("RequestTime")
	public String requestTime;
}
