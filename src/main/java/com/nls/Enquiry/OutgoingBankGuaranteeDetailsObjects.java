package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode","errorDesc","unitId","guaranteeRef", "cbxReference", "issueDate", "accountPartyNameAdd", 
	"applicantPartyNameAdd", "benefName", "address1",
		"address2", "city", "country", "advisingBankname","guaranteeType","guaranteeCurrency","guaranteeAmt",
		"expiryDate","guaranteePurpose","claimAmt","guarOSAmt","guarOSAmtinLCY","transmissionBy" })
public class OutgoingBankGuaranteeDetailsObjects {

	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
    public String errorDesc = "success";
    
    @JsonbProperty("UnitId")
    public String unitId;
    
	@JsonbProperty("GuarReferenceNo")
	private String guaranteeRef;

	@JsonbProperty("CBXReferenceNo")
	private String cbxReference;

	@JsonbProperty("IssueDate")
	private String issueDate;
	
	@JsonbProperty("AccountPartyNameAddress")
	private String accountPartyNameAdd;
	
	@JsonbProperty("ApplicantPartyNameAddress")
	private String applicantPartyNameAdd;
	
	@JsonbProperty("BeneficiaryName")
	private String benefName;
	
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
	
	@JsonbProperty("GuarType")
	private String guaranteeType;

	@JsonbProperty("GuarCurrency")
	private String guaranteeCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("GuarAmount")
	private Double guaranteeAmt;

	@JsonbProperty("ExpiryDate")
	private String expiryDate;

	@JsonbProperty("GuarPurpose")
	private String guaranteePurpose;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("ClaimAmount")
	private Double claimAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("GuarOSAmount")
	private Double guarOSAmt;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("GuarOSAmtInLCY")
	private Double guarOSAmtinLCY;

	@JsonbProperty("TransmissonBy")
	private String transmissionBy;


	public OutgoingBankGuaranteeDetailsObjects() {

		this.setErrCode(errCode);
		this.setErrorDesc(errorDesc);
		this.setUnitId(unitId);
		this.setGuaranteeRef(guaranteeRef);
		this.setCbxReference(cbxReference);
		this.setIssueDate(issueDate);
		this.setAccountPartyNameAdd(accountPartyNameAdd);
		this.setApplicantPartyNameAdd(applicantPartyNameAdd);
		this.setBenefName(benefName);
		this.setAddress1(address1);
		this.setAddress2(address2);
		
		this.setCity(city);
		this.setCountry(country);
		this.setAdvisingBankname(advisingBankname);
		this.setGuaranteeType(guaranteeType);
		this.setGuaranteeCurrency(guaranteeCurrency);
		this.setGuaranteeAmt(guaranteeAmt);
		this.setExpiryDate(expiryDate);
		this.setGuaranteePurpose(guaranteePurpose);
		this.setClaimAmt(claimAmt);
		this.setGuarOSAmt(guarOSAmt);
		this.setGuarOSAmtinLCY(guarOSAmtinLCY);
		this.setTransmissionBy(transmissionBy);

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


	public String getGuaranteeRef() {
		return guaranteeRef;
	}


	public void setGuaranteeRef(String guaranteeRef) {
		this.guaranteeRef = guaranteeRef;
	}


	public String getCbxReference() {
		return cbxReference;
	}


	public void setCbxReference(String cbxReference) {
		this.cbxReference = cbxReference;
	}


	public String getIssueDate() {
		return issueDate;
	}


	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}


	public String getAccountPartyNameAdd() {
		return accountPartyNameAdd;
	}


	public void setAccountPartyNameAdd(String accountPartyNameAdd) {
		this.accountPartyNameAdd = accountPartyNameAdd;
	}


	public String getApplicantPartyNameAdd() {
		return applicantPartyNameAdd;
	}


	public void setApplicantPartyNameAdd(String applicantPartyNameAdd) {
		this.applicantPartyNameAdd = applicantPartyNameAdd;
	}


	public String getBenefName() {
		return benefName;
	}


	public void setBenefName(String benefName) {
		this.benefName = benefName;
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


	public String getGuaranteeType() {
		return guaranteeType;
	}


	public void setGuaranteeType(String guaranteeType) {
		this.guaranteeType = guaranteeType;
	}


	public String getGuaranteeCurrency() {
		return guaranteeCurrency;
	}


	public void setGuaranteeCurrency(String guaranteeCurrency) {
		this.guaranteeCurrency = guaranteeCurrency;
	}


	public Double getGuaranteeAmt() {
		return guaranteeAmt;
	}


	public void setGuaranteeAmt(Double guaranteeAmt) {
		this.guaranteeAmt = guaranteeAmt;
	}


	public String getExpiryDate() {
		return expiryDate;
	}


	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}


	public String getGuaranteePurpose() {
		return guaranteePurpose;
	}


	public void setGuaranteePurpose(String guaranteePurpose) {
		this.guaranteePurpose = guaranteePurpose;
	}


	public Double getClaimAmt() {
		return claimAmt;
	}


	public void setClaimAmt(Double claimAmt) {
		this.claimAmt = claimAmt;
	}


	public Double getGuarOSAmt() {
		return guarOSAmt;
	}


	public void setGuarOSAmt(Double guarOSAmt) {
		this.guarOSAmt = guarOSAmt;
	}


	public Double getGuarOSAmtinLCY() {
		return guarOSAmtinLCY;
	}


	public void setGuarOSAmtinLCY(Double guarOSAmtinLCY) {
		this.guarOSAmtinLCY = guarOSAmtinLCY;
	}


	public String getTransmissionBy() {
		return transmissionBy;
	}


	public void setTransmissionBy(String transmissionBy) {
		this.transmissionBy = transmissionBy;
	}

   
}
