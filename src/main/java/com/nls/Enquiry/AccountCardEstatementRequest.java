package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitId", "referenceNum", "customerId", "startDate", "endDate", "requestTime" })
@Schema(name = "AccountCardEstatementRequest", description = "Request object with unitId, referenceNum, customerId, startDate, endDate and requestTime")
public class AccountCardEstatementRequest {
	
	@Schema(required = true, example = "KE0010001", description = "unit Id" , enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "ABCCC2220393", description = "Reference Number")
	@JsonbProperty("ReferenceNumber")
	public String referenceNum;

	@Schema(required = true, example = "171231", description = "Customer Id")
	@JsonbProperty("CustomerId")
	public String customerId;

	@Schema(required = true, example = "02032015", description = "Start Date")
	@JsonbProperty("StartDate")
	public String startDate;

	@Schema(required = true, example = "02062022", description = "End Date")
	@JsonbProperty("EndDate")
	public String endDate;

	@Schema(required = false, example = "08112022112500", description = "Request Time")
	@JsonbProperty("RequestTime")
	public String requestTime;

}
