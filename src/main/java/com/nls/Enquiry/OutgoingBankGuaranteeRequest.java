package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name="OutgoingBankGuaranteeRequest",description="Request object with Unit Id and CIF")
@JsonbPropertyOrder({"proCode","unId","cif"})
public class OutgoingBankGuaranteeRequest {

	@Schema(required =false , example="PRDT001" , description = "Product Code")
	@JsonbProperty("ProCode")
	public String proCode;
	
	@Schema(required =true , example="KE0010001" , description = "Unit ID", enumeration = {"KE0010001","TZ0010001","UG0010001"})
	@JsonbProperty("UnitID")
	public String unId;
	
	@Schema(required =true , example="660864" , description = "Customer Id")
	@JsonbProperty("CIF")
	public String cif;

}


