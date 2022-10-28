package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "respCode", "respDesc", "accId", "bkId", "uniqId", "accName",
	"msgId"})
public class PesaLinkAccountValidationObject {
	
	@JsonbProperty("accountId")
	public String accId;

	@JsonbProperty("bankId")
	public String bkId;

	@JsonbProperty("responseCode")
	public String respCode;

	@JsonbProperty("responseDesc")
	public String respDesc;

	@JsonbProperty("accountName")
	public String accName;

	@JsonbProperty("MsgId")
	public String msgId;

	@JsonbProperty("uniqueId")
	public String uniqId;

	public String getAccId() {
		return accId;
	}

	public void setAccId(String accId) {
		this.accId = accId;
	}

	public String getBkId() {
		return bkId;
	}

	public void setBkId(String bkId) {
		this.bkId = bkId;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getUniqId() {
		return uniqId;
	}

	public void setUniqId(String uniqId) {
		this.uniqId = uniqId;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	
}
