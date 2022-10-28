package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "unitId", "accountNo","currency","accountBranch", "valueDate", "valueDateBalance",
		"lastBalanceTimestamp","erCode","erMsg"})
public class AccountValueDateObject {

	@JsonbProperty("UnitID")
	private String unitId;

	@JsonbProperty("AccountNumber")
	private String accountNo;

	@JsonbProperty("ValueDate")
	private String valueDate;

	@JsonbProperty("Currency")
	private String currency;

	@JsonbProperty("AccountBranch")
	private String accountBranch;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("ValueDateBalance")
	private Double valueDateBalance;

	@JsonbProperty("LastBalanceTimestamp")
	private String lastBalanceTimestamp;

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE erCode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String erMsg = "Account Found";

	
	public AccountValueDateObject() {
		this.setUnitId(unitId);
		this.setAccountNo(accountNo);
		this.setValueDate(valueDate);
		this.setCurrency(currency);
		this.setAccountBranch(accountBranch);
		this.setValueDateBalance(valueDateBalance);
		this.setLastBalanceTimestamp(lastBalanceTimestamp);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getValueDate() {
		return valueDate;
	}

	public void setValueDate(String valueDate) {
		this.valueDate = valueDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountBranch() {
		return accountBranch;
	}

	public void setAccountBranch(String accountBranch) {
		this.accountBranch = accountBranch;
	}

	public Double getValueDateBalance() {
		return valueDateBalance;
	}

	public void setValueDateBalance(Double valueDateBalance) {
		this.valueDateBalance = valueDateBalance;
	}

	public String getLastBalanceTimestamp() {
		return lastBalanceTimestamp;
	}

	public void setLastBalanceTimestamp(String lastBalanceTimestamp) {
		this.lastBalanceTimestamp = lastBalanceTimestamp;
	}

	public ERROR_CODE getErCode() {
		return erCode;
	}

	public void setErCode(ERROR_CODE erCode) {
		this.erCode = erCode;
	}

	public String getErMsg() {
		return erMsg;
	}

	public void setErMsg(String erMsg) {
		this.erMsg = erMsg;
	}

}
