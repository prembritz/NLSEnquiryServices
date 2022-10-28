package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unitId","customerId","implcList"})
public class ImportLCList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   public String errorDesc = "success";
    
    @JsonbProperty("UnitId")
   public String unitId;
    
    @JsonbProperty("CustomerId")
   public String customerId;
      
   @JsonbProperty("RecordList")
	public List<ImportLCObjects> implcList;

	public ImportLCList() {
		implcList = new ArrayList<ImportLCObjects>();
	}
	
	public void addAccount(ImportLCObjects object) {
		this.implcList.add(object);
	}

	public ERROR_CODE getErrCode() {
		return errCode;
	}

	public void setErrCode(ERROR_CODE errCode) {
		this.errCode = errCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
}
