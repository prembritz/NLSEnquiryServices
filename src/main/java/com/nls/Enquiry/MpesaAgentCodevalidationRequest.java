package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({"unitID","agentCode"})
@Schema(name = "MpesaAgentCodevalidationRequest", description = "Request object with Unit Id & AgentCode")
public class MpesaAgentCodevalidationRequest {

	@Schema(required = true, example = "KE0010001", description = "UnitID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unitID;
	
	@Schema(required = true, example = "122-96519", description = "Agent Code")
	@JsonbProperty("AgentCode")
	public String agentCode;
}

