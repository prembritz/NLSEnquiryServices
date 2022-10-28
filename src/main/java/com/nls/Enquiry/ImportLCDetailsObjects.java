package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode","errorDesc","unitId","lcReference", "accountPartyName", "applicantName", "beneficiaryName", "address1", 
	"address2", "city","country","advisingBankname",
		"adviseThroughBankname", "lcIssueDate", "placeOfExpiry", "lcCurrency", "lcAmount",
		"totValueUtilized","lcosAmount","osAmountLcy","lcTenor","lcTenorDetails","portOfDeparture","noofAmendments","lcType",
		"revolvingLC","revolvingUnits","frequency","cumulativeFlag"})
public class ImportLCDetailsObjects {

	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   public String errorDesc = "success";
    
    @JsonbProperty("UnitId")
    public String unitId;
    
	@JsonbProperty("LCReferenceNo")
	private String lcReference;
	
	@JsonbProperty("AccountPartyNameAddress")
	private String accountPartyName;
	
	@JsonbProperty("ApplicantNameAddress")
	private String applicantName;
	
	@JsonbProperty("BeneficiaryName")
	private String beneficiaryName;
	
	@JsonbProperty("AddressLine1")
	private String address1;
	
	@JsonbProperty("AddressLine2")
	private String address2;
	
	@JsonbProperty("City")
	private String city;
	
	@JsonbProperty("Country")
	private String country;
	
	@JsonbProperty("AdvisingBankNameAddress")
	private String advisingBankname;
	
	@JsonbProperty("AdviseThroughBankNameAddress")
	private String adviseThroughBankname;

	@JsonbProperty("LCIssueDate")
	private String lcIssueDate;

	@JsonbProperty("PlaceOfExpiry")
	private String placeOfExpiry;

	@JsonbProperty("LCCurrency")
	private String lcCurrency;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LCAmount")
	private Double lcAmount;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("TotalValueUtilized")
	private Double totValueUtilized;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LCOSAmount")
	private Double lcosAmount;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OSAmountInLCY")
	private Double osAmountLcy;
	
	@JsonbProperty("LCTenor")
	private String lcTenor;
	
	@JsonbProperty("LCTenorDetails")
	private String lcTenorDetails;
	
	@JsonbProperty("PortOfDeparture")
	private String portOfDeparture;
	
	@JsonbProperty("NoOfAmendments")
	private String noofAmendments;
	
	@JsonbProperty("LCType")
	private String lcType;
	
	@JsonbProperty("RevolvingLC")
	private String revolvingLC;
	
	@JsonbProperty("RevolvingUnits")
	private String revolvingUnits;
	
	@JsonbProperty("Frequency")
	private String frequency;
	
	@JsonbProperty("CumulativeFlag")
	private String cumulativeFlag;

	public ImportLCDetailsObjects() {

		this.setUnitId(unitId);
		this.setLcReference(lcReference);
		this.setAccountPartyName(accountPartyName);
		this.setApplicantName(applicantName);
		this.setBeneficiaryName(beneficiaryName);
		this.setAddress1(address1);
		this.setAddress2(address2);
		this.setCity(city);
		this.setCountry(country);
		this.setAdvisingBankname(advisingBankname);
		this.setAdviseThroughBankname(adviseThroughBankname);
		this.setLcIssueDate(lcIssueDate);
		this.setPlaceOfExpiry(placeOfExpiry);
		this.setLcCurrency(lcCurrency);
		this.setLcAmount(lcAmount);
		this.setTotValueUtilized(totValueUtilized);
		this.setLcosAmount(lcosAmount);
		this.setOsAmountLcy(osAmountLcy);
		this.setLcTenor(lcTenor);
		
		this.setLcTenorDetails(lcTenorDetails);
		this.setPortOfDeparture(portOfDeparture);
		this.setNoofAmendments(noofAmendments);
		this.setLcType(lcType);
		this.setRevolvingLC(revolvingLC);
		this.setRevolvingUnits(revolvingUnits);
		this.setFrequency(frequency);
		this.setCumulativeFlag(cumulativeFlag);

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

	public String getLcReference() {
		return lcReference;
	}

	public void setLcReference(String lcReference) {
		this.lcReference = lcReference;
	}

	public String getAccountPartyName() {
		return accountPartyName;
	}

	public void setAccountPartyName(String accountPartyName) {
		this.accountPartyName = accountPartyName;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAdvisingBankname() {
		return advisingBankname;
	}

	public void setAdvisingBankname(String advisingBankname) {
		this.advisingBankname = advisingBankname;
	}

	public String getAdviseThroughBankname() {
		return adviseThroughBankname;
	}

	public void setAdviseThroughBankname(String adviseThroughBankname) {
		this.adviseThroughBankname = adviseThroughBankname;
	}

	public String getLcIssueDate() {
		return lcIssueDate;
	}

	public void setLcIssueDate(String lcIssueDate) {
		this.lcIssueDate = lcIssueDate;
	}

	public String getPlaceOfExpiry() {
		return placeOfExpiry;
	}

	public void setPlaceOfExpiry(String placeOfExpiry) {
		this.placeOfExpiry = placeOfExpiry;
	}

	public String getLcCurrency() {
		return lcCurrency;
	}

	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}

	public Double getLcAmount() {
		return lcAmount;
	}

	public void setLcAmount(Double lcAmount) {
		this.lcAmount = lcAmount;
	}

	public Double getTotValueUtilized() {
		return totValueUtilized;
	}

	public void setTotValueUtilized(Double totValueUtilized) {
		this.totValueUtilized = totValueUtilized;
	}

	public Double getLcosAmount() {
		return lcosAmount;
	}

	public void setLcosAmount(Double lcosAmount) {
		this.lcosAmount = lcosAmount;
	}

	public Double getOsAmountLcy() {
		return osAmountLcy;
	}

	public void setOsAmountLcy(Double osAmountLcy) {
		this.osAmountLcy = osAmountLcy;
	}

	public String getLcTenor() {
		return lcTenor;
	}

	public void setLcTenor(String lcTenor) {
		this.lcTenor = lcTenor;
	}

	public String getLcTenorDetails() {
		return lcTenorDetails;
	}

	public void setLcTenorDetails(String lcTenorDetails) {
		this.lcTenorDetails = lcTenorDetails;
	}

	public String getPortOfDeparture() {
		return portOfDeparture;
	}

	public void setPortOfDeparture(String portOfDeparture) {
		this.portOfDeparture = portOfDeparture;
	}

	public String getNoofAmendments() {
		return noofAmendments;
	}

	public void setNoofAmendments(String noofAmendments) {
		this.noofAmendments = noofAmendments;
	}

	public String getLcType() {
		return lcType;
	}

	public void setLcType(String lcType) {
		this.lcType = lcType;
	}

	public String getRevolvingLC() {
		return revolvingLC;
	}

	public void setRevolvingLC(String revolvingLC) {
		this.revolvingLC = revolvingLC;
	}

	public String getRevolvingUnits() {
		return revolvingUnits;
	}

	public void setRevolvingUnits(String revolvingUnits) {
		this.revolvingUnits = revolvingUnits;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getCumulativeFlag() {
		return cumulativeFlag;
	}

	public void setCumulativeFlag(String cumulativeFlag) {
		this.cumulativeFlag = cumulativeFlag;
	}

}
