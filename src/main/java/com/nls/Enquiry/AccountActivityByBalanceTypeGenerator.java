package com.nls.Enquiry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AccountActivityByBalanceTypeGenerator {
	
	private static Map<String, String> fldMap = new HashMap<String, String>();
	static {
		fldMap.put("DAY_NO", "1");
		fldMap.put("TURNOVER_CREDIT", "2");
		fldMap.put("TURNOVER_DEBIT", "3");
		fldMap.put("BALANCE", "4");

		fldMap.put("BK_DAY_NO", "15");
		fldMap.put("BK_BALANCE", "16");
		fldMap.put("BK_CREDIT_MVMT", "17");
		fldMap.put("BK_DEBIT_MVMT", "18");

	}

	// public static void main(String[] args) throws Exception {
	// DriverManager.registerDriver(new com.mysql.jdbc.Driver());
	// Connection dbConnection =
	// DriverManager.getConnection("jdbc:mysql://localhost:3307/sheria", "root",
	// "root");
	// String accountNo = "6524620022";
	// String yyyyMMdd = "202102";
	// dbConnection.setAutoCommit(false);
	// AccountActivityByBalanceTypeGenerator.generateActivityByBalanceType(dbConnection,
	// "SHERIA", accountNo,
	// yyyyMMdd);
	// }

	public static void generateActivityByBalanceType(Connection dbConnection, String accountNo,
			String yyyyMM,String AcctBalanceActivityTable,String AcctActivityBalanceTypeTable) throws Exception {
		//String tblName = "ACCT$BALANCE$ACTIVITY";
	//	tblName = Utilities.getTableName(dbConnection, schemaName, tblName);
	//	String actTableDetail = "ACCT_ACTIVITY$BALANCE_TYPE";
		//actTableDetail = Utilities.getTableName(dbConnection, schemaName, actTableDetail);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		try (PreparedStatement acctBalanceActSt = dbConnection
				.prepareStatement("SELECT * FROM " + AcctBalanceActivityTable + " TBL WHERE ID = ?");
				PreparedStatement acctBalSt = dbConnection.prepareStatement(
						"SELECT TBL.* FROM " + AcctActivityBalanceTypeTable
								+ " TBL WHERE ID = ? AND BALANCE_TYPE = ?",
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
			acctBalanceActSt.setString(1, accountNo + "-" + yyyyMM);
			try (ResultSet acctBalanceActRs = acctBalanceActSt.executeQuery()) {
				// System.out.println("Fetched Full XML Content For Record [
				// "+accountNo+"-"+yyyyMM+" ]");
				if (acctBalanceActRs.next()) {
					Document doc = builder.parse(extractContent(acctBalanceActRs));
					final Element rootElement = doc.getDocumentElement();
					Map<String, Integer> balanceTypePos = extractBalanceTypePost(rootElement);

					// System.out.println(" XML Content "+accountNo+"-"+yyyyMM+" ] Balance Types
					// Available [ "+balanceTypePos+" ]");
					balanceTypePos.entrySet().stream().forEach(entry -> {
						// System.out.println("Extracting "+accountNo+"-"+yyyyMM+" ] Balance [
						// "+entry.getKey()+" ] [ "+entry.getValue()+" ]");
						generateBalanceTypeActivity(acctBalSt, accountNo, yyyyMM, entry.getKey(), entry.getValue(),
								rootElement);
					});
					if(dbConnection.getAutoCommit()==false)dbConnection.commit();

				}
			}
		}
	}

	private static InputStream extractContent(ResultSet acctBalanceActRs) throws Exception {
		ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
		InputStream ips = acctBalanceActRs.getBinaryStream("XML_CONTENT");
		byte[] temp = new byte[1024];
		int read = 0;
		do {
			read = ips.read(temp);
			if (read == -1) {
				break;
			} else {
				bufferStream.write(temp, 0, read);
			}
		} while (true);
		ips.close();
		bufferStream.flush();
		ByteArrayInputStream bufferInputStream = new ByteArrayInputStream(bufferStream.toString("UTF-8").getBytes());
		bufferStream.close();
		bufferStream = null;
		return bufferInputStream;
	}

	private static void generateBalanceTypeActivity(PreparedStatement acctBalSt, String accountNo, String yyyyMM,
			String balanceType, Integer balanceTypePos, final Element rootElement) {
		try {
			boolean newRecord = false;
			String id = accountNo + "-" + yyyyMM;
			acctBalSt.setString(1, id);
			acctBalSt.setString(2, balanceType);
			try (ResultSet acctRs = acctBalSt.executeQuery()) {
				newRecord = !acctRs.next();
				if (newRecord) {
					acctRs.moveToInsertRow();
				}
				acctRs.updateString("ID", id);
				acctRs.updateString("BALANCE_TYPE", balanceType);

				fldMap.entrySet().stream().forEach(entry -> {
					try {
						updateFldData(acctRs, rootElement, balanceTypePos, entry.getKey(), entry.getValue());
					} catch (Exception except) {
						throw new RuntimeException(except);
					}
				});
				if (newRecord) {
					acctRs.insertRow();
				} else {
					acctRs.updateRow();
				}

			}
		} catch (Exception except) {
			except.printStackTrace();
		}

	}

	private static void updateFldData(ResultSet acctRs, Element rootElement, Integer balanceTypePos, String fldName,
			String fldPos) throws SQLException {
		NodeList elementList = rootElement.getElementsByTagName("c2");
		int elemCnt = elementList.getLength();
		Element fldElem = null;
		String m = null;
		String s = null;
		String t = null;
		Vector<String> contentVector = new Vector<String>();
		String elementContent = null;
		for (int elemI = 0; elemI < elemCnt; elemI++) {
			fldElem = (Element) elementList.item(elemI);
			if (fldElem.getChildNodes().getLength() > 0) {
				elementContent = fldElem.getFirstChild().getNodeValue();
			} else {
				elementContent = "";
			}
			m = fldElem.getAttribute("m");
			if (m == null || m.isEmpty()) {
				m = "1";
			}
			if (Integer.parseInt(m) == balanceTypePos) {
				s = fldElem.getAttribute("s");
				if (s == null || s.isEmpty()) {
					s = "1";
				}
				if (Integer.parseInt(s) == Integer.parseInt(fldPos)) {
					t = fldElem.getAttribute("t");
					if (t == null || t.isEmpty()) {
						t = "1";
					}

					if (Integer.parseInt(t) == 1) {
						contentVector.add(elementContent);
					} else {
						int size = contentVector.size();
						if (size + 1 < Integer.parseInt(t)) {
							for (int i = size; i < Integer.parseInt(t) - 1; i++) {
								contentVector.add("");
							}
						}
						contentVector.add(elementContent);
					}

				}
			}
		}
		String content = contentVector.stream().collect(Collectors.joining("^"));
		// System.out.println(balanceTypePos + "/" + fldName + "=" + content);
		acctRs.updateString(fldName, content);

	}

	private static Map<String, Integer> extractBalanceTypePost(Element rootElement) {
		Map<String, Integer> balanceTypePos = new LinkedHashMap<String, Integer>();
		NodeList elemList = rootElement.getElementsByTagName("c1");
		int elemCnt = elemList.getLength();
		Element balElement = null;
		String attr = null;

		for (int elemI = 0; elemI < elemCnt; elemI++) {

			balElement = (Element) elemList.item(elemI);
			attr = balElement.getAttribute("m");
			if (attr == null || attr.isEmpty()) {
				attr = "1";
			}
			balanceTypePos.put(balElement.getFirstChild().getNodeValue(), Integer.parseInt(attr));
		}
		return balanceTypePos;
	}

}
