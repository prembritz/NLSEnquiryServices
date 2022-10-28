package com.nls.Enquiry;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CCAcquireTicket {

	private static Properties interfaceProperties;

	public static synchronized void initialiseInteface(Properties configProperties) {
		if (interfaceProperties == null) {
			interfaceProperties = configProperties;
		}
	}

	public static ArrayList GetTicket(LinkedHashMap<String, String> HeaderMap, LinkedHashMap<String, String> TicketMap)
			throws Exception {

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,
				new NTCredentials(interfaceProperties.getProperty("AuthKey"),
						interfaceProperties.getProperty("AuthPass"), InetAddress.getLocalHost().getHostName(),
						interfaceProperties.getProperty("Domain")));
		Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider>create()
				.register(AuthSchemes.NTLM, new NTLMSchemeFactory()).build();
		RequestConfig config = RequestConfig.custom().setTargetPreferredAuthSchemes(
				Arrays.asList(AuthSchemes.NTLM, AuthSchemes.KERBEROS, AuthSchemes.SPNEGO)).build();
		CloseableHttpClient httpclient = HttpClients.custom().setDefaultAuthSchemeRegistry(authSchemeRegistry)
				.setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider).build();
		String responseMsg = "";
		try {
			int timeout = Integer.parseInt(interfaceProperties.getProperty("ServiceTimout")) * 1000;
			RequestConfig.Builder requestConfig = RequestConfig.custom();
			requestConfig.setConnectTimeout(timeout);
			requestConfig.setConnectionRequestTimeout(timeout);
			requestConfig.setSocketTimeout(timeout);

			requestConfig.setRedirectsEnabled(true);
			requestConfig.setRelativeRedirectsAllowed(true);
			requestConfig.setCircularRedirectsAllowed(true);

			// RequestConfig config = requestConfig.build();
			HttpPost post = new HttpPost(interfaceProperties.getProperty("EndPointUrl"));
			post.setHeader("SOAPAction", interfaceProperties.getProperty("AcquireTicketSoapAction"));
			post.setConfig(config);
			HashMap<String, String> nameSpaces = new HashMap<String, String>();
			// String acNS = "prim";
			// String soapNS = "soapenv";
			String acNS = "";
			String soapNS = "";
			String Envelope = "Envelope";
			String Namespace = "xmlns";
			nameSpaces.put(Envelope, interfaceProperties.getProperty("SoapUrl"));
			// nameSpaces.put(Namespace, AcquireTicketNS);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element[] soapElements = SOAPUtility.generateEnvelope(doc, nameSpaces, soapNS);
			Element soapBody = soapElements[SOAPUtility.SOAP_BODY];
			Element requestElement1 = SOAPUtility.generateServiceNameSpace(doc, soapBody, "AcquireTicket",
					interfaceProperties.getProperty("CreditCardNS"), Namespace);
			// Element requestElement1 = SOAPUtility.createXMLElement(doc, soapBody,
			// "AcquireTicket", AcquireTicketNS, Namespace);
			Element requestElement = SOAPUtility.createXMLElement(doc, requestElement1, "xmlRequest", "", acNS);
			Element Header = SOAPUtility.createXMLElement(doc, requestElement, "Header", "", acNS);
			// SOAPUtility.createXMLElement(doc, Header, "AuthKey",
			// interfaceProperties.getProperty("AuthKey"), acNS);
			// SOAPUtility.createXMLElement(doc, Header, "AuthPassword",
			// interfaceProperties.getProperty("AuthPassword"),acNS);
			for (Map.Entry<String, String> Headers : HeaderMap.entrySet()) {
				SOAPUtility.createXMLElement(doc, Header, Headers.getKey(), Headers.getValue(), acNS);
			}

			Element Ticket = SOAPUtility.createXMLElement(doc, requestElement, "Ticket", "", acNS);

			for (Map.Entry<String, String> TicketChilds : TicketMap.entrySet()) {
				SOAPUtility.createXMLElement(doc, Ticket, TicketChilds.getKey(), TicketChilds.getValue(), acNS);
			}

			// SOAPUtility.createXMLElement(doc, requestElement, "AccountNumber", accRef,
			// acNS);
//				SOAPUtility.createXMLElement(doc, requestElement, "TransactionType", transactionType, acNS);
			String requestMessage = SOAPUtility.getXMLAsString(doc);
			System.out.println(requestMessage);
			post.setEntity(new XMLEntity(requestMessage));
			HttpHost target = new HttpHost(interfaceProperties.getProperty("ServerUrl"),
					Integer.parseInt(interfaceProperties.getProperty("ServerPort")));
			
			System.out.println("Executing NLTM Authenticated Request " + post.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(target, post);
			try {
				System.out.println(response.getStatusLine());
				HttpEntity entity = response.getEntity();
				InputStream ips = entity.getContent();
				ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
				byte[] data = new byte[1024];
				int read = 0;
				do {
					read = ips.read(data);
					if (read == -1) {
						break;
					} else {
						bufferStream.write(data, 0, read);
					}
				} while (true);
				responseMsg = new String(bufferStream.toByteArray());
				bufferStream.close();
				ips.close();
				EntityUtils.consume(entity);

				System.out.println("NLTM Authenticated Response Received  [ " + responseMsg + " ]");

				Object[] result = SOAPUtility.getSOAPResponseInfo(responseMsg);
				if ((Boolean) result[0] == true) {

					System.out.println("Ticket Generation Response Successful");
					
					Element resultElement = (Element) ((Element) result[1]);
					Element Elem = (Element) resultElement.getElementsByTagName("AcquireTicketResponse").item(0);
					NodeList ResultDetails = Elem.getElementsByTagName("AcquireTicketResult");
					int cnt = ResultDetails.getLength();
					ArrayList TicketItems = new ArrayList<CCAcquireTicketObjects>();
					for (int itemI = 0; itemI < cnt; itemI++) {
					
						TicketItems.add(new CCAcquireTicketObjects((Element) ResultDetails.item(itemI)));
						
						/*MessageId = SOAPUtility.getTextValue((Element) ResultDetails.item(itemI), "", "MessageID");
						TicketId = SOAPUtility.getTextValue((Element) ResultDetails.item(itemI), "", "Ticket");
						System.out.println("Message Id [" + MessageId + "], Ticket [" + TicketId + "]");
						TicketDetails[0] = MessageId;
						TicketDetails[1] = TicketId;*/
					}
					return TicketItems;
				} else {
					System.out.println("Ticket Generation Result Failed");
					throw new Exception(responseMsg);
				}
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		//return TicketDetails;
	}

}
