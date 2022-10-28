package com.nls.Enquiry;

import javax.json.bind.annotation.JsonbProperty;

import org.w3c.dom.Element;

public class CCAcquireTicketObjects {

	@JsonbProperty("MessageId")
	private String messageId;
	
	@JsonbProperty("Ticket")
	private String ticket;
	
	public CCAcquireTicketObjects(Element resultElem) {
		this.messageId = SOAPUtility.getTextValue(resultElem, "", "MessageID");
        this.ticket = SOAPUtility.getTextValue(resultElem, "", "Ticket");
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

}
