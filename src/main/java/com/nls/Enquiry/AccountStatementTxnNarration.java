package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AccountStatementTxnNarration {

	private static HashMap<String, String> ActualTableName = null;
	
	public static String ftAppl = "FUNDS.TRANSFER";
	public static String ttAppl = "TELLER";
	public static String acChgReqAppl = "AC.CHARGE.REQUEST";
	public static String fxAppl = "FOREX";
	
	public static Map<String, Map<String, String>> applFldMapgs = null;
	public static Map<String, String> paymentDetailFld = new HashMap<String, String>();
	public static Map<String, String> systemIdMap = new HashMap<String, String>();
	public static Map<String, String> liveTables = new HashMap<String, String>();
	public static Map<String, String> historyTables = new HashMap<String, String>();

	public static String ftHistTableT24 = "FBNK_FUNDS_TRANSFER#HIS";
	public static String ttHistTableT24 = "FBNK_TELLER#HIS";

	
	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountStatementTxnNarration.ActualTableName = ActualTableName;
	}
	
	public String extractDescriptionField(String systemId,
			String originalSystemId, Connection dbConnection, String txnRef, String txnDesc, StringBuffer description,
			ResultSet stmtEntryRs,String unitId) throws Exception {
		
		synchronized (paymentDetailFld) {
			if (paymentDetailFld.size() <= 0) {
				initialiseDescriptionFldMappings(dbConnection,unitId);
			}
		}
		
		String liveTableId = liveTables.get(systemId);
		ResultSet recordRs = null;
		boolean liveRecord = false;
		boolean histTable = false;

		PreparedStatement liveSt = null;
		PreparedStatement histSt = null;
		if (liveTableId != null) {
			liveTableId=ActualTableName.get(unitId+"-"+liveTableId);
			//liveTableId = Utilities.getTableName(dbConnection, configProperties, liveTableId);
			liveSt = dbConnection.prepareStatement("SELECT * FROM " + liveTableId + " WHERE ID = ?");
			System.out.println("SELECT * FROM " + liveTableId + "  WHERE ID = '" + txnRef + "'");
			liveSt.setString(1, txnRef);
			recordRs = liveSt.executeQuery();
			if (!recordRs.next()) {
				recordRs.close();
				liveSt = null;
			} else {
				liveRecord = true;
			}
		}
		String histTableId = null;
		if (!liveRecord) {
			histTableId = historyTables.get(systemId);
			if (histTableId != null) {
				histTableId = ActualTableName.get(unitId+"-"+histTableId);
				//histTableId = Utilities.getTableName(dbConnection, configProperties, histTableId);
				histSt = dbConnection
						.prepareStatement("SELECT * FROM " + histTableId + " WHERE ID = ?");
				System.out
						.println("SELECT * FROM " + histTableId + " WHERE ID = '" + txnRef + ";1'");
				histSt.setString(1, txnRef + ";1");
				recordRs = histSt.executeQuery();
				if (!recordRs.next()) {
					recordRs.close();
					recordRs = null;
				} else {
					histTable = true;
				}
			}
		}

	/*	if (systemIdMap.containsKey(originalSystemId) && !liveRecord && !histTable) {
			if (histTableId != null && !histTableId.trim().isEmpty()) {
				String hitTable = ftHistTableT24;
				if (systemId.toString().contains("FT")) {
					hitTable = ftHistTableT24;
				} else {
					hitTable = ttHistTableT24;

				}
				hitTable = Utilities.getTableName(dbConnection, configProperties, hitTable);
				Utilities.addExternalSystemOfflineSyncItem(dbConnection, configProperties, hitTable, txnRef + ";1");
			}
			throw new MissingCoreTransactionException();
		} */

		/*
		 * record exist read the version name and fetch mappings
		 * 
		 */
		String applName = systemId;
		if (systemIdMap.containsKey(systemId)) {
			applName = systemIdMap.get(systemId);
		}

		String descriptFldValue = null;
		System.out.println("[ " + txnRef + " ] Live REcord [ " + liveRecord + " ] Hist Record [ " + histTable + " ]");
		String versionName = null;
		if (liveRecord || histTable) {
			try {
				versionName = recordRs.getString("CBA_VER_NAME");
			} catch (Exception except) {

			}
		}
		if (versionName != null && !versionName.trim().isEmpty()) {
			versionName = versionName.split(" ")[0];
		}

		System.out.println("[ " + txnRef + " ] System Id [ " + systemId + " ]");
		String mappingId = (systemIdMap.containsKey(systemId) ? systemIdMap.get(systemId) : "") + "," + versionName;

		String narr = stmtEntryRs.getString("NARRATIVE");
		if (narr != null && narr.startsWith("AC-") && systemId.substring(0, 2).equals("FT")) {
			mappingId = "DEFAULT." + systemId + ".CHG";
		}

		System.out.println("Version Name [" + mappingId + " ]");
		String paymentDetailFldName = null;

		if (mappingId != null && paymentDetailFld.containsKey(mappingId)) {
			paymentDetailFldName = paymentDetailFld.get(mappingId);
		}
		System.out.println("[ " + txnRef + " ] Mapping [ " + mappingId + " ] Payment Detail Field ["
				+ paymentDetailFldName + " ]");
		if ((paymentDetailFldName == null || paymentDetailFldName.trim().isEmpty())
				&& paymentDetailFld.containsKey(applName)) {
			paymentDetailFldName = paymentDetailFld.get(applName);
			System.out.println("[ " + txnRef + " ] Mapping [ " + applName + " ] Payment Detail Field ["
					+ paymentDetailFldName + " ]");
		}

		StringBuffer paymentFldValue = null;
		if (paymentDetailFldName != null && !paymentDetailFldName.trim().isEmpty()) {
			String[] fldNames = paymentDetailFldName.split("\\^");
			for (String fldName : fldNames) {
				descriptFldValue = null;
				if (fldName.startsWith("SE!") && fldName.endsWith("NARRATIVE")) {
					descriptFldValue = txnDesc;
				} else if (fldName.startsWith("SE!") && fldName.endsWith("TRANS.REFERENCE")) {
					descriptFldValue = stmtEntryRs.getString("TRANS_REFERENCE");
				} else {

					if (fldName != null && applFldMapgs.containsKey(applName)) {
						fldName = applFldMapgs.get(applName).get(fldName);
					}
					if (fldName != null && (liveRecord || histTable)) {
						try {
							descriptFldValue = recordRs.getString(fldName);
						} catch (Exception except) {
							except.printStackTrace();
						}
					}
				}
				if (descriptFldValue != null) {
					if (paymentFldValue == null) {
						paymentFldValue = new StringBuffer();
					} else {
						paymentFldValue.append(" ");
					}
					paymentFldValue.append(descriptFldValue);
				}
			}
		}
		if (paymentFldValue != null && paymentFldValue.length() > 0) {
			descriptFldValue = paymentFldValue.toString();
		}
		// System.out.println("Payment Detail Field CM Field Value [" + descriptFldValue
		// + " ]");

		// System.out.println("Payment Detail Field CM Field Value [" + descriptFldValue
		// + " ] [ " + systemId+ " ] Live record [ " + liveRecord + " ] History REcord [
		// " + histTable + " ]");
		// if (systemId.equals("FT") && (liveRecord || histTable)) {
		// try {
		// descriptFldValue += " " + recordRs.getString("IN_PAYMENT_DETAILS");
		// } catch (Exception except) {
		// except.printStackTrace();
		// }
		//
		// }
		if (liveSt != null) {
			liveSt.close();
		}
		if (histSt != null) {
			histSt.close();
		}

		if (descriptFldValue != null && !descriptFldValue.trim().equals("null")) {
			description.append(descriptFldValue + " ");
		}
		
		return description.toString();
	}
	
	private void loadApplFieldMappings(Connection dbConnection,String t24applName,
			HashMap<String, String> fldMap,String unitId) throws SQLException {
		String cmFieldMappingTable = ActualTableName.get(unitId+"-T24$CM$FLD$MAPPING");
		try (PreparedStatement dbSt = dbConnection.prepareStatement(
				"SELECT * FROM " + cmFieldMappingTable + " WHERE T24_APPL_NAME = ?")) {
			dbSt.setString(1, t24applName);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				while (dbRs.next()) {
					fldMap.put(dbRs.getString("T24_FLD_NAME"), dbRs.getString("CM_FLD_NAME"));
				}
			}
		}

	}
	
	private void initialiseDescriptionFldMappings(Connection dbConnection,String unitId) throws Exception {
		String paymentDetails = null;
		String remitMappingTable = ActualTableName.get(unitId+"-CBA$REMIT$FIELD$MAPPING");
		try (PreparedStatement mappingSt = dbConnection
				.prepareStatement("SELECT * FROM " + remitMappingTable);
				ResultSet mappingRs = mappingSt.executeQuery()) {
			while (mappingRs.next()) {
				paymentDetails = mappingRs.getString("PAYMENT_DETAILS");
				if (paymentDetails != null && !paymentDetails.trim().isEmpty()) {
					paymentDetailFld.put(mappingRs.getString("ID"), paymentDetails);
				}
			}
		}

		// [PAYMENT.DETAILS^ORDERING.CUST^TELEX.FROM.CUST^CREDIT.THEIR.REF^DEBIT.THEIR.REF^FX.REFERENCE^IN.BK.TO.BK
		// ]

		applFldMapgs = new HashMap<String, Map<String, String>>();
		HashMap<String, String> fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, ftAppl, fldMap,unitId);
		applFldMapgs.put(ftAppl, fldMap);

		fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, ttAppl, fldMap,unitId);
		applFldMapgs.put(ttAppl, fldMap);

		fldMap = new HashMap<String, String>();
		loadApplFieldMappings(dbConnection, acChgReqAppl, fldMap,unitId);
		applFldMapgs.put(acChgReqAppl, fldMap);

		historyTables.put("FT", "FUNDS$TRANSFER_HISTORY");
		historyTables.put("TT", "TELLER_HISTORY");
		historyTables.put("AC", "AC$CHARGE$REQUEST$HIS");

		liveTables.put("FT", "FUNDS$TRANSFER");
		liveTables.put("TT", "TELLER");
		liveTables.put("AC", "AC$CHARGE$REQUEST");

		systemIdMap.put("FT", ftAppl);
		systemIdMap.put("TT", ttAppl);
		systemIdMap.put("AC", acChgReqAppl);
		systemIdMap.put("FX", fxAppl);
	}
}
