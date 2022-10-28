package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.opentracing.Traced;

@Path("/AccountTransactionAdvise")
public class AccountTransactionAdvise {

	private static HashMap<String, String> descriptionMappings;
	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	AccountTransactionAdviseObject account = null;
	
	private static String AcctTableRef = "ACCOUNT";
	private static String AccActivityRef = "ACCT_ACTIVITY";
	private static String AccStmtPrintRef = "ACCT$STMT$PRINT";
	private static String StmtEntryTableRef = "STATEMENT$ENTRY";
	private static String StmtEntryDetailsTableRef = "STATEMENT$ENTRY$DETAILS";
	private static String StmtPrintedRef = "STMT$PRINTED";
	private static String StmtEntryDetailsXrefTableRef = "STMT_ENTRY_DETAIL_XREF";
	private static String FundsTransferRef = "FUNDS$TRANSFER";
	private static String FundsTransferHistoryRef = "FUNDS$TRANSFER_HISTORY";
	private static String TellerRef = "TELLER";
	private static String TellerHistoryRef = "TELLER_HISTORY";
	private static String customerRef = "CUSTOMER";
	
	private static Map<String, Map<String, String>> applFldMapgs = null;
	private static Map<String, String> paymentDetailFld = new HashMap<String, String>();
	private static Map<String, String> systemIdMap = new HashMap<String, String>();
	private static Map<String, String> TableColumns = new HashMap<String, String>();
	private static Map<String, String> liveTables = new HashMap<String, String>();
	private static Map<String, String> historyTables = new HashMap<String, String>();
	
	// latest narrative changes
	private static String ftAppl = "FUNDS.TRANSFER";
	private static String ttAppl = "TELLER";
	private static String acChgReqAppl = "AC.CHARGE.REQUEST";
	private static String fxAppl = "FOREX";

	public static void setDBPool(DataSource cmDBPool) {
		AccountTransactionAdvise.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountTransactionAdvise.ActualTableName = ActualTableName;
	}
 
	public static void setInitiailizeGlobalParameters(HashMap<String, String> GlobalParameters) {
		AccountTransactionAdvise.descriptionMappings = GlobalParameters;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = AccountTransactionAdviseObject.class, responseDescription = "Account Transaction Advise Response", responseCode = "200")
	@Operation(summary = "Account Transaction Advise Request", description = "returns Account Transaction Advise Data")
	public Response getAccountTransactionAdvise(
			@RequestBody(description = "Account Transaction Request Id", required = true, content = @Content(mediaType = "application/json", 
			schema = @Schema(implementation = AccountTransactionAdviseRequest.class))) AccountTransactionAdviseRequest id) {

		String unitId = id.unitID;
		String uniqRef=id.uniqueTransactionLegNo;
		String instrumentNumber=id.instrumentReferenceNumber;
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Account Transaction Advise Interface Started on ["+startTime+"]");
			
			int TotalTxn = 0;
			
			System.out.println("Account Transaction Advise Table Ref [" + AcctTableRef + "," + AccActivityRef + ","
					+ AccStmtPrintRef + "," + StmtEntryTableRef + "," + StmtEntryDetailsTableRef + ","
					+ StmtEntryDetailsXrefTableRef + "," + FundsTransferRef + "," + TellerRef + ",["+customerRef+"]]");

			String AcctTableName = ActualTableName.get(unitId + "-" + AcctTableRef);
			String AcctActivityTableName = ActualTableName.get(unitId + "-" + AccActivityRef);
			String AcctStmtPrintTableName = ActualTableName.get(unitId + "-" + AccStmtPrintRef);
			
			String narrLength=descriptionMappings.get("NARRATION_LENGTH");
			System.out.println("Narration Length:::::::::::::"+narrLength);

			System.out.println("Account Transaction Advise Table Names [" + AcctTableName + "," + AcctActivityTableName + ","
					+ AcctStmtPrintTableName + "]");

			try (Connection dbConnection = cmDBPool.getConnection()) {
					TotalTxn = generateStatement(dbConnection, uniqRef, instrumentNumber, unitId,narrLength);
					System.out.println("TotalTxn Count ["+TotalTxn+"]");
					if (TotalTxn<0) {
						ResponseMessages(account,unitId,uniqRef,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(account).build();
				    }

			}catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(account,unitId,uniqRef,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(account).build();
			}

				
			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(account,unitId,uniqRef,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(account).build();
			}finally {
				LocalDateTime endTime = LocalDateTime.now();
				long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
				System.out.println("Account Transaction Advise Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
			}

			return Response.status(Status.ACCEPTED).entity(account).build();

	}

	private void ResponseMessages(AccountTransactionAdviseObject accountAdvise,String unitId,String uniq,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		accountAdvise.setErrorcode(ErrorCode);
		accountAdvise.setErrormessage(ErrorDescription);
		accountAdvise.setUnitid(unitId);
		accountAdvise.setUniqueTransactionLegNo(uniq);
		
	}
	
	private Integer generateStatement(Connection dbConnection, String uniqRef,
			String instrumentNumber,  String UnitID,String narrLength) throws Exception {

		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		int TransactionSeqNo = 0;
		System.out.println(" [AccountSummary],[generateStatement] condition Has Been Started on [" + timestamp1 + "]");
		
		
		String StmtEntryTable = ActualTableName.get(UnitID + "-" + StmtEntryTableRef);
		String StmtEntryDetailsTable = ActualTableName.get(UnitID + "-" + StmtEntryDetailsTableRef);
		String StmtPrintedTable = ActualTableName.get(UnitID + "-" + StmtPrintedRef);
		String detailReferencesTable = ActualTableName.get(UnitID + "-" + StmtEntryDetailsXrefTableRef);
		String FundTransferTable = ActualTableName.get(UnitID + "-" + FundsTransferRef);
		String TellerTable = ActualTableName.get(UnitID + "-" + TellerRef);
		String FundTransferHistoryTable = ActualTableName.get(UnitID + "-" + FundsTransferHistoryRef);
		String TellerHistoryTable = ActualTableName.get(UnitID + "-" + TellerHistoryRef);
		String customerTab = ActualTableName.get(UnitID + "-" + customerRef);

		System.out.println("Account Transaction Advise Table Names [" + StmtEntryTable + "," + StmtEntryDetailsTable + ","
				+ StmtPrintedTable + "," + detailReferencesTable + "," + FundTransferTable + ","
						+ ""+FundTransferHistoryTable+"," + TellerTable + ","+TellerHistoryTable+"]");
		

		account = new AccountTransactionAdviseObject();
		
		boolean flag=false;

		// String statementTable = tablePrefix + "STATEMENT$ENTRY";

		try (PreparedStatement FTStatement = dbConnection
				.prepareStatement("select DEBIT_ACCOUNT,CREDIT_ACCOUNT,BENEFICIARY2,DEBIT_AMOUNT,CREDIT_AMOUNT,CHEQUE_NUMBER,TRANSACTION_TYPE from "
						+ FundTransferTable + " where ID = ? or ID = ?");
				
				PreparedStatement FTHistStatement = dbConnection
						.prepareStatement("select DEBIT_ACCOUNT,CREDIT_ACCOUNT,BENEFICIARY2,DEBIT_AMOUNT,CREDIT_AMOUNT,CHEQUE_NUMBER,TRANSACTION_TYPE from "
								+ FundTransferHistoryTable + " where ID = ? or ID = ?");
				
				PreparedStatement customerNamePs = dbConnection
						.prepareStatement("select SHORT_NAME from "+ customerTab + " where ID = ? ");
				
				PreparedStatement TellerStatement = dbConnection.prepareStatement("select NARRATIVE_TWO,ACCOUNT1,ACCOUNT2,AMOUNT_LOCAL1,AMOUNT_LOCAL2,"
						+ " TRANS_TYPE from " + TellerTable + " where id = ? or id = ? ");
				
				PreparedStatement TellerHistStatement = dbConnection.prepareStatement("select NARRATIVE_TWO,AMOUNT_LOCAL1,AMOUNT_LOCAL2,"
						+ " TRANS_TYPE from " + TellerHistoryTable + " where id = ? or id = ? ");
				
				PreparedStatement stmtStatement = dbConnection
						.prepareStatement(
								"SELECT ID,BOOKING_DATE,OUR_REFERENCE,VALUE_DATE,PROCESSING_DATE,CRF_TYPE,NARRATIVE,"
										+ " EXCHANGE_RATE,THEIR_REFERENCE,TRANS_REFERENCE,TRANSACTION_CODE,CURRENCY,"
										+ "CUSTOMER_ID,AMOUNT_FCY,AMOUNT_LCY,CHEQUE_NUMBER,SYSTEM_ID FROM "
										+ StmtEntryTable + " WHERE ID = ? or TRANS_REFERENCE=?",
								ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
			ResultSet stmtRecord = null;
			
			stmtStatement.setString(1, uniqRef); 
			stmtStatement.setString(2, instrumentNumber);
			stmtRecord = stmtStatement.executeQuery();
			if (stmtRecord.next()) {
				flag=true;
				account.setValuedate(DateFormtter.format(TimeFormat.parse("" + stmtRecord.getString("VALUE_DATE"))));
				account.setUserreference(stmtRecord.getString("OUR_REFERENCE"));
				account.setUniqueTransactionLegNo(stmtRecord.getString("ID")==null?"":stmtRecord.getString("ID"));
				account.setTransferamount(stmtRecord.getDouble("AMOUNT_LCY"));
				account.setHostreferencenumber(instrumentNumber); 
				
				
				customerNamePs.setString(1, stmtRecord.getString("CUSTOMER_ID"));
				ResultSet customernameRs=customerNamePs.executeQuery();
				if(customernameRs.next()) {
					account.setCustomername(customernameRs.getString("SHORT_NAME"));
				}else {
					account.setCustomername("");
				}
				customerNamePs.clearParameters();
				customernameRs.close();
				
	
				StringBuffer description = new StringBuffer();
				String chqNo = stmtRecord.getString("CHEQUE_NUMBER");
				if (chqNo != null && !chqNo.isEmpty()) {
					description.append("CHQ-");
					description.append(chqNo);
					description.append(" ");
				}
	
				String systemId = stmtRecord.getString("SYSTEM_ID");
				String originalSystemId = systemId;
				if (systemId != null && systemId.length() > 2) {
					systemId = systemId.substring(0, 2);
				}
				String TransactionDesc = TransactionDescription(dbConnection,
						stmtRecord.getString("TRANSACTION_CODE"), UnitID);
	
				System.out.println("systemId[" + systemId + "]");
			//	System.out.println("originalSystemId[" + originalSystemId + "]");
				//System.out.println("TransactionDesc[" + TransactionDesc + "]");
				System.out.println("TransRef[" + stmtRecord.getString("TRANS_REFERENCE") + "]");
				System.out.println("description[" + description + "]");
	
				String transId = stmtRecord.getString("TRANS_REFERENCE");
				if (transId.indexOf("\\") != -1) {
					transId = transId.substring(0, transId.indexOf("\\"));
				}
				
				extractDescriptionField(systemId, originalSystemId, dbConnection,
						transId, TransactionDesc, description,
						stmtRecord, UnitID);
				
				String stmtNarrative = stmtRecord.getString("NARRATIVE");
				if (description.length() <= 0 && stmtNarrative != null
						&& !stmtNarrative.trim().isEmpty()) {
					description.append(stmtNarrative.trim());
				}
				if (description.length() <= 0) {
					description.append(TransactionDesc);
				}
	
				int NarrationLength = Integer.parseInt(narrLength);
				System.out.println("NarrationLength[" + NarrationLength + "]");
				String desc = description.length() < NarrationLength ? description.toString()
						: description.toString().substring(0, NarrationLength);
				if (desc != null) {
					desc = AccountUtilities.rationalizeInvalidChars(desc);
					desc = desc.replaceAll("\\^", " ");
				}
				System.out.println("desc[" + desc + "]");
				account.setNarration(desc);
				// account.setNarr(stmtRecord.getString("NARRATIVE"));
				
	
				 if (transId.startsWith("FT")) {
					// System.out.println(entryIds[entryI] + " " + transId);
	
					FTStatement.setString(1, transId);
					FTStatement.setString(2, transId + ";1");
					ResultSet FTEntries = FTStatement.executeQuery();
					if (FTEntries.next()) {
						account.setDebitaccountnumber(FTEntries.getString("DEBIT_ACCOUNT"));
						account.setCreditaccountnumber(FTEntries.getString("CREDIT_ACCOUNT"));
						account.setBeneficiaryname(FTEntries.getString("BENEFICIARY2")==null ||
								FTEntries.getString("BENEFICIARY2").equals("")?"":FTEntries.getString("BENEFICIARY2"));
						FTStatement.clearParameters();
						FTEntries.close();
					}
					else
					{
						FTHistStatement.setString(1, transId);
						FTHistStatement.setString(2, transId + ";1");
						ResultSet FTHistEntries = FTHistStatement.executeQuery();
						if (FTHistEntries.next()) {
							account.setDebitaccountnumber(FTHistEntries.getString("DEBIT_ACCOUNT"));
							account.setDebitaccountnumber(FTHistEntries.getString("CREDIT_ACCOUNT"));
							account.setBeneficiaryname("");
							FTHistStatement.clearParameters();
							FTHistEntries.close();
						}
					}
				} else if (transId.startsWith("TT")) {
	
					TellerStatement.setString(1, transId);
					TellerStatement.setString(2, transId + ";1");
	
					ResultSet TellerEntries = TellerStatement.executeQuery();
					if (TellerEntries.next()) {
						account.setDebitaccountnumber(TellerEntries.getString("ACCOUNT1"));
						account.setCreditaccountnumber(TellerEntries.getString("ACCOUNT2"));
						account.setBeneficiaryname(TellerEntries.getString("NARRATIVE_TWO")==null ||
								     TellerEntries.getString("NARRATIVE_TWO").equals("")?"":TellerEntries.getString("NARRATIVE_TWO"));
						TellerStatement.clearParameters();
						TellerEntries.close();
					}
					else
					{
						TellerHistStatement.setString(1, transId);
						TellerHistStatement.setString(2, transId + ";1");
	
						ResultSet TellerHistEntries = TellerHistStatement.executeQuery();
						if (TellerHistEntries.next()) {
							
							account.setDebitaccountnumber(TellerHistEntries.getString("ACCOUNT1"));
							account.setCreditaccountnumber(TellerHistEntries.getString("ACCOUNT2"));
							account.setBeneficiaryname("");
							
							TellerHistStatement.clearParameters();
							TellerHistEntries.close();
						}
					}
	
				}
				
				}
				stmtRecord.close();
				stmtStatement.clearParameters();
				
				if(!flag) {
					System.out.println("Transaction:[" +uniqRef+"] is not found");
					System.out.println("Reference [" +instrumentNumber+"] is not found");
					account.setErrorcode(ERROR_CODE.NOT_FOUND);
					account.setErrormessage(ErrorResponseStatus.DATA_NOT_FOUND.getValue());
					account.setUnitid(UnitID);
					account.setUniqueTransactionLegNo(uniqRef);
				}

							
			customerNamePs.close();	
			stmtStatement.close();
			FTStatement.close();
			FTHistStatement.close();
			TellerStatement.close();
			TellerHistStatement.close();

		} catch (Exception e) {
			e.printStackTrace();
			account.setErrorcode(ERROR_CODE.NOT_FOUND);
			account.setErrormessage(ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
			account.setUnitid(UnitID);
			account.setUniqueTransactionLegNo(uniqRef);
			return -1;
		}

	timestamp1 = new Timestamp(System.currentTimeMillis());
	System.out.println(" [AccountSummary],[generateStatement] condition Has Been Completed on [" + timestamp1 + "]");
	
	return TransactionSeqNo; 
}

private String TransactionDescription(Connection dbconnection, String TransCode, String unitId) {

	String TransDesc = "";
	String TransactionTable = ActualTableName.get(unitId + "-" + "TRANSACTION");
	try {
		PreparedStatement TransactionDescPS = dbconnection
				.prepareStatement("SELECT description FROM " + TransactionTable + "  where id = ? ");
		TransactionDescPS.setString(1, TransCode);
		ResultSet TransactionDescRS = TransactionDescPS.executeQuery();
		if (TransactionDescRS.next()) {
			TransDesc = TransactionDescRS.getString(1);
		}
		TransactionDescPS.close();
		TransactionDescRS.close();
	} catch (Exception e) {
		e.printStackTrace();
	}

	return TransDesc;
}

private void extractDescriptionField(String systemId, String originalSystemId, Connection dbConnection,
		String txnRef, String txnDesc, StringBuffer description, ResultSet stmtEntryRs, String unitId)
		throws Exception {

	Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
	System.out.println(" [AccountSummary],[extractDescriptionField] condition Has Been Started on [" + timestamp1 + "]");
	
	synchronized (paymentDetailFld) {
		if (paymentDetailFld.size() <= 0) {
			initialiseDescriptionFldMappings(dbConnection, unitId);
		}
	}

	String liveTableId = liveTables.get(systemId);
	ResultSet recordRs = null;
	boolean liveRecord = false;
	boolean histTable = false;

	PreparedStatement liveSt = null;
	PreparedStatement histSt = null;
	if (liveTableId != null) {
		liveTableId = ActualTableName.get(unitId + "-" + liveTableId);
		// liveTableId = Utilities.getTableName(dbConnection, configProperties,
		// liveTableId);
		liveSt = dbConnection.prepareStatement("SELECT * FROM " + liveTableId + " WHERE ID = ?");
		System.out.println("SELECT * FROM " + liveTableId + "  WHERE ID = '" + txnRef + "'");
		liveSt.setString(1, txnRef);
		recordRs = liveSt.executeQuery();
		if (!recordRs.next()) {
			// recordRs.close();
			liveSt = null;
		} else {
			liveRecord = true;
		}
	}

	String histTableId = null;
	if (!liveRecord) {
		histTableId = historyTables.get(systemId);
		if (histTableId != null) {
			histTableId = ActualTableName.get(unitId + "-" + histTableId);
			// histTableId = Utilities.getTableName(dbConnection, configProperties,
			// histTableId);
			histSt = dbConnection.prepareStatement("SELECT * FROM " + histTableId + " WHERE ID = ?");
			System.out.println("SELECT * FROM " + histTableId + " WHERE ID = '" + txnRef + ";1'");
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

	/*
	 * if (systemIdMap.containsKey(originalSystemId) && !liveRecord && !histTable) {
	 * if (histTableId != null && !histTableId.trim().isEmpty()) { String hitTable =
	 * ftHistTableT24; if (systemId.toString().contains("FT")) { hitTable =
	 * ftHistTableT24; } else { hitTable = ttHistTableT24;
	 * 
	 * } hitTable = Utilities.getTableName(dbConnection, configProperties,
	 * hitTable); Utilities.addExternalSystemOfflineSyncItem(dbConnection,
	 * configProperties, hitTable, txnRef + ";1"); } throw new
	 * MissingCoreTransactionException(); }
	 */

	/*
	 * record exist read the version name and fetch mappings
	 * 
	 */
	String applName = systemId;
	if (systemIdMap.containsKey(systemId)) {
		applName = systemIdMap.get(systemId);
	}

	String descriptFldValue = null;
	System.out.println("[ " + txnRef + " ] Live Record [ " + liveRecord + " ] Hist Record [ " + histTable + " ]");
	String versionName = null;
	if (liveRecord || histTable) {
		try {
			//System.out.println("version name");
			versionName = recordRs.getString("CBA_VER_NAME");
		//	System.out.println("version name["+versionName+"]");
		} catch (Exception except) {
       //System.out.println("((((((((((((((((((((((((((");
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
	
	if (recordRs != null) {
		recordRs.close();
	}

	if (descriptFldValue != null && !descriptFldValue.trim().equals("null")) {
		description.append(descriptFldValue + " ");
	}
	System.out.println("description[" + description.toString() + "]");
	
	timestamp1 = new Timestamp(System.currentTimeMillis());
	System.out.println(" [AccountSummary],[extractDescriptionField] condition Has Been Completed on [" + timestamp1 + "]");
	// return description.toString();
}

private void loadApplFieldMappings(Connection dbConnection, String t24applName, HashMap<String, String> fldMap,
		String unitId) throws SQLException {

	System.out.println("*********loadApplFieldMappings***********");
	String cmFieldMappingTable = ActualTableName.get(unitId + "-T24$CM$FLD$MAPPING");
	try (PreparedStatement dbSt = dbConnection
			.prepareStatement("SELECT * FROM " + cmFieldMappingTable + " WHERE T24_APPL_NAME = ?")) {
		dbSt.setString(1, t24applName);
		try (ResultSet dbRs = dbSt.executeQuery()) {
			while (dbRs.next()) {
				fldMap.put(dbRs.getString("T24_FLD_NAME"), dbRs.getString("CM_FLD_NAME"));
			}
			dbRs.close();
		}
		dbSt.close();
	}

}

private void initialiseDescriptionFldMappings(Connection dbConnection, String unitId) throws Exception {

	System.out.println("*********initialiseDescriptionFldMappings***********");
	String paymentDetails = null;
	String remitMappingTable = ActualTableName.get(unitId + "-CBA$REMIT$FIELD$MAPPING");
	try (PreparedStatement mappingSt = dbConnection.prepareStatement("SELECT * FROM " + remitMappingTable);
			ResultSet mappingRs = mappingSt.executeQuery()) {
		while (mappingRs.next()) {
			paymentDetails = mappingRs.getString("PAYMENT_DETAILS");
			if (paymentDetails != null && !paymentDetails.trim().isEmpty()) {
				paymentDetailFld.put(mappingRs.getString("ID"), paymentDetails);
			}
		}
		mappingRs.close();
		mappingSt.close();
	}

	// [PAYMENT.DETAILS^ORDERING.CUST^TELEX.FROM.CUST^CREDIT.THEIR.REF^DEBIT.THEIR.REF^FX.REFERENCE^IN.BK.TO.BK
	// ]

	applFldMapgs = new HashMap<String, Map<String, String>>();
	HashMap<String, String> fldMap = new HashMap<String, String>();
	loadApplFieldMappings(dbConnection, ftAppl, fldMap, unitId);
	applFldMapgs.put(ftAppl, fldMap);

	fldMap = new HashMap<String, String>();
	loadApplFieldMappings(dbConnection, ttAppl, fldMap, unitId);
	applFldMapgs.put(ttAppl, fldMap);

	fldMap = new HashMap<String, String>();
	loadApplFieldMappings(dbConnection, acChgReqAppl, fldMap, unitId);
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
