package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

public class AccountUtilities {
	
     private static HashMap<String, String> ActualTableName = null;
     public static String invalidFCDBChars = new String();
	
	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountUtilities.ActualTableName = ActualTableName;
	}

	public static ACCOUNT_TYPE getAccountType(String accountId, Connection dbConnection,String UnitId)
			throws Exception {
		ACCOUNT_TYPE acctType = ACCOUNT_TYPE.ACCOUNTS;
		String arrRef = getArrangementReference(dbConnection, accountId, UnitId);
		if (arrRef != null && !arrRef.trim().isEmpty()) {
			String arrangementTable = ActualTableName.get(UnitId+"-AA_ARRANGEMENT");
			try (PreparedStatement dbSt = dbConnection
					.prepareStatement("SELECT * FROM " + arrangementTable + " WHERE ID = ?")) {
				dbSt.setString(1, arrRef);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						String productLine = dbRs.getString("PRODUCT_LINE");
						try {
							acctType = ACCOUNT_TYPE.valueOf(productLine);							
						} catch (Exception except) {
							acctType = ACCOUNT_TYPE.UNKNOWN;
						}
					}
				}
			}
		}
		//System.out.println("acctType["+acctType+"]");
		return acctType;
	}
	
	public static String getArrangementReference(Connection dbConnection, String accountId,String UnitId)
			throws Exception {
		String arrangementReference = accountId;
		String accountTable = ActualTableName.get(UnitId+"-ACCOUNT");
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT ARRANGMENT_ID FROM " + accountTable + " WHERE ID = ?")) {
			dbSt.setString(1, accountId);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next()) {
					arrangementReference = dbRs.getString("ARRANGMENT_ID");
				} else {
					String accountHistoryTable = ActualTableName.get(UnitId+"-ACCOUNT_HISTORY");
					try (PreparedStatement histSt = dbConnection.prepareStatement(
							"SELECT ARRANGMENT_ID FROM " + accountHistoryTable + " WHERE ID = ?")) {
						histSt.setString(1, accountId + ";1");
						try (ResultSet histRs = histSt.executeQuery()) {
							if (histRs.next()) {
								arrangementReference = histRs.getString("ARRANGMENT_ID");
							}
						}
					}
				}
			}

		}
		//System.out.println("arrangementReference["+arrangementReference+"]");
		return arrangementReference;
	}
		
	public static String getStringField(Connection dbConnection, String tableName, String idField,
			String idValue, String fieldName) throws Exception {
		String fldValue = "";
		try (PreparedStatement dbSt = dbConnection.prepareStatement(
				"SELECT " + fieldName + " FROM " + tableName + " WHERE " + idField + " = ?")) {
			dbSt.setString(1, idValue);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next()) {
					fldValue = dbRs.getString(fieldName);
				}
			}

		}
		//System.out.println("fldValue["+fldValue+"]");
		return fldValue;
	}
	
	public static String rationalizeInvalidChars(String stringVal) {
		StringBuffer rationalizedString = new StringBuffer();
		if (stringVal != null) {
			char c;
			for (int i = 0; i < stringVal.length(); i++) {
				c = stringVal.charAt(i);
				if (invalidFCDBChars.indexOf(c) != -1) {
					c = ' ';
				}
				rationalizedString.append(c);
			}
			stringVal = rationalizedString.toString();
		}
		return stringVal;
	}

	public static void initialiseFCDBInvalidChars(Connection dbConnection, String schemaName,String unitId) throws SQLException {
		if (invalidFCDBChars.length() <= 0) {
			synchronized (invalidFCDBChars) {
				//String asciiValTable = Utilities.getTableName(dbConnection, schemaName, "ASCII$VAL$TABLE");
				String asciiValTable = ActualTableName.get(unitId+"-ASCII$VAL$TABLE");
				try (PreparedStatement dbSt = dbConnection.prepareStatement(
						"SELECT * FROM " + schemaName + "." + asciiValTable + " WHERE ID = 'FCDB.INVALID.CHARS'");
						ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						invalidFCDBChars = dbRs.getString("NOTES");
					}
				}
			}
		}
	}
	
	public static Vector<String> split(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		do {
			index = expression.indexOf(delimeter);
			if (index == -1) {
				break;
			} else {
				tempString = expression.substring(0, index);
				expression = expression.substring(index + 1, expression.length());
				tokens.add(tempString);
			}
		} while (true);
		tokens.add(expression);

		return tokens;
	}
}
