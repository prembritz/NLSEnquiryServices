package com.nls.Enquiry;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KRASOAPValidation {

	private static Properties interfaceProperties;

	public static synchronized void initialiseInteface(Properties configProperties) {
		if (interfaceProperties == null) {
			interfaceProperties = configProperties;
		}
	}

	public static ArrayList GetEslipDetails(LinkedHashMap<String, String> HeaderMap)
			throws Exception {

		
		HashMap<String, String> nameSpaces = new HashMap<String, String>();
        String acNS = "tem";
        String soapNS = "soapenv";
        nameSpaces.put(soapNS, interfaceProperties.getProperty("SoapUrl"));
        nameSpaces.put(acNS, interfaceProperties.getProperty("EslipNS"));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element[] soapBodyElements = SOAPUtility.generateSoapEnvelope(doc, nameSpaces, soapNS);
        Element soapBody = soapBodyElements[SOAPUtility.SOAP_BODY];
        Element requestElement = SOAPUtility.createXMLElement(doc, soapBody, "SlipValidation", "", acNS);
        for (Map.Entry<String, String> Headers : HeaderMap.entrySet()) {
        	SOAPUtility.createXMLElement(doc, requestElement, Headers.getKey(), Headers.getValue(), acNS);
       }
        String requestMessage = SOAPUtility.getXMLAsString(doc);
        System.out.println(requestMessage);
        
        String destinationEndPoint = interfaceProperties.getProperty("EndPointUrl");
        String soapAction = interfaceProperties.getProperty("SoapAction");
        System.out.println("soapAction["+soapAction+"]");
        System.out.println("destinationEndPoint["+destinationEndPoint+"]");
        String responseMsg = "";
        int timeout = Integer.parseInt(interfaceProperties.getProperty("ServiceTimout", "15")) * 1000;    
                 // System.out.println("destinationEndPoint="+destinationEndPoint);
                  responseMsg = SOAPUtility.sendSoapMessage(destinationEndPoint, requestMessage, soapAction, timeout, timeout);
                  System.out.println(responseMsg);
       
				Object[] result = SOAPUtility.getSOAPResponseInfo(responseMsg);
				if ((Boolean) result[0] == true) {

					System.out.println("KRA Validation Response Successful");
					
					Element resultElement = (Element) ((Element) result[1]);
					Element Elem = (Element) resultElement.getElementsByTagName("SlipValidationResponse").item(0);
					NodeList ResultDetails = Elem.getElementsByTagName("SlipValidationResult");
					int cnt = ResultDetails.getLength();
					ArrayList KRAItems = new ArrayList<KRASOAPValidationObjects>();
					for (int itemI = 0; itemI < cnt; itemI++) {
					
						KRAItems.add(new KRASOAPValidationObjects((Element) ResultDetails.item(itemI)));
						
						String Status = SOAPUtility.getTextValue((Element) ResultDetails.item(itemI), "", "Status");
						String TaxpayerFullName = SOAPUtility.getTextValue((Element) ResultDetails.item(itemI), "", "TaxpayerFullName");
						System.out.println("Status [" + Status + "], TaxpayerFullName [" + TaxpayerFullName + "]");
						/*TicketDetails[0] = MessageId;
						TicketDetails[1] = TicketId;*/
					}
					return KRAItems;
				} else {
					System.out.println("KRA Validation Failed");
					throw new Exception(responseMsg);
				}

		//return TicketDetails;
	}

}
