package com.nls.Enquiry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CompressionUtils {

	private static final int NB_BYTE_BLOCK = 1024;

	/**
	 * compress and write in into out
	 * 
	 * @param in
	 *            the stream to be ziped
	 * @param out
	 *            the stream where to write
	 * @throws IOException
	 *             if a read or write problem occurs
	 */
	private static void compressWrite(InputStream in, OutputStream out)
			throws IOException {

		ByteArrayOutputStream tempBuffer = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int read = 0;
		do {
			read = in.read(buffer);
			if (read == -1) {
				break;
			} else {
				tempBuffer.write(buffer, 0, read);
			}
		} while (true);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(stream);
		gzip.write(tempBuffer.toByteArray());
		gzip.close();
		tempBuffer.close();
		byte[] compressed = stream.toByteArray();
		stream.close();
		out.write(compressed);
		out.flush();

		// DeflaterOutputStream deflaterOutput = new DeflaterOutputStream(out);
		// int nBytesRead = 1;
		// byte[] cur = new byte[NB_BYTE_BLOCK];
		// while (nBytesRead >= 0) {
		// nBytesRead = in.read(cur);
		// byte[] curResized;
		// if (nBytesRead > 0) {
		// if (nBytesRead < NB_BYTE_BLOCK) {
		// curResized = new byte[nBytesRead];
		// System.arraycopy(cur, 0, curResized, 0, nBytesRead);
		// } else {
		// curResized = cur;
		// }
		// deflaterOutput.write(curResized);
		// }
		//
		// }
		// deflaterOutput.close();
	}

	/**
	 * compress and write the string content into out
	 * 
	 * @param in
	 *            a string, compatible with UTF8 encoding
	 * @param out
	 *            an output stream
	 */
	public static void compressWrite(String in, OutputStream out) {
		InputStream streamToZip = null;
		try {
			streamToZip = new ByteArrayInputStream(in.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			compressWrite(streamToZip, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * uncompress and write int into out
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */

	/**
	 * uncompress and write in into a new string that is returned
	 * 
	 * @param in
	 * @return the string represented the unziped input stream
	 */
	public static String uncompressWrite(InputStream in) {
		StringBuilder sb = new StringBuilder();
		try {
			GZIPInputStream gis = new GZIPInputStream(in);
			BufferedReader br = new BufferedReader(new InputStreamReader(gis,
					"UTF-8"));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			gis.close();
			in.close();
		} catch (Exception except) {
			except.printStackTrace();
		}

		return sb.toString();
	}

	public static void main(String[] args) throws Exception {
		
		/*Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		HashMap<String, String> nameSpaces = new HashMap<String, String>();
		String soapNs = "soapenv";
		nameSpaces.put(soapNs, "http://schemas.xmlsoap.org/soap/envelope/");
		String reqNs = "log";
		nameSpaces.put(reqNs, "http://www.nlske.com/LoginSession");
		Element[] xmlElements = SOAPUtilities.generateSoapEnvelope(doc,
				nameSpaces, soapNs);
		Element bodyElem = xmlElements[SOAPUtilities.SOAP_BODY];
		Element reqElement = SOAPUtility.createXMLElement(doc, bodyElem,
				"CreateSessionRequest", "", reqNs);
		SOAPUtility.createXMLElement(doc, reqElement, "Username", "0975678602",
				"");
		SOAPUtility.createXMLElement(doc, reqElement, "Password", "1234", "");
		SOAPUtility.createXMLElement(doc, reqElement, "DateOfBirth",
				"2016-02-07", "");
		SOAPUtility.createXMLElement(doc, reqElement, "IMEI", "123456789", "");

		String reqContent = SOAPUtility.getXMLAsString(doc);
		ByteArrayOutputStream bop = new ByteArrayOutputStream();
//		CompressionUtils.compressWrite(reqContent, bop);
		String content = SOAPUtility.sendSoapMessageCompressed(
				"http://localhost:8028/SOAServices/CreateSession", reqContent,
				"http://www.nlske.com/CreateSession");
		System.out.println("Response Message " + content);*/
	}
}
