package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitID", "referenceNum", "cif", "idType" })
@Schema(name = "CreditFacilitySummaryRequest", description = "Request object with Unit Id,ReferenceNum ,CIF & IdType")
public class CreditFacilitySummaryInterfaceRequest {

	@JsonbProperty("UnitID")
	@Schema(required = true, example = "KE0010001", description = "UnitID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	public String unitID;

	@JsonbProperty("ReferenceNum")
	@Schema(required = true, example = "AAA4343434", description = "Reference Number")
	public String referenceNum;

	@JsonbProperty("CIF")
	@Schema(required = true, example = "171173", description = "CIF")
	public String cif;

	@JsonbProperty("IdType")
	@Schema(required = false, example = "AA904", description = "IdType")
	public String idType;
}
