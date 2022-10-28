package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"errCode","errorDesc","unitID","cifRef","custName","primaryId","hostMachine","ctPerson","rmCode",
	"rmName","rmMail","add1","add2","add3","add4","cityTown","countryCode","zip","faxNo","phNumber","cellNo",
	"emailID","branchCode","custCategory","stat","locallangCustname","sicCode","tin","accList"})

public class CustomerOnboardingList
{
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
    public String errorDesc = "success";   
    
    @JsonbProperty("UnitID")
	public String unitID;
	
	@JsonbProperty("CIFNo")
	public String cifRef;
	
	@JsonbProperty("CustName")
	public String custName;
	
	@JsonbProperty("PrimaryID")
	public String primaryId;
	
	@JsonbProperty("HostSystem")
	public String hostMachine;
	
	@JsonbProperty("ContactPerson")
	public String ctPerson;
	
	@JsonbProperty("RMCode")
	public String rmCode;
	
	@JsonbProperty("RMName")
	public String rmName;
	
	@JsonbProperty("RMMailID")
	public String rmMail;
	
	@JsonbProperty("Address1")
	public String add1;
	
	@JsonbProperty("Address2")
	public String add2;
	
	@JsonbProperty("Address3")
	public String add3;
	
	@JsonbProperty("Address4")
	public String add4;
	
	@JsonbProperty("CityTown")
	public String cityTown;
	
	@JsonbProperty("CountryCode")
	public String countryCode;
	
	@JsonbProperty("ZipCode")
	public String zip;
	
	@JsonbProperty("FaxNo")
	public String faxNo;
	
	@JsonbProperty("PhoneNo")
	public String phNumber;
	
	@JsonbProperty("CellNo")
	public String cellNo;
	
	@JsonbProperty("EmailID")
	public String emailID;
	
	@JsonbProperty("BranchCode")
	public String branchCode;
	
	@JsonbProperty("CustCategory")
	public String custCategory;
	
	@JsonbProperty("Status")
	public String stat;
	
	@JsonbProperty("LocalLangCustomerName")
	public String locallangCustname;
	
	@JsonbProperty("SICCode")
	public String sicCode;
	
	@JsonbProperty("TIN")
	public String tin;
   
   @JsonbProperty("AccountList")
  	public List<AccountOnboardingInformation> accList;

	public CustomerOnboardingList() {
		accList = new ArrayList<AccountOnboardingInformation>();
	}
	
	public void addAccount(AccountOnboardingInformation object) {
		this.accList.add(object);
	}
}
