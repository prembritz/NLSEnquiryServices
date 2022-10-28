package com.nls.Enquiry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SOAPUtility {
	public static int SOAP_ENVELOPE = 0;
	public static int SOAP_HEADER = 1;
	public static int SOAP_BODY = 2;

	public static String sendSoapMessage(String serviceEndPoint, String requestMessage, String soapAction,
			Proxy proxyServer, int connectTimeout, int readTimeout) throws Exception {

		URL endPointUrl = new URL(serviceEndPoint);
		HttpURLConnection connection = (HttpURLConnection) endPointUrl.openConnection(proxyServer);

		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		byte[] content = requestMessage.toString().getBytes();
		connection.setRequestProperty("Content-Length", String.valueOf(content.length));
		connection.setRequestProperty("Content-Type", " text/xml;charset=UTF-8;action=" + soapAction);
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		java.io.OutputStream writer = connection.getOutputStream();
		writer.write(content);
		writer.flush();
		writer.close();

		return readResponse(connection);

	}

	public static String sendSoapMessage(String serviceEndPoint, String requestMessage, String soapAction,
			int connectTimeout, int readTimeout) throws Exception {

		System.out.println("End Point ="+serviceEndPoint);
		HttpURLConnection connection = (HttpURLConnection) new URL(serviceEndPoint).openConnection();
		connection.setConnectTimeout(connectTimeout);
		connection.setReadTimeout(readTimeout);

		byte[] content = requestMessage.toString().getBytes();
		connection.setRequestProperty("Content-Length", String.valueOf(content.length));
		// connection.setRequestProperty("Content-Type",
		// " text/xml;charset=UTF-8;action=" + soapAction);

		connection.setRequestProperty("Content-Type", "text/xml");
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		java.io.OutputStream writer = connection.getOutputStream();
		writer.write(content);
		writer.flush();
		writer.close();

		return readResponse(connection);

	}

	public static String sendSoapMessageCompressed(String serviceEndPoint, String stringMessage, String soapAction)
			throws Exception {

		HttpURLConnection connection = (HttpURLConnection) new URL(serviceEndPoint).openConnection();
		if (System.getProperty("ConnectTimeOut") != null && !System.getProperty("ConnectTimeOut").equals("")) {
			connection.setConnectTimeout(Integer.parseInt(System.getProperty("ConnectTimeOut")));
			connection.setReadTimeout(Integer.parseInt(System.getProperty("ReadTimeOut")));
		}
		// System.out.println("Before Decompression Size "
		// + stringMessage.getBytes().length);
		ByteArrayOutputStream bop = new ByteArrayOutputStream();
		CompressionUtils.compressWrite(stringMessage, bop);
		byte[] content = bop.toByteArray();
		// System.out.println("After Decompression Size " + content.length);
		bop.close();
		connection.setRequestProperty("Content-Length", String.valueOf(content.length));
		// connection.setRequestProperty("Content-Type",
		// " text/xml;charset=UTF-8;action=" + soapAction);

		connection.setRequestProperty("Content-Type", "text/xml");
		connection.setRequestProperty("Content-Encoding", "gzip");
		connection.setRequestProperty("SOAPAction", soapAction);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		java.io.OutputStream writer = connection.getOutputStream();
		writer.write(content);
		writer.flush();
		writer.close();

		byte[] responseBuffer = readResponse(connection, false);
		// System.out
		// .println("Before Decompression Size " + responseBuffer.length);
		ByteArrayInputStream bip = new ByteArrayInputStream(responseBuffer);
		String respContent = CompressionUtils.uncompressWrite(bip);
		// System.out.println("After Decompression Size "
		// + respContent.getBytes().length);
		bip.close();
		return respContent;
	}

	public static String sendSoapMessageWithAction(String serviceEndPoint, String requestMessage, String soapAction)
			throws Exception {

		HttpURLConnection connection = (HttpURLConnection) new URL(serviceEndPoint).openConnection();

		if (System.getProperty("ConnectTimeOut") != null && !System.getProperty("ConnectTimeOut").equals("")) {
			connection.setConnectTimeout(Integer.parseInt(System.getProperty("ConnectTimeOut")));
			connection.setReadTimeout(Integer.parseInt(System.getProperty("ReadTimeOut")));
		}

		byte[] content = requestMessage.toString().getBytes();
		connection.setRequestProperty("Content-Length", String.valueOf(content.length));
		connection.setRequestProperty("Content-Type", " text/xml;charset=UTF-8");
		connection.setRequestProperty("Action", soapAction);
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		java.io.OutputStream writer = connection.getOutputStream();
		writer.write(content);
		writer.flush();
		writer.close();

		return readResponse(connection);

	}

	private static String readResponse(HttpURLConnection connection) throws IOException {
		StringBuffer response = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String responseString = null;
			while ((responseString = reader.readLine()) != null) {
				response.append(responseString);
			}
		} catch (Exception except) {

			except.printStackTrace();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

			String responseString = null;
			response = new StringBuffer();
			while ((responseString = reader.readLine()) != null) {
				response.append(responseString);
			}
		}

		return response.toString();

	}

	private static byte[] readResponse(HttpURLConnection connection, boolean byteArrayResponse) throws IOException {

		try {
			InputStream ips = connection.getInputStream();
			return readByteStream(ips);

		} catch (Exception except) {

			except.printStackTrace();

			return readByteStream(connection.getErrorStream());
		}

	}

	public static byte[] readByteStream(InputStream ips) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] byteBuffer = new byte[1024];
		int read = 0;
		do {
			read = ips.read(byteBuffer);
			if (read == -1) {
				break;
			} else {
				buffer.write(byteBuffer, 0, read);
			}

		} while (true);
		ips.close();
		byte[] responseBytes = buffer.toByteArray();
		System.out.println("Received Response " + responseBytes.length);
		buffer.close();
		return responseBytes;
	}

	public static Element createXMLElement(Document document, Element parentElement, String elementName,
			String elementValue, String namespace) {

		Element tempElement = null;

		if (namespace == null || namespace.equals("")) {
			tempElement = document.createElement(elementName);
		} else {
			tempElement = document.createElement(namespace + ":" + elementName);
		}

		parentElement.appendChild(tempElement);

		if (elementValue != null && !elementValue.equals("")) {
			tempElement.setTextContent(elementValue);
		}

		return tempElement;
	}

	public static Element createXMLElement(Document document, Element parentElement, String elementName,
			Object elementValue, String namespace) {

		Element tempElement = null;

		if (namespace == null || namespace.equals("")) {
			tempElement = document.createElement(elementName);
		} else {
			tempElement = document.createElement(namespace + ":" + elementName);
		}

		parentElement.appendChild(tempElement);

		if (elementValue != null && !elementValue.equals("")) {
			tempElement.setTextContent(elementValue.toString());
		}

		return tempElement;
	}

	public static String getXMLAsString(Document rootElement) throws Exception {
		StringWriter xmlString = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(rootElement), new StreamResult(xmlString));

		return xmlString.toString();
	}

	public static Element[] generateEnvelope(Document document, HashMap<String, String> nameSpaces,
			String soapNameSpaceId) {

		Element[] soapItems = new Element[3];

		Element soapEnvelope = document.createElement("Envelope");

		Iterator<String> iterator = nameSpaces.keySet().iterator();

		String nameSpaceId;

		while (iterator.hasNext()) {
			nameSpaceId = iterator.next();
			soapEnvelope.setAttribute("xmlns", nameSpaces.get(nameSpaceId));
		}

		//Element soapHeader = document.createElement(soapNameSpaceId + ":Header");
		//soapEnvelope.appendChild(soapHeader);

		Element soapBody = document.createElement("Body");
		soapEnvelope.appendChild(soapBody);

		document.appendChild(soapEnvelope);

		soapItems[SOAP_ENVELOPE] = soapEnvelope;
		//soapItems[SOAP_HEADER] = soapHeader;
		soapItems[SOAP_BODY] = soapBody;

		return soapItems;
	}
	
	public static Element[] generateSoapEnvelope(Document document, HashMap<String, String> nameSpaces,
			String soapNameSpaceId) {

		Element[] soapItems = new Element[3];

		Element soapEnvelope = document.createElement(soapNameSpaceId + ":Envelope");

		Iterator<String> iterator = nameSpaces.keySet().iterator();

		String nameSpaceId;

		while (iterator.hasNext()) {
			//System.out.println("*********************");
			nameSpaceId = iterator.next();
			soapEnvelope.setAttribute("xmlns:"+nameSpaceId, nameSpaces.get(nameSpaceId));
		}

		Element soapHeader = document.createElement(soapNameSpaceId + ":Header");
		soapEnvelope.appendChild(soapHeader);

		Element soapBody = document.createElement(soapNameSpaceId + ":Body");
		soapEnvelope.appendChild(soapBody);

		document.appendChild(soapEnvelope);

		soapItems[SOAP_ENVELOPE] = soapEnvelope;
		soapItems[SOAP_HEADER] = soapHeader;
		soapItems[SOAP_BODY] = soapBody;

		return soapItems;
	}
	
	public static Element generateServiceNameSpace(Document document, Element parentElement, String elementName,
			String elementValue, String namespace) {

		Element ServiceElement = document.createElement(elementName);
	     
		ServiceElement.setAttribute(namespace, elementValue);

		parentElement.appendChild(ServiceElement);
	
		return ServiceElement;
	}

	public static Object[] getSOAPResponseInfo(String responseMessage)
			throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		Document responseDoc = factory.newDocumentBuilder()
				.parse(new InputSource(new ByteArrayInputStream(responseMessage.getBytes())));

		Element bodyElement = (Element) responseDoc.getElementsByTagNameNS("*", "Body").item(0);

		Object[] reponseInfo = new Object[2];

		NodeList faultList = bodyElement.getElementsByTagNameNS("*", "Fault");

		if (faultList.getLength() > 0) {
			reponseInfo[0] = false;
			reponseInfo[1] = ((Element) faultList.item(0));
		} else {
			reponseInfo[0] = true;
			reponseInfo[1] = bodyElement;
		}

		return reponseInfo;
	}

	public static Object[] getSOAPResponseInfoUTF8(String responseMessage)
			throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		InputStream is = new ByteArrayInputStream(responseMessage.getBytes());
		InputStreamReader reader = new InputStreamReader(is, "UTF-8");

		Document responseDoc = factory.newDocumentBuilder().parse(new InputSource(reader));

		Element bodyElement = (Element) responseDoc.getElementsByTagNameNS("*", "Body").item(0);

		Object[] reponseInfo = new Object[2];

		NodeList faultList = bodyElement.getElementsByTagNameNS("*", "Fault");

		if (faultList.getLength() > 0) {
			reponseInfo[0] = false;
			reponseInfo[1] = ((Element) faultList.item(0));
		} else {
			reponseInfo[0] = true;
			reponseInfo[1] = bodyElement;
		}

		return reponseInfo;
	}

	public static String getTextValue(Element responseElements, String namespace, String elementName) {
		String value = "";
		NodeList list = responseElements.getElementsByTagNameNS(namespace, elementName);
		if (list.getLength() > 0) {
			value = list.item(0).getTextContent();
		} else {
			list = responseElements.getElementsByTagName(elementName);
			if (list.getLength() > 0) {
				value = list.item(0).getTextContent();
			}
		}
		return value;
	}
	
	public static String getTextValueofDuplicateChild(Element responseElements, String namespace,
			String elementName,String SubelementName) {
		String value = "";
		
		 NodeList nList1 = responseElements.getElementsByTagName(elementName);
	        for (int i = 0; i < nList1.getLength(); i++) {
	           Node nNode = nList1.item(i);
	           if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        Element eElement = (Element) nNode;
	        value = eElement.getElementsByTagName(SubelementName).item(i).getTextContent();
	       // System.out.println(value);
	    }
	}
		return value;
	}

}
