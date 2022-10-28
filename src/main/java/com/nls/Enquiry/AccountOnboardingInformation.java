package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;


@JsonbPropertyOrder({"unitID","accNumber","accName","accCur","branch","openDt","cifRef","accType","proCode","accCategory",
	"closureFlag","closureDate","nocredStatus","nodebtStatus","delinquencyStat","inactiveStat","rmCode",
	"rmName","rmMailid"})
public class AccountOnboardingInformation {

	
	@JsonbProperty("UnitID")
	public String unitID;
	
	@JsonbProperty("AccountNo")
	public String accNumber;
	
	@JsonbProperty("AccountCcy")
	public String accCur;
	
	@JsonbProperty("AccountName")
	public String accName;
	
	@JsonbProperty("Branch")
	public String branch;
	
	@JsonbProperty("OpenDate")
	public String openDt;
	
	@JsonbProperty("CIFNo")
	public String cifRef;
	
	@JsonbProperty("AccountType")
	public int accType;
	
	@JsonbProperty("ProductCode")
	public String proCode;
	
	@JsonbProperty("AccCategory")
	public String accCategory;
	
	@JsonbProperty("ClosureFlag")
	public String closureFlag;
	
	@JsonbProperty("ClosureDate")
	public String closureDate;
	
	@JsonbProperty("NoCreditStatus")
	public String nocredStatus;
	
	@JsonbProperty("NoDebitStatus")
	public String nodebtStatus;
	
	@JsonbProperty("DelinquencyStatus")
	public String delinquencyStat;
	
	@JsonbProperty("InactiveStatus")
	public String inactiveStat;
	
	@JsonbProperty("RMCode")
	public String rmCode;
	
	@JsonbProperty("RMName")
	public String rmName;
	
	@JsonbProperty("RMMailID")
	public String rmMailID;

}
