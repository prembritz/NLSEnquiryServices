package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "referencenum", "unitId", "customerId", "requestTime"})
@Schema(name = "FXFutureRequests", description = "Request object with Unit Id ,Reference Number,Customer Id and Request Time")
public class FXFutureRequest {

	@Schema(required = true, example = "20220521EIKD", description = "Reference Number")
	@JsonbProperty("ReferenceNumber")
	public String referencenum;

	@Schema(required = true, example = "KE0010001", description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "775724", description = "Customer Id")
	@JsonbProperty("CustomerId")
	public String customerId;

	@Schema(required = false, example = "08112021112500", description = "Request Time")
	@JsonbProperty("RequestTime")
	public String requestTime;

}
