package com.nls.Enquiry;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbPropertyOrder;
import javax.json.bind.annotation.JsonbTypeAdapter;

@JsonbPropertyOrder({"unId","transList"})
public class TransactionStatusList
{

    @JsonbProperty("UnitID")
    private String unId;
    
   @JsonbProperty("TransactionData")
	private List<TransactionStatusObject> transList;
	
	public TransactionStatusList() {
		transList = new ArrayList<TransactionStatusObject>();
		
	}
	
	public void addAccount(TransactionStatusObject object) {
		this.transList.add(object);
	}

	public String getUnId() {
		return unId;
	}

	public void setUnId(String unId) {
		this.unId = unId;
	}

	public List<TransactionStatusObject> getTransList() {
		return transList;
	}

	public void setTransList(List<TransactionStatusObject> transList) {
		this.transList = transList;
	}

	
}
