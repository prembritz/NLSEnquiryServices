package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbNillable(true)
@JsonbPropertyOrder({ "errCode","errorDesc","adviseReference","lcReference", "lcTenor", "lcIssueDate", "applicantName", "bankName", 
	"shipmentDate", "lcExpiryDate","expiryPlace","lcCurrency",
		"lcAmount", "negotiationValue", "outstandingLCAmount", "transferableFlag", "confirmInstructions",
		"confirmationStatus","adviseDetails","noofAmendments","goodsCategory"})
public class ExportLCDetailsObjects {

	
	@JsonbProperty("ErrorCode")
	@JsonbTypeAdapter(value = com.nls.Enquiry.ERROR_CODE_SERIALIZER.class)
	public ERROR_CODE errCode = ERROR_CODE.SUCCESSFUL;
	
    @JsonbProperty("ErrorMessage")
   public String errorDesc = "success";
    
    @JsonbProperty("UnitId")
    public String unitId;
    
	@JsonbProperty("AdviseReference")
	private String adviseReference;
	
	@JsonbProperty("LCReferenceNo")
	private String lcReference;

	@JsonbProperty("LCTenor")
	private String lcTenor;

	@JsonbProperty("LCIssueDate")
	private String lcIssueDate;

	@JsonbProperty("ApplicantName")
	private String applicantName;

	@JsonbProperty("BankName")
	private String bankName;
	
	@JsonbProperty("LatestShipmentDate")
	private String shipmentDate;
	
	@JsonbProperty("LCExpiryDate")
	private String lcExpiryDate;
	
	@JsonbProperty("ExpiryPlace")
	private String expiryPlace;

	@JsonbProperty("LCCurrency")
	private String lcCurrency;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("LCAmount")
	private Double lcAmount;

	@JsonbNumberFormat("###0.00")
	@JsonbProperty("NegotiationValue")
	private Double negotiationValue;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("OutstandingLCAmount")
	private Double outstandingLCAmount;
	
	@JsonbProperty("TransferableFlag")
	private String transferableFlag;
	
	@JsonbProperty("ConfirmationInstructions")
	private String confirmInstructions;

	@JsonbProperty("ConfirmationStatus")
	private String confirmationStatus;
	
	@JsonbProperty("AdviseDetails")
	private String adviseDetails;
	
	@JsonbProperty("NoOfAmendments")
	private String noofAmendments;
		
	@JsonbProperty("GoodsCategory")
	private String goodsCategory;


	public ExportLCDetailsObjects() {

		this.setAdviseReference(adviseReference);
		this.setLcReference(lcReference);
		this.setLcTenor(lcTenor);
		this.setLcIssueDate(lcIssueDate);
		this.setLcExpiryDate(lcExpiryDate);
		this.setApplicantName(applicantName);
		this.setBankName(bankName);
		this.setShipmentDate(shipmentDate);
		this.setExpiryPlace(expiryPlace);
		this.setLcCurrency(lcCurrency);
		this.setLcAmount(lcAmount);
		this.setNegotiationValue(negotiationValue);
		this.setOutstandingLCAmount(outstandingLCAmount);
		this.setTransferableFlag(transferableFlag);
		this.setGoodsCategory(goodsCategory);
		this.setConfirmInstructions(confirmInstructions);
		this.setConfirmationStatus(confirmationStatus);
		this.setNoofAmendments(noofAmendments);
		this.setAdviseDetails(adviseDetails);
		this.setUnitId(unitId);

	}


	public String getUnitId() {
		return unitId;
	}


	public void setUnitId(String unitId) {
		this.unitId = unitId;
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


	public String getAdviseReference() {
		return adviseReference;
	}


	public void setAdviseReference(String adviseReference) {
		this.adviseReference = adviseReference;
	}


	public String getLcReference() {
		return lcReference;
	}


	public void setLcReference(String lcReference) {
		this.lcReference = lcReference;
	}


	public String getLcTenor() {
		return lcTenor;
	}


	public void setLcTenor(String lcTenor) {
		this.lcTenor = lcTenor;
	}


	public String getLcIssueDate() {
		return lcIssueDate;
	}


	public void setLcIssueDate(String lcIssueDate) {
		this.lcIssueDate = lcIssueDate;
	}


	public String getApplicantName() {
		return applicantName;
	}


	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getShipmentDate() {
		return shipmentDate;
	}


	public void setShipmentDate(String shipmentDate) {
		this.shipmentDate = shipmentDate;
	}


	public String getLcExpiryDate() {
		return lcExpiryDate;
	}


	public void setLcExpiryDate(String lcExpiryDate) {
		this.lcExpiryDate = lcExpiryDate;
	}


	public String getExpiryPlace() {
		return expiryPlace;
	}


	public void setExpiryPlace(String expiryPlace) {
		this.expiryPlace = expiryPlace;
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


	public Double getNegotiationValue() {
		return negotiationValue;
	}


	public void setNegotiationValue(Double negotiationValue) {
		this.negotiationValue = negotiationValue;
	}


	public Double getOutstandingLCAmount() {
		return outstandingLCAmount;
	}


	public void setOutstandingLCAmount(Double outstandingLCAmount) {
		this.outstandingLCAmount = outstandingLCAmount;
	}


	public String getTransferableFlag() {
		return transferableFlag;
	}


	public void setTransferableFlag(String transferableFlag) {
		this.transferableFlag = transferableFlag;
	}


	public String getConfirmInstructions() {
		return confirmInstructions;
	}


	public void setConfirmInstructions(String confirmInstructions) {
		this.confirmInstructions = confirmInstructions;
	}


	public String getConfirmationStatus() {
		return confirmationStatus;
	}


	public void setConfirmationStatus(String confirmationStatus) {
		this.confirmationStatus = confirmationStatus;
	}


	public String getAdviseDetails() {
		return adviseDetails;
	}


	public void setAdviseDetails(String adviseDetails) {
		this.adviseDetails = adviseDetails;
	}


	public String getNoofAmendments() {
		return noofAmendments;
	}


	public void setNoofAmendments(String noofAmendments) {
		this.noofAmendments = noofAmendments;
	}


	public String getGoodsCategory() {
		return goodsCategory;
	}


	public void setGoodsCategory(String goodsCategory) {
		this.goodsCategory = goodsCategory;
	}


}
