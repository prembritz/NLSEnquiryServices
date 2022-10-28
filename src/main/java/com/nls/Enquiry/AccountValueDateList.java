package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@JsonbPropertyOrder({ "errCode","errorDesc","accList"})
public class AccountValueDateList {

	@Schema(required = true, type=SchemaType.ARRAY,implementation=AccountValueDateObject.class)
	@JsonbProperty("AccountValueDateList")
	private List<AccountValueDateObject> accList;
	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errorDesc = "success";

	public AccountValueDateList() {
		accList = new ArrayList<AccountValueDateObject>();
	}

	public void addAccount(AccountValueDateObject object) {
		this.accList.add(object);
	}

	public List<AccountValueDateObject> getAccList() {
		return accList;
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

	public void setAccList(List<AccountValueDateObject> accList) {
		this.accList = accList;
	}
}
