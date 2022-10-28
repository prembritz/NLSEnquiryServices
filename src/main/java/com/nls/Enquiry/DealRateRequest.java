package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "proCode", "unitID","cif","dealReferenceNo"})
@Schema(name = "DealRateRequest", description = "Request object with ProCode,Unit Id ,cif & dealReferenceNo")
public class DealRateRequest {

	@Schema(required = false, example = "PRDT001", description = "ProCode")
	@JsonbProperty("ProCode")
	public String proCode;

	@Schema(required = true, example = "KE0010001", description = "UnitID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitID;
	
	@Schema(required = true, example = "835253", description = "CIF")
	@JsonbProperty("CIF")
	public String cif;

	@Schema(required = true, example = "FX2011280006", description = "DealReferenceNo")
	@JsonbProperty("DealReferenceNo")
	public String dealReferenceNo;
	
	

}

