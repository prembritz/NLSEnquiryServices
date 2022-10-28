package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitId", "customerId", "referenceNumber", "requestTime"})
@Schema(name = "ExportLCBillCollectionRequest", description = "Request object with Unit Id ,CustomerId , ReferenceNum & Request Time")
public class ExportLCBillCollectionRequest {

	@Schema(required = true, example = "KE0010001", description = "Unit Id", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = true, example = "171200", description = "Customer Id")
	@JsonbProperty("CustomerId")
	public String customerId;

	@Schema(required = true, example = "A435534433", description = "Reference Number")
	@JsonbProperty("ReferenceNumber")
	public String referenceNumber;

	@Schema(required = false, example = "02062022", description = "Request Time")
	@JsonbProperty("RequestTime")
	public String requestTime;

}
