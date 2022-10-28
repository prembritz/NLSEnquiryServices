package com.nls.Enquiry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MiniStatementMobi {

	private static HashMap<String, String> ActualTableName;

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		MiniStatementMobi.ActualTableName = ActualTableName;
	}
	
    public String[][] generateMiniStatement(Connection dbConnection, String accountNumber, int no_of_transactions,
            String unitId, String IdType) throws Exception {
    	
        String AccStmtPrintTableRef="ACCT$STMT$PRINT";
        String AccStatmentTableRef="ACCOUNT$STATEMENT";
        String StmtPrintedRef="STMT$PRINTED";
        String AcctStmtPrintedTable=ActualTableName.get(unitId+"-"+AccStmtPrintTableRef);
        String AccStatmentTable=ActualTableName.get(unitId+"-"+AccStatmentTableRef);
        String StmtPrintedTable=ActualTableName.get(unitId+"-"+StmtPrintedRef);

        boolean fixedSize = (no_of_transactions != 0);

        String[] idList = null;

        int no_of_trans_processed = 0;

        if (fixedSize) {
            idList = new String[no_of_transactions];
            no_of_trans_processed = no_of_transactions;
        }

        Object[] lastObject = getLastStatementBalance(dbConnection, accountNumber, 
        		AcctStmtPrintedTable,AccStatmentTable);

        String selectId = accountNumber + "-" + lastObject[0];

        System.out.println("generateMiniStatement STMT$PRINTED: [" + selectId + "] [" + unitId + "]");

        PreparedStatement currentStatement = dbConnection
                .prepareStatement("select ENTRY_LIST,ID from " + StmtPrintedTable + " where ID = ? ");
        currentStatement.setString(1, selectId);

        ResultSet currentEntries = currentStatement.executeQuery();

        StringWriter rationalizedString = null;
        String temp = "";
        String[] entryList = null;

        if (currentEntries.next()) {

            rationalizedString = new StringWriter();
            rationalizedString.write(currentEntries.getString("ENTRY_LIST").toCharArray());
            temp = rationalizedString.toString();

            entryList = temp.replaceAll(((char) 65533) + "", "^").split("\\^");

            int no_of_entries = entryList.length;

            if (!fixedSize) {
                idList = new String[no_of_entries];
                no_of_trans_processed = no_of_entries;
            }

            for (int i = no_of_entries - 1; i >= 0; i--) {

                if (entryList[i] == null || entryList[i].equals("")) {
                    continue;
                }

                System.out.println("[STMT$PRINTED] Current Statement : " + entryList[i]);

                idList[no_of_trans_processed - 1] = entryList[i];

                no_of_trans_processed--;

                if (no_of_trans_processed == 0) {
                    break;
                }
            }

        }

        if (no_of_trans_processed != 0) {
            fetchPreviousStatements(dbConnection, accountNumber, no_of_trans_processed, 
            		idList, AcctStmtPrintedTable,StmtPrintedTable);
        }

        currentEntries.close();
        currentStatement.close();

        double lastStatementBalance = Double.parseDouble(lastObject[1].toString());

        return generateStatement(dbConnection, idList, IdType, ActualTableName,unitId);

    }

    DecimalFormat decFormat = new DecimalFormat("###0.00");
    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");

    public String[][] generateStatement(Connection dbConnection, String[] idList,
    		String IdType,HashMap<String, String> ActualTableName,String UnitId) throws Exception {
        String[][] statementArray = new String[idList.length][7];

        String StmtEntryRef="STATEMENT$ENTRY";
        String StmtEntryTable=ActualTableName.get(UnitId+"-"+StmtEntryRef);
        System.out.println("StmtEntryTable ="+StmtEntryTable);
        PreparedStatement stmtStatement = dbConnection
                .prepareStatement("SELECT * FROM " + StmtEntryTable + " WHERE ID = ?");
        ResultSet stmtRecord = null;

        int no_of_entries = idList.length;

        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat timeParser = new SimpleDateFormat("yyMMddHHmm");

        String amount = null;

        String fcyAmount = null;

        for (int entryI = 0; entryI < no_of_entries; entryI++) {

            stmtStatement.setString(1, idList[entryI]);
            stmtRecord = stmtStatement.executeQuery();

            if (stmtRecord.next()) {
                System.out.println("statement$entry Generating Entry: " + idList[entryI]);

                try {

                    String transId = stmtRecord.getString("TRANS_REFERENCE");
                    if (IdType != null) {
                        if (!transId.startsWith(IdType)) {
                            no_of_entries--;
                            continue;
                        }
                    }

                    statementArray[entryI][0] = "" + dateFormat.format(stmtRecord.getDate("VALUE_DATE"));

                    statementArray[entryI][1] = stmtRecord.getString("TRANSACTION_CODE");

                    if (stmtRecord.getString("AMOUNT_FCY") != null) {
                        fcyAmount = stmtRecord.getString("AMOUNT_FCY");
                        if (fcyAmount != null && Double.parseDouble(fcyAmount) == 0.00) {
                            amount = stmtRecord.getString("AMOUNT_LCY");
                        } else {
                            amount = stmtRecord.getString("AMOUNT_FCY");
                        }
                    } else {
                        amount = stmtRecord.getString("AMOUNT_LCY");
                    }

//				amount = decFormat.format(Double.parseDouble(amount));	
//                System.out.println("statement$entry Generating Entry: [" + amount + "] [" + Math.abs(Double.parseDouble(amount)) + "]");
                    statementArray[entryI][2] = "" + Math.abs(Double.parseDouble(amount));
                    if (Double.parseDouble(amount) < 0) {
                        statementArray[entryI][3] = "Debit";
                    } else {
                        statementArray[entryI][3] = "Credit";
                    }

                    statementArray[entryI][4] = stmtRecord.getString("CURRENCY");
                    statementArray[entryI][5] = transId;

                    String stmtExtraDesc = "", chequeNumber = "", valueDate = "";

                    String stmtNarravie = getClobContent(stmtRecord, "NARRATIVE");

                    if (stmtNarravie != null) {

                        stmtExtraDesc = stmtExtraDesc + " " + stmtNarravie;

                    }

                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
                    int bookingDate = Integer.parseInt(dateFormat2.format(stmtRecord.getDate("BOOKING_DATE")));
                    if (transId.startsWith("FT")) {
                        String tableNameFT = "FUNDS$TRANSFER";
                        tableNameFT=ActualTableName.get(UnitId+"-"+tableNameFT);
                        if (!("" + bookingDate).equals(dateFormat.format(Calendar.getInstance().getTime()))) {
                            tableNameFT = "FUNDS$TRANSFER_HISTORY";
                            tableNameFT=ActualTableName.get(UnitId+"-"+tableNameFT);
                        }

                        System.out.println("Fetching FT Details [ " + transId + " ] [" + tableNameFT + "] [" + bookingDate + "] [" + dateFormat.format(Calendar.getInstance().getTime()) + "]");
                        PreparedStatement currentStatement = dbConnection
                                .prepareStatement("select * from " + tableNameFT + " where ID = ? or ID = ?");
                        currentStatement.setString(1, transId);
                        currentStatement.setString(2, transId + ";1");
                        ResultSet currentEntries = currentStatement.executeQuery();
                        if (currentEntries.next()) {
                            System.out.println(transId + ": FT Details [ " + currentEntries.getString("ID") + " ] IN_PAYMENT_DETAILS [ " + currentEntries.getString("IN_PAYMENT_DETAILS") + " ] ");
                            if (currentEntries.getString("CHEQUE_NUMBER") != null) {
                                chequeNumber = currentEntries.getString("CHEQUE_NUMBER");
                            }

                            if (currentEntries.getString("CREDIT_VALUE_DATE") != null) {
                                valueDate = dateFormat2.format(currentEntries.getDate("CREDIT_VALUE_DATE"));
                            }

                            if (currentEntries.getString("IN_PAYMENT_DETAILS") != null && !stmtExtraDesc
                                    .contains(currentEntries.getString("IN_PAYMENT_DETAILS"))) {
                                stmtExtraDesc = stmtExtraDesc + " "
                                        + currentEntries.getString("IN_PAYMENT_DETAILS");
                            }

                            if (currentEntries.getString("DEBIT_THEIR_REF") != null && !stmtExtraDesc
                                    .contains(currentEntries.getString("DEBIT_THEIR_REF"))) {
                                stmtExtraDesc = stmtExtraDesc + " "
                                        + currentEntries.getString("DEBIT_THEIR_REF");
                            }

                            if (currentEntries.getString("ORDERING_CUST") != null
                                    && !stmtExtraDesc.contains(currentEntries.getString("ORDERING_CUST"))) {
                                stmtExtraDesc = stmtExtraDesc + " "
                                        + currentEntries.getString("ORDERING_CUST");
                            }
                        }

                        currentStatement.close();
                        currentEntries.close();

                    } else if (transId.startsWith("TT")) {

                        String tableNameTT = "TELLER";
                        tableNameTT=ActualTableName.get(UnitId+"-"+tableNameTT);
                        if (!("" + bookingDate).equals(dateFormat.format(Calendar.getInstance().getTime()))) {
                            tableNameTT = "TELLER_HISTORY";
                            tableNameTT=ActualTableName.get(UnitId+"-"+tableNameTT);
                        }

                        System.out.println("Fetching TT Details [ " + transId + " ] ["+tableNameTT+"]");
                        PreparedStatement currentStatement = dbConnection
                                .prepareStatement("select * from " + tableNameTT + " where id = ? or id = ?");

                        currentStatement.setString(1, transId);
                        currentStatement.setString(2, transId + ";1");

                        ResultSet currentEntries = currentStatement.executeQuery();
                        if (currentEntries.next()) {
//                                        System.out.println(accountNumber + ": TT Details [ " + currentEntries.getString("ID") + " ] REMITTER_NAME [ " + currentEntries.getString("REMITTER_NAME") + " ] ");
                            if (currentEntries.getString("REMITTER_NAME") != null
                                    && !stmtExtraDesc.contains(currentEntries.getString("REMITTER_NAME"))) {
                                stmtExtraDesc = stmtExtraDesc + " "
                                        + currentEntries.getString("REMITTER_NAME");
                            }
                            if (currentEntries.getString("NARRATIVE_TWO") != null
                                    && !stmtExtraDesc.contains(currentEntries.getString("NARRATIVE_TWO"))) {
                                stmtExtraDesc = stmtExtraDesc + " "
                                        + currentEntries.getString("NARRATIVE_TWO");
                            }
                        }
                        currentStatement.close();
                        currentEntries.close();
                    }

                    statementArray[entryI][6] = stmtExtraDesc;

                    System.out.println("statement$entry Generation Complete: [" + idList[entryI] + "] [" + stmtRecord.getDate("VALUE_DATE") + "] [" + dateFormat.format(stmtRecord.getDate("VALUE_DATE")) + "] [" + stmtExtraDesc + "]");

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            stmtRecord.close();
            stmtStatement.clearParameters();

        }

        stmtStatement.close();

        return statementArray;
    }

    private static String getClobContent(ResultSet stmtRecord, String fieldName) throws IOException, SQLException {
        Clob clob = stmtRecord.getClob(fieldName);
        if (clob != null) {
            InputStream is = clob.getAsciiStream();
            ByteArrayOutputStream clobContent = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int read = 0;
            do {
                read = is.read(data);
                if (read == -1) {
                    break;
                } else {
                    clobContent.write(data, 0, read);
                }

            } while (true);
            return new String(clobContent.toByteArray());
        }

        return null;
    }

    public Object[] getLastStatementBalance(Connection dbConnection, String accountNumber,
    		String AcctStmtPrintedTable,String AccountStatementTable)
            throws Exception {

        System.out.println("getLastStatementBalance: [" + accountNumber + "] [" + AcctStmtPrintedTable + "]");
        double returnAmount = 0.00;
        String returnDate = null;
        Object[] returnObject = new Object[2];
        PreparedStatement acctStmtPrintStatement = dbConnection
                .prepareStatement("SELECT STMT_DATE_BAL FROM " + AcctStmtPrintedTable + " WHERE ID = ?");

        acctStmtPrintStatement.setString(1, accountNumber);

        ResultSet acctStmtmPrint = acctStmtPrintStatement.executeQuery();

        String tempString = "";

        if (acctStmtmPrint.next()) {
            tempString = acctStmtmPrint.getString("STMT_DATE_BAL");
            tempString = tempString.replaceAll(((char) 65533) + "", "^");

            System.out.println("getLastStatementBalance: Records Found: [" + tempString + "]");

            String[] acctStmtPrinted = tempString.split("\\^");

            ACCT_STMT_PRINT:
            for (int stmtI = acctStmtPrinted.length - 1; stmtI >= 0; stmtI--) {

                System.out.println("getLastStatementBalance: Records Iterate: [" + acctStmtPrinted[stmtI] + "]");
                String[] lastBal = acctStmtPrinted[stmtI].split("/");
                returnDate = acctStmtPrinted[stmtI].split("/")[0];
                if (lastBal.length == 2) {
                    returnAmount = Double.parseDouble(acctStmtPrinted[stmtI].split("/")[1]);
                    System.out.println("getLastStatementBalance: A: [" + accountNumber + "]");
                } else {
                    System.out.println("getLastStatementBalance: B: [" + accountNumber + "]");
                    Statement stmtgetref = dbConnection.createStatement();
                    ResultSet rsref = stmtgetref
                            .executeQuery("SELECT * FROM "+AccountStatementTable+" where ID ='" + accountNumber + "' ");
                    if (rsref.next()) {
                        returnAmount = Double.parseDouble(rsref.getString("LAST_BALANCE"));
                    }

                    stmtgetref.close();
                    rsref.close();
                }

                break ACCT_STMT_PRINT;
            }
        }

        System.out.println("getLastStatementBalance: C: [" + returnAmount + "] [" + returnDate + "]");

        returnObject[0] = returnDate;
        returnObject[1] = returnAmount;

        return returnObject;
    }

    public void fetchPreviousStatements(Connection dbConnection, String accountNumber, int no_of_trans_processed,
            String[] idList,String AcctStmtPrintedTable,String StmtPrintedTable) throws Exception {
        PreparedStatement acctStmtPrintStatement = dbConnection
                .prepareStatement("SELECT STMT_DATE_BAL FROM " + AcctStmtPrintedTable + " WHERE ID = ?");

        acctStmtPrintStatement.setString(1, accountNumber);

        ResultSet acctStmtmPrint = acctStmtPrintStatement.executeQuery();

        String[] acctStmtPrinted = null;

        PreparedStatement stmtPrintedStatement = dbConnection
                .prepareStatement("select ENTRY_LIST from " + StmtPrintedTable + " where ID = ?");

        ResultSet stmtPrinted = null;

        String[] entryList = null;

        StringWriter rationalizedString = new StringWriter();
        String temp = "";

        int no_of_stmt_entries = 0;

        String entryId = "";

        if (acctStmtmPrint.next()) {
            acctStmtPrinted = acctStmtmPrint.getString("STMT_DATE_BAL").split("\\^");

            ACCT_STMT_PRINT:
            for (int stmtI = acctStmtPrinted.length - 2; stmtI >= 0; stmtI--) {

                stmtPrintedStatement.setString(1, accountNumber + "-" + acctStmtPrinted[stmtI].split("/")[0]);

                System.out.println("ACCT_STMT_PRINT - PREVIOUS: " + acctStmtPrinted[stmtI]);

                stmtPrinted = stmtPrintedStatement.executeQuery();

                if (stmtPrinted.next() && stmtPrinted.getString("ENTRY_LIST") != null) {

                    rationalizedString = new StringWriter();
                    rationalizedString.write(stmtPrinted.getString("ENTRY_LIST").toCharArray());
                    temp = rationalizedString.toString();
                    if (temp.length() > 22) {
                        entryList = temp.replaceAll(((char) 65533) + "", "^").split("\\^");
                    } else {
                        entryList = temp.split("\\^");
                    }

                    no_of_stmt_entries = entryList.length;

                    STMT_PRINTED:
                    for (int stmtIdI = no_of_stmt_entries - 1; stmtIdI >= 0; stmtIdI--) {
                        entryId = entryList[stmtIdI];

                        if (entryId == null || entryId.equals("")) {
                            continue;
                        }

                        System.out.println("STMT$PRINTED - PREVIOUS: " + entryId);
                        idList[no_of_trans_processed - 1] = entryId;

                        no_of_trans_processed--;

                        if (no_of_trans_processed == 0) {
                            break ACCT_STMT_PRINT;
                        }
                    }
                }

                stmtPrinted.close();
                stmtPrintedStatement.clearParameters();

            }

        }

        stmtPrintedStatement.close();

        acctStmtmPrint.close();
        acctStmtPrintStatement.close();

    }

     /*public static void main(String[] args) throws Exception {
     DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
     Connection connection = DriverManager.getConnection(
    "jdbc:oracle:thin:@localhost:1521/orcl", "ncbadb",
     "ncba123");
     MiniStatementMobi statementInterface = new MiniStatementMobi();
     String[][] data = statementInterface.generateMiniStatement(connection,
     "7519350046", 10, "BNK_","");
     for (int i = 0; i < data.length; i++) {
     for (int j = 0; j < data[i].length; j++) {
     System.out.print(data[i][j] + "\t");
     }
     System.out.println();
     }
    
     }*/
}
