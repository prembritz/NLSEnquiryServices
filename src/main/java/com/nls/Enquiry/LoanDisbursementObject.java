package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;

@JsonbPropertyOrder({"disburseType","desc","cur","disburseDate","disburseAmt"})
public class LoanDisbursementObject 
{
	
	@JsonbProperty("DisbursementType")
	private String disburseType;

	@JsonbProperty("DisbursementDate")
	private String disburseDate;  
	
	@JsonbProperty("Description")
	private String desc;
	
	@JsonbProperty("Currency")
	private String cur;
	
	@JsonbNumberFormat("###0.00")
	@JsonbProperty("DisbursementAmount")
	private Double disburseAmt;
	
	
	public LoanDisbursementObject() {

		this.setDisburseType(disburseType);
		this.setDisburseDate(disburseDate);
		this.setDisburseAmt(disburseAmt);
		this.setCur(cur);
		this.setDesc(desc);
	}


	public String getDisburseType() {
		return disburseType;
	}


	public void setDisburseType(String disburseType) {
		this.disburseType = disburseType;
	}


	public String getDisburseDate() {
		return disburseDate;
	}


	public void setDisburseDate(String disburseDate) {
		this.disburseDate = disburseDate;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getCur() {
		return cur;
	}


	public void setCur(String cur) {
		this.cur = cur;
	}


	public Double getDisburseAmt() {
		return disburseAmt;
	}


	public void setDisburseAmt(Double disburseAmt) {
		this.disburseAmt = disburseAmt;
	}

   

}
