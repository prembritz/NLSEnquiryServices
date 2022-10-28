package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode", "errorDesc", "unId", "stats", "failureDesc" })
public class CustomerOnBoardingStatusObject {

	
	@JsonbProperty("UnitID")
	public String unId;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
    public String errorDesc = "success";

	@JsonbProperty("Status")
	public String stats="S";
	
	@JsonbProperty("Description")
	public String failureDesc="";
}
