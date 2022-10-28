package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({ "errorcode", "errormessage", "unitid","uniqueTransactionLegNo", "debitaccountnumber", "creditaccountnumber",
		"hostreferencenumber", "customername", "beneficiaryname", "transferamount", "narration", "userreference",
		"valuedate" })
public class AccountTransactionAdviseObject {

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errorcode = ERROR_CODE.SUCCESSFUL;

	@JsonbProperty("ErrorMessage")
	public String errormessage = "success";

	@JsonbProperty("UnitID")
	private String unitid;

	@JsonbProperty("Debit Account Number")
	private String debitaccountnumber;

	@JsonbProperty("Credit Account Number")
	private String creditaccountnumber;

	@JsonbProperty("Host reference Number")
	private String hostreferencenumber;

	@JsonbProperty("Customer name")
	private String customername;

	@JsonbProperty("Beneficiary Name")
	private String beneficiaryname;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("Transfer Amount")
	private Double transferamount;

	@JsonbProperty("Narration")
	private String narration;

	@JsonbProperty("User Reference")
	private String userreference;

	@JsonbProperty("Value Date")
	private String valuedate;
	
	
	@JsonbProperty("UniqueTransactionLegNo")
	public String uniqueTransactionLegNo;

	public ERROR_CODE getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(ERROR_CODE errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrormessage() {
		return errormessage;
	}

	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}

	public String getUnitid() {
		return unitid;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	public String getDebitaccountnumber() {
		return debitaccountnumber;
	}

	public void setDebitaccountnumber(String debitaccountnumber) {
		this.debitaccountnumber = debitaccountnumber;
	}

	public String getCreditaccountnumber() {
		return creditaccountnumber;
	}

	public void setCreditaccountnumber(String creditaccountnumber) {
		this.creditaccountnumber = creditaccountnumber;
	}

	public String getHostreferencenumber() {
		return hostreferencenumber;
	}

	public void setHostreferencenumber(String hostreferencenumber) {
		this.hostreferencenumber = hostreferencenumber;
	}

	public String getCustomername() {
		return customername;
	}

	public void setCustomername(String customername) {
		this.customername = customername;
	}

	public String getBeneficiaryname() {
		return beneficiaryname;
	}

	public void setBeneficiaryname(String beneficiaryname) {
		this.beneficiaryname = beneficiaryname;
	}

	public Double getTransferamount() {
		return transferamount;
	}

	public void setTransferamount(Double transferamount) {
		this.transferamount = transferamount;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public String getUserreference() {
		return userreference;
	}

	public void setUserreference(String userreference) {
		this.userreference = userreference;
	}

	public String getValuedate() {
		return valuedate;
	}

	public void setValuedate(String valuedate) {
		this.valuedate = valuedate;
	}

	public AccountTransactionAdviseObject() {
		this.setErrorcode(errorcode);
		this.setErrormessage(errormessage);
		this.setUnitid(unitid);
		this.setDebitaccountnumber(debitaccountnumber);
		this.setCreditaccountnumber(creditaccountnumber);
		this.setHostreferencenumber(hostreferencenumber);
		this.setCustomername(customername);
		this.setBeneficiaryname(beneficiaryname);
		this.setTransferamount(transferamount);
		this.setNarration(narration);
		this.setUserreference(userreference);
		this.setValuedate(valuedate);
		this.setUniqueTransactionLegNo(uniqueTransactionLegNo);
	}

	public String getUniqueTransactionLegNo() {
		return uniqueTransactionLegNo;
	}

	public void setUniqueTransactionLegNo(String uniqueTransactionLegNo) {
		this.uniqueTransactionLegNo = uniqueTransactionLegNo;
	}
	
	
	

}
