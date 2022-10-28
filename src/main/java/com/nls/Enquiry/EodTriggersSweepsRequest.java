package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "unitId", "processId", "referenceNumber", "eodstage", "coreEodDate", "nextBusinessDate",
		"systemDateTime" })
@Schema(name = "EodTriggersSweepsRequest", description = "Request object with unitId, processId, referenceNumber, eodstage, coreEodDate, nextBusinessDate & systemDateTime")
public class EodTriggersSweepsRequest {

	@Schema(required = true, example = "KE0010001", description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitId;

	@Schema(required = false, example = "EODINITIATION", description = "Process ID")
	@JsonbProperty("ProcessID")
	public String processId;

	@Schema(required = true, example = "1343483908AAA", description = "ReferenceNumber")
	@JsonbProperty("ReferenceNumber")
	public String referenceNumber;

	@Schema(required = true, example = "12345", description = "EOD Stage")
	@JsonbProperty("EODStage")
	public String eodstage;

	@Schema(required = true, example = "02032022", description = "Core EODDate")
	@JsonbProperty("CoreEODDate")
	public String coreEodDate;

	@Schema(required = true, example = "02032022", description = "Next Business Date")
	@JsonbProperty("NextBusinessDate")
	public String nextBusinessDate;

	@Schema(required = false, example = "02032022123300", description = "System DateTime")
	@JsonbProperty("SystemDateTime")
	public String systemDateTime;

}
