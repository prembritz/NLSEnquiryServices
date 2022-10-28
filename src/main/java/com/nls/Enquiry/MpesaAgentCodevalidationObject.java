package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errCode","errorDesc","unitId","agentCode", "agentName", "agentAccount" })
public class MpesaAgentCodevalidationObject {

  
  
  @JsonbProperty("ErrorCode")
  @JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
  public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

  @JsonbProperty("ErrorMessage")
  public String errorDesc = "success";
  
   @JsonbProperty("UnitId")
    public String unitId;
  
	@JsonbProperty("AgentCode")
	public String agentCode;

	@JsonbProperty("AgentName")
	public String agentName;

	@JsonbProperty("AgentAccount")
	public String agentAccount;

	
}
