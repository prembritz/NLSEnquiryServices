package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

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

@Path("/LoanDetails")
public class LoanDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName;
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static String AcctTableRef = "ACCOUNT";
	private static String CategoryRef = "CATEGORY";
	private static String AAArrInterestRef = "AA$ARR$INTEREST";
	// private static String AAArrangementTableRef = "AA_ARRANGEMENT";
	private static String AABillDetailsRef = "AA$BILL$DETAILS";
	private static String AAAccountTableRef = "AA$ACCOUNT$DETAILS";
	private static String AAArrTermAmountRef = "AA$ARR$TERM$AMOUNT";
	private static String PaymentScheduleRef = "PAYMENT$SHEDULE";
	private static String AAArrSettlementRef = "AA$ARR$SETTLEMENT";
	private static String EBContractBalRef = "EB$CONTRACT$BALANCES";
	private static String CustomerRef = "CUSTOMER";

	enum SettlementTypes {
		UNPAID, SETTLED;
	}

	enum PropertyTypes {
		ACCOUNT, PRINCIPALINT, PENALTYINT;
	}

	public static void setDBPool(DataSource cmDBPool) {
		LoanDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		LoanDetails.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = LoanDetailsObject.class, responseDescription = "Loan Details Response", responseCode = "200")
	@Operation(summary = "Loan Details Data Request", description = "returns Loan Details data")
	public Response getLoanDetails(
			@RequestBody(description = "Loan Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDetailsRequest.class))) LoanDetailsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Loan Details Data Interface Started on ["+startTime+"]");
			String unitId = id.unId;
			String AccountNumber = id.loanAccnumber;
			
			System.out.println("Fetching Loan Details For [ " + AccountNumber + " ]");

			LoanDetailsObject Loan = new LoanDetailsObject();

			System.out.println("Loan Details Table Ref [" + AcctTableRef + "," + CategoryRef + "," + AAArrInterestRef
					+ "," + AAAccountTableRef + "," + AAArrSettlementRef + "," + PaymentScheduleRef + ","
					+ AABillDetailsRef + "," + AAArrTermAmountRef + "," + EBContractBalRef + "," + CustomerRef + "]");

			
			String AcctTable = ActualTableName.get(unitId + "-" + AcctTableRef);
			String CategoryTable = ActualTableName.get(unitId + "-" + CategoryRef);
			String AAArrInterestTable = ActualTableName.get(unitId + "-" + AAArrInterestRef);
			String AAAccountTable = ActualTableName.get(unitId + "-" + AAAccountTableRef);
			String AAArrSettlementTable = ActualTableName.get(unitId + "-" + AAArrSettlementRef);
			String PaymentScheduleTable = ActualTableName.get(unitId + "-" + PaymentScheduleRef);
			String AABillDetailsTable = ActualTableName.get(unitId + "-" + AABillDetailsRef);
			String AAArrTermAmountTable = ActualTableName.get(unitId + "-" + AAArrTermAmountRef);
			String EBContractBalTable = ActualTableName.get(unitId + "-" + EBContractBalRef);
			String CustomerTable = ActualTableName.get(unitId + "-" + CustomerRef);

			System.out.println("Loan Details Table Names [" + AcctTable + "," + CategoryTable + "," + AAArrInterestTable
					+ "," + AAAccountTable + "," + AAArrSettlementTable + "," + PaymentScheduleTable + ","
					+ AABillDetailsTable + "," + AAArrTermAmountTable + "," + EBContractBalTable + "," + CustomerTable
					+ "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection.prepareStatement(
							"select ID,CUSTOMER_ID," + " ACCOUNT_TITLE,CURRENCY,CATEGORY,ARRANGMENT_ID " + " from "
									+ AcctTable + " WHERE id = ? ")) {
				dbSt.setString(1, AccountNumber);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {

						Loan.setUnId(unitId);
						Loan.setLoanAccnumber(dbRs.getString("ID"));
						Loan.setLoanCur(dbRs.getString("CURRENCY"));
						Loan.setLnType(dbRs.getString("CATEGORY"));
						Loan.setLoanTypedesc(DescriptionPick(dbConnection, dbRs.getString("CATEGORY"), CategoryTable));
						Loan.setFundingAcc(
								PayingAccount(dbConnection, dbRs.getString("ARRANGMENT_ID"), AAArrSettlementTable));
						String paymentFrequency[] = getPaymentFrequency(PaymentScheduleTable,
								dbRs.getString("ARRANGMENT_ID"), dbConnection);
						Loan.setPaymentFreq(paymentFrequency[0]);
						String TermDetails[] = TermAmount(dbConnection, dbRs.getString("ARRANGMENT_ID"),
								AAArrTermAmountTable);
						Loan.setOriginalAmt(Double.parseDouble(TermDetails[0]));
						Object[] loanBalanceCalc = loanBalanceCalc(dbConnection, dbRs.getString("ID"), unitId,
								AcctTable, EBContractBalTable, AAArrTermAmountTable,CategoryTable);
						Loan.setOutstandingLoanamt(Double.parseDouble(loanBalanceCalc[2].toString()));
						String Amounts[] = BillDetails(dbConnection, dbRs.getString("ARRANGMENT_ID"),
								AABillDetailsTable);
						Loan.setPastDueamt(Double.parseDouble(Amounts[0]));
						Loan.setLastPaymentamt(Double.parseDouble(Amounts[1]));
						Loan.setEmi(Double.parseDouble(paymentFrequency[2].equals("")?"0.0":paymentFrequency[2]));
						Loan.setEmiPaid(InstallmentPaid(dbConnection, dbRs.getString("ARRANGMENT_ID"),
								AABillDetailsTable));

						//String Dates[] = AccountDetails(dbConnection, dbRs.getString("ARRANGMENT_ID"), AAAccountTable);
						Loan.setContractDate(paymentFrequency[1]);
						Loan.setMaturityDate(DateFormtter.format(TimeFormat.parse("" + TermDetails[1])));
						Loan.setIntrstRate(
								ArrangementInterest(dbConnection, dbRs.getString("ARRANGMENT_ID"), AAArrInterestTable));
						//Loan.setNextPaydate(Dates[2]);
						Loan.setNextPaydate(NextPaymentDate(dbConnection,dbRs.getString("ARRANGMENT_ID"),
								AAAccountTable,dbRs.getString("ID"),TermDetails[1]));

						Loan.setNextPayamt(0.0);
						Loan.setPayOffamt(0.0);
						Loan.setPrincipalClsgamt(0.0);
						Loan.setDisbursementAcc(dbRs.getString("ACCOUNT_TITLE"));
						Loan.setIntRepaymentamt(Double.parseDouble(Amounts[3]));
						Loan.setIntPaydate(Amounts[4]);
						Loan.setPrincipalPaydate(Amounts[4]);
						Loan.setPrincipalPayamt(Double.parseDouble(Amounts[2]));
						Loan.setLnHoldername(CustomerName(dbConnection, dbRs.getString("CUSTOMER_ID"), CustomerTable));
						System.out.println("Contract Date ["+paymentFrequency[1]+"] Maturity Date ["+TermDetails[1]+"]");
						if (paymentFrequency[1] != null && TermDetails[1] != null) 
							Loan.setLnTenor(ChronoUnit.DAYS.between(LocalDate.parse(paymentFrequency[1], 
									DateTimeFormatter.ofPattern("ddMMyyyy")), LocalDate.parse(TermDetails[1], 
											DateTimeFormatter.ofPattern("yyyy-MM-dd")))+"D");
						//Loan.setLnTenor(String.valueOf(CountDays(paymentFrequency[1], TermDetails[1]))+"-D");
						else
							Loan.setLnTenor("0D");	

					} else {
						ResponseMessages(Loan,unitId,AccountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.LOAN_ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(Loan).build();
					}
					dbRs.close();
				}	
				catch (Exception e) {
					e.printStackTrace();
					ResponseMessages(Loan,unitId,AccountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(Loan).build();
				}
				dbSt.close();
			} catch (Exception e) {
				e.printStackTrace();
				ResponseMessages(Loan,unitId,AccountNumber,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(Loan).build();
	
			}
			return Response.status(Status.ACCEPTED).entity(Loan).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Loan Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	/*private long CountDays(String ContractDate, String MaturityDate) {
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		long diffDays = 0;
		try {
			Date d1 = null;
			Date d2 = null;

			d1 = format.parse(ContractDate);
			d2 = format1.parse(MaturityDate);

			// in milliseconds
			long diff = d2.getTime()-d1.getTime();

			diffDays = diff / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diffDays;
	}*/
	
	public static String[] getPaymentFrequency(String PaymentScheduleTable, String arrangementReference, Connection dbConnection)
			throws Exception {

		/*
		 * calculate the installments due in future
		 * 
		 */
		String freq = "";
		String baseDate="";
		String EMI="";
		String Frequency[]=new String[3];

		try (PreparedStatement paymentFreqSt = dbConnection.prepareStatement("SELECT PAYMENT_FREQ,BASE_DATE,"
				+ " DUE_FREQ,case when REGEXP_SUBSTR(to_char(actual_amt),'[^^]+') is not "
				+ " null then REGEXP_SUBSTR(to_char(actual_amt),'[^^]+') else "
				+ " REGEXP_SUBSTR(to_char(calc_amount),'[^^]+') end as EMI FROM " +PaymentScheduleTable+ " "
				+ " WHERE ARRANGEMENT_ID = ? and rownum<=1 ORDER BY ID DESC ")) {
			paymentFreqSt.setString(1, arrangementReference);
			try (ResultSet paymentFreqRs = paymentFreqSt.executeQuery()) {
				if (paymentFreqRs.next()) {
					freq = paymentFreqRs.getString("PAYMENT_FREQ");
					if (freq != null && freq.indexOf("^") != -1) {
						freq = freq.split("\\^")[0];
					}
					baseDate=DateFormtter.format(TimeFormat2.parse("" + paymentFreqRs.getString("BASE_DATE")));
					EMI=paymentFreqRs.getString("EMI")==null?"":paymentFreqRs.getString("EMI");
					if (freq == null || freq.trim().isEmpty()) {
						freq = paymentFreqRs.getString("DUE_FREQ");
					}
				}
			}
		}

		int frequency = 0;
		String unit = "";
		String freqLength = "";
		String scheduledDay = "";
		if (freq != null && !freq.isEmpty()) {
			FREQ_LOOP: for (String freqElem : freq.split(" ")) {
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("e") && (frequency == 0)) {
					freqLength = freqElem.substring(1, freqElem.length() - 1);
					try {
						frequency = Integer.parseInt(freqLength);
					} catch (Exception except) {
						frequency = 1;
					}
					unit = "" + freqElem.charAt(freqElem.length() - 1);
					if (frequency == 0) {
						unit = "";
					}
				}
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("o")) {
					scheduledDay = freqElem.substring(1, freqElem.length() - 1);
					if (scheduledDay.equals("0")) {
						scheduledDay = "";
					}
				}
			}
		}

		/*
		 * roll the frequency till maturity date
		 */
		ChronoUnit cycleUnit = null;
		String frequencyString = "";
		if (unit.length() > 0) {
			switch (unit.charAt(0)) {
			case 'W':
				if (frequency > 1) {
					frequencyString = "Every " + frequency + " Weeks";
				} else {
					frequencyString = "Weekly";
				}
				break;
			case 'M':
				if (frequency > 1) {
					frequencyString = "Every " + frequency + " Months";
				} else {
					frequencyString = "Monthly";
				}

				break;
			case 'D':
				frequencyString = "Daily";
				break;
			case 'Y':
				if (frequency > 1) {
					frequencyString = "Every " + frequency + " Years";
				} else {
					frequencyString = "Yearly";
				}
				break;
			}
		}
		switch (scheduledDay) {
		case "L":
			frequencyString += " On Last Day";
			break;
		}
		try {

			int day = Integer.parseInt(scheduledDay);
			frequencyString += " On Day " + day;
		} catch (Exception except) {

		}
		System.out.println("Frequency ["+frequencyString+"], Contract Date["+baseDate+"],EMI["+EMI+"]");
		Frequency[0]=frequencyString;
		Frequency[1]=baseDate;
		Frequency[2]=EMI;
		
		return Frequency;

	}
	
	
	/*public static LocalDate getContractDate(String PaymentScheduleTable, String arrangementReference, Connection dbConnection)
			throws SQLException {
		String contractDate = null;
		String contract = null;
		String psTable = "PAYMENT$SHEDULE";
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT * FROM " + schema + "." + psTable + " WHERE ARRANGEMENT_ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				if (dbRs.next() && dbRs.getString("BASE_DATE") != null) {
					contract = dbRs.getString("BASE_DATE");
					contractDate=DateFormtter.format(TimeFormat2.parse("" + contract));
				}
			}
		}
		return contractDate;
	}*/
	
	/*public static String getAAMaturityDate(String AAArrTermAmountTable, String arrangementReference, Connection dbConnection)
			throws SQLException, ParseException {
		String matDate = null;
		//String termAmountTable = "AA$ARR$TERM$AMOUNT";
		try (PreparedStatement dbSt = dbConnection
				.prepareStatement("SELECT MATURITY_DATE FROM " + AAArrTermAmountTable + " WHERE ARRANGEMENT_ID = ?")) {
			dbSt.setString(1, arrangementReference);
			try (ResultSet dbRs = dbSt.executeQuery()) {
				while (dbRs.next() && dbRs.getDate("MATURITY_DATE") != null) {
					matDate = DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("MATURITY_DATE")));
				}
			}
		}
		return matDate;
	}*/

	private String DescriptionPick(Connection dbConnection, String Category, String CategoryTable) throws SQLException

	{
		String Description = "";

		PreparedStatement CategoryPS = dbConnection
				.prepareStatement("select DESCRIPTION from " + CategoryTable + " where id = ? ");
		CategoryPS.setString(1, Category);
		ResultSet CategoryRS = CategoryPS.executeQuery();
		if (CategoryRS.next()) {
			Description = CategoryRS.getString(1);
		}
		CategoryPS.close();
		CategoryRS.close();

		return Description;
	}

	private String PayingAccount(Connection dbConnection, String Arrangment_id, String AAArrSettlementTable)
			throws SQLException

	{
		String PayingAccount = "";
		String query = "select PAYING_ACCOUNT from " + AAArrSettlementTable + " where id like '" + Arrangment_id + "%'"
				+ " and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc ";
		PreparedStatement PayAccountPs = dbConnection.prepareStatement(query);
		ResultSet PayAccountRs = PayAccountPs.executeQuery();
		if (PayAccountRs.next()) {
			PayingAccount = PayAccountRs.getString(1);
		}
		PayAccountPs.close();
		PayAccountRs.close();

		return PayingAccount;
	}

	private String[] BillDetails(Connection dbConnection, String Arrangement_id, String AABillDetailsTable)
			throws SQLException, ParseException {

		String BillAmts[] = new String[5];

		Double PastDueAmt = 0.0;
		Double OutstandingAmt = 0.0;
		Double PrincipalAmt = 0.0;
		Double InterestAmt = 0.0;
		String PaymentDate="";

		PreparedStatement ArrDetailsPS = dbConnection.prepareStatement("select payment_date,"
				+ " os_prop_amount,property,settle_status,set_status_change_date, "
				+ " bill_status,bill_status_change_date,or_total_amount,os_total_amount from " + " "
				+ AABillDetailsTable + " where arrangement_id = ?  and rownum<=1 order by actual_pay_date desc ");

		ArrDetailsPS.setString(1, Arrangement_id);
		ResultSet ArrDetailsRS = ArrDetailsPS.executeQuery();

		Vector<String> SettleStatus = null;
		Vector<String> SettleStatusDate = null;
		Vector<String> OSTotalAmt = null;
		Vector<String> ORTotalAmt = null;
		Vector<String> Property = null;
		Vector<String> PropertyAmt = null;

		if (ArrDetailsRS.next()) {

			SettleStatus = new Vector<String>();
			SettleStatusDate = new Vector<String>();
			OSTotalAmt = new Vector<String>();
			ORTotalAmt = new Vector<String>();
			Property = new Vector<String>();
			PropertyAmt = new Vector<String>();

			SettleStatus = nlsSplit(ArrDetailsRS.getString("settle_status"), "^");
			SettleStatusDate = nlsSplit(ArrDetailsRS.getString("set_status_change_date"), "^");
			OSTotalAmt = nlsSplit(ArrDetailsRS.getString("os_total_amount"), "^");
			ORTotalAmt = nlsSplit(ArrDetailsRS.getString("or_total_amount"), "^");

			Property = nlsSplit(ArrDetailsRS.getString("property"), "^");
			PropertyAmt = nlsSplit(ArrDetailsRS.getString("os_prop_amount"), "^");

			String Settlestatus = SettleStatus.get(SettleStatusDate.indexOf(Collections.max(SettleStatusDate)));

			String OSTotAmt = OSTotalAmt.get(SettleStatusDate.indexOf(Collections.max(SettleStatusDate)));

			String ORTotAmt = ORTotalAmt.get(SettleStatusDate.indexOf(Collections.max(SettleStatusDate)));

			if (!Settlestatus.equalsIgnoreCase(SettlementTypes.SETTLED.toString())) {
				PastDueAmt = OSTotAmt == null ? 0.0 : Double.parseDouble(OSTotAmt);
			} else if (Settlestatus.equalsIgnoreCase(SettlementTypes.SETTLED.toString())) {
				OutstandingAmt = ORTotAmt == null ? 0.0 : Double.parseDouble(ORTotAmt);
			}

			for (int i = 0; i < Property.size(); i++) {
				if (Property.get(i).equalsIgnoreCase(PropertyTypes.ACCOUNT.toString())) {
					PrincipalAmt += Double.parseDouble(PropertyAmt.get(i) == null ? "0.0" : PropertyAmt.get(i));
				} else if (Property.get(i).equalsIgnoreCase(PropertyTypes.PRINCIPALINT.toString())
						|| Property.get(i).equalsIgnoreCase(PropertyTypes.PENALTYINT.toString())) {
					InterestAmt += Double.parseDouble(PropertyAmt.get(i) == null ? "0.0" : PropertyAmt.get(i));
				}
			}
			PaymentDate=DateFormtter.format(TimeFormat2.parse("" + ArrDetailsRS.getString("payment_date")));
		}
		ArrDetailsRS.close();
		ArrDetailsPS.close();
		
		BillAmts[0] = String.valueOf(PastDueAmt);
		BillAmts[1] = String.valueOf(OutstandingAmt);
		BillAmts[2] = String.valueOf(PrincipalAmt);
		BillAmts[3] = String.valueOf(InterestAmt);
		BillAmts[4] = PaymentDate;
		
		System.out.println("PastDueAmt [" + BillAmts[0] + "]");
		System.out.println("LastPaymentAmt [" + BillAmts[1] + "]");
		System.out.println("PrincipalAmount [" + BillAmts[2] + "]");
		System.out.println("InterestAmount [" + BillAmts[3] + "]");
		System.out.println("PaymentDate [" + BillAmts[4] + "]");

		return BillAmts;
	}

	public Vector<String> nlsSplit(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		while ((index = expression.indexOf(delimeter)) != -1) {
			tempString = expression.substring(0, index);
			expression = expression.substring(index + 1, expression.length());
			tokens.add(tempString);
		}
		tokens.add(expression);
		return tokens;
	}

	private String CustomerName(Connection dbConnection, String CustomerID, String CustomerTable) throws SQLException

	{
		String CustomerId = "";
		PreparedStatement CustomerPs = dbConnection
				.prepareStatement("select short_name from " + CustomerTable + " where id=? ");
		CustomerPs.setString(1, CustomerID);
		ResultSet CustomerRs = CustomerPs.executeQuery();
		if (CustomerRs.next()) {
			CustomerId = CustomerRs.getString(1);
		}
		CustomerPs.close();
		CustomerRs.close();

		return CustomerId;
	}

	private String[] TermAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {
		
		String TermDetails[] = new String[2];
		
		String Amount="";
		String MaturityDate="";
		
		try {
			PreparedStatement ArrTermPS = dbconnection
					.prepareStatement("select amount,maturity_date from " + AAArrTermAmountTable + " "
							+ " where arrangement_id=? and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ArrTermPS.setString(1, ArrangementID);
			ResultSet ArrTermRS = ArrTermPS.executeQuery();
			if (ArrTermRS.next()) {
				Amount = ArrTermRS.getString(1);
				MaturityDate = TimeFormat.format(TimeFormat.parse("" + ArrTermRS.getString(2)));
				//TermDetails[1] = ArrTermRS.getString(2);
			}
			ArrTermRS.close();
			ArrTermPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TermDetails[0] = Amount;
		TermDetails[1] = MaturityDate;
		
		 System.out.println("maturityDate = "+TermDetails[1]);

		return TermDetails;
	}

	public static Object[] loanBalanceCalc(Connection dbConnection, String AccountId, String unitId,
			String AcctTableName, String EBCTable, String AAArrTermAmountTable,String CategoryTable) {

		double loanSum = 0;
		Object[] returnObject = null;

		try {
			PreparedStatement loanDetSt = dbConnection
					.prepareStatement("select a.ID,a.ARRANGMENT_ID," + " b.DESCRIPTION from " + AcctTableName + " a, "
							+ " "+CategoryTable+" b where a.id = '" + AccountId + "' and a.CATEGORY = b.ID ");
			ResultSet loanDetRs = loanDetSt.executeQuery();

			if (loanDetRs.next()) {
				returnObject = new Object[4];

				String arrangement = loanDetRs.getString("ARRANGMENT_ID");
				String account = loanDetRs.getString("ID");
				String Description = loanDetRs.getString("DESCRIPTION");

//	                System.out.println("Loan Processing: [" + arrangement + "] [" + account + "] [" + Description + "]");

				/*
				 * calculate contract balances
				 */
				Vector<String> Balance = new Vector<String>();
				Vector<String> type = new Vector<String>();
				double principle = 0.0D;

				Double Total_Outstanding;
				Double TotalPriIntSus;
				Double TotalPenaltyIntSus;
				Double TotalPriInt;
				Double TotalPenaltyInt;

				PreparedStatement ps_Principle = dbConnection.prepareStatement("SELECT ID,TYPE_SYSDATE, "
						+ " CURR_ASSET_TYPE,DEBIT_MVMT,CREDIT_MVMT,OPEN_BALANCE " + "" + " FROM " + EBCTable
						+ " where OPEN_BALANCE " + " is not null and CURR_ASSET_TYPE is not null and id= ?");
				ps_Principle.setString(1, account);
				ResultSet rs_principle = ps_Principle.executeQuery();
				double Other_Balance[] = new double[7];
				if (rs_principle.next()) {

//	                    System.out.println("Loan Processing 1: [" + rs_principle.getString("OPEN_BALANCE") + "]");
					double pdamnt = 0.0D;
					Vector<String> opbalance = split(rs_principle.getString("OPEN_BALANCE"), "^");
					Vector<String> debitmvt = split(rs_principle.getString("DEBIT_MVMT"), "^");
					Vector<String> creditmvt = split(rs_principle.getString("CREDIT_MVMT"), "^");
					Balance = split(rs_principle.getString("OPEN_BALANCE"), "^");
					type = split(rs_principle.getString("TYPE_SYSDATE"), "^");

					principle = getBalance(Balance, type, debitmvt, creditmvt);
					Other_Balance = otherBalance(Balance, type);
					pdamnt = Other_Balance[0];
					if (pdamnt == 0.00) {
						PreparedStatement ps_aarrtermamount = dbConnection.prepareStatement("SELECT amount FROM "
								+ AAArrTermAmountTable + " where id like ? " + " order by ID FETCH NEXT 1 ROWS ONLY");
						ps_aarrtermamount.setString(1, arrangement + "%");
						ResultSet rs_aarrtermamount = ps_aarrtermamount.executeQuery();
						if (rs_aarrtermamount.next()) {
							pdamnt = Double.parseDouble(rs_aarrtermamount.getString("amount") == null ? "0.00"
									: rs_aarrtermamount.getString("amount"));
						}
						rs_aarrtermamount.close();
						ps_aarrtermamount.close();
					}

					Total_Outstanding = principle;
					double principalInterestDueSuspen = 0.00;
					double principalInterestSuspen = 0.00;
					double PenaltypalInterestDueSuspen = 0.00;
					double PenaltyInterestSuspen = 0.00;
					double PenaltyInterest = 0.00;
					double PenaltypalInterestDue = 0.00;
					double principalInterest = 0.00;
					double principalInterestDue = 0.00;

					if (opbalance.size() == type.size()) {
						for (int i = 0; i < opbalance.size(); i++) {
							if (!type.get(i).contains("SP")) {
								{
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")
											|| type.get(i).startsWith("ACCPE")
											|| type.get(i).substring(0, 3).equals("DUE")) {

									}
								}
								if (type.get(i).contains("PRINCIPAL")) {
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")) {
										String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										principalInterestDue += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
												+ Double.parseDouble(amtcrt1);
									}
									if (type.get(i).startsWith("ACCPRIN")) {
										String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										principalInterest += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
												+ Double.parseDouble(amtcrt2);

									}

								} else if (type.get(i).contains("PENALTY")) {
									if (type.get(i).substring(0, 2).equals("LS")
											|| type.get(i).substring(0, 2).equals("SS")
											|| type.get(i).substring(0, 2).equals("DA")
											|| type.get(i).substring(0, 3).equals("ESP")
											|| type.get(i).substring(0, 2).equals("WT")
											|| type.get(i).substring(0, 3).equals("NOR")) {
										String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										PenaltypalInterestDue += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
												+ Double.parseDouble(amtcrt1);
									}
									if (type.get(i).startsWith("ACCPENALTY")) {
										String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
										String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
										String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
										PenaltyInterest += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
												+ Double.parseDouble(amtcrt2);
									}
								}
							} else if (type.get(i).contains("PRINCIPAL")) {
								if (type.get(i).substring(0, 2).equals("LS") || type.get(i).substring(0, 2).equals("SS")
										|| type.get(i).substring(0, 2).equals("DA")
										|| type.get(i).substring(0, 3).equals("ESP")
										|| type.get(i).substring(0, 2).equals("WT")
										|| type.get(i).substring(0, 3).equals("NOR")) {
									String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									principalInterestDueSuspen += Double.parseDouble(amt1) + Double.parseDouble(amtmvt1)
											+ Double.parseDouble(amtcrt1);
								}
								if (type.get(i).startsWith("ACCPRIN")) {
									String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									principalInterestSuspen += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
											+ Double.parseDouble(amtcrt2);
								}
							} else if (type.get(i).contains("PENALTY")) {
								if (type.get(i).substring(0, 2).equals("LS") || type.get(i).substring(0, 2).equals("SS")
										|| type.get(i).substring(0, 2).equals("DA")
										|| type.get(i).substring(0, 3).equals("ESP")
										|| type.get(i).substring(0, 2).equals("WT")
										|| type.get(i).substring(0, 3).equals("NOR")) {
									String amt1 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt1 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt1 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									PenaltypalInterestDueSuspen += Double.parseDouble(amt1)
											+ Double.parseDouble(amtmvt1) + Double.parseDouble(amtcrt1);
								}

								if (type.get(i).startsWith("ACCPENALTY")) {
									String amt2 = opbalance.get(i).equals("") ? "0.00" : opbalance.get(i);
									String amtmvt2 = debitmvt.get(i).equals("") ? "0.00" : debitmvt.get(i);
									String amtcrt2 = creditmvt.get(i).equals("") ? "0.00" : creditmvt.get(i);
									PenaltyInterestSuspen += Double.parseDouble(amt2) + Double.parseDouble(amtmvt2)
											+ Double.parseDouble(amtcrt2);
								}
							}
//	                            System.out.println("Loan Processing 2: [" + type.get(i) + "]");
						}

					}
					TotalPriIntSus = principalInterestSuspen + principalInterestDueSuspen;
					TotalPenaltyIntSus = PenaltypalInterestDueSuspen + PenaltyInterestSuspen;
					TotalPriInt = principalInterest + principalInterestDue;
					TotalPenaltyInt = PenaltyInterest + PenaltypalInterestDue;

					loanSum = Math.abs(Total_Outstanding) + Math.abs(TotalPriInt) + Math.abs(TotalPenaltyInt)
							+ Math.abs(TotalPenaltyInt) + Math.abs(TotalPenaltyIntSus) + Math.abs(TotalPriIntSus);

//	                    System.out.println("[ACCOUNT]  [ " + account + "] [ARRANGEMENT]  [ " + arrangement + "]  [BALANCE] [" + amtFmt.format(loanSum) + "]");

				}
				rs_principle.close();
				ps_Principle.close();

				returnObject[0] = account;
				returnObject[1] = arrangement;
				// returnObject[2] = amtFmt.format(loanSum);
				returnObject[2] = loanSum;
				returnObject[3] = Description;

//	                System.out.println("Loan Processing 3: [" + amtFmt.format(loanSum) + "]");

			}

			loanDetRs.close();
			loanDetSt.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return returnObject;
	}

	public static double getBalance(Vector<String> Balance, Vector<String> TypeofAccount, Vector<String> debitmvt,
			Vector<String> creditmvt) {
		String type[] = { "CURACCOUNT", "DUEACCOUNT", "NORACCOUNT", "WT1ACCOUNT", "WT3ACCOUNT", "SS2ACCOUNT",
				"DA1ACCOUNT", "DA2ACCOUNT", "LS1ACCOUNT", "SS4ACCOUNT", "SS1ACCOUNT", "SS3ACCOUNT" };
		double bal = 0.0D;
		int size = 0;
		if (TypeofAccount.size() < Balance.size()) {
			size = TypeofAccount.size();
		} else {
			size = Balance.size();
		}

		for (int i = 0; i < size; i++) {
			String[] myType = TypeofAccount.get(i).toString().split("\\-");

			if (Arrays.asList(type).contains(myType[0])) {

				bal += Double.parseDouble(Balance.get(i).toString().equals("") ? "0.0" : Balance.get(i).toString())
						+ Double.parseDouble(debitmvt.get(i).toString().equals("") ? "0.0" : debitmvt.get(i).toString())
						+ Double.parseDouble(
								creditmvt.get(i).toString().equals("") ? "0.0" : creditmvt.get(i).toString());
			}
		}

		return bal;
	}

	public static double[] otherBalance(Vector<String> Balance, Vector<String> TypeofAccount) {
		String type[] = { "TOTCOMMITMENT", "CURCOMMITMENT", "CURACCOUNT", "ACCPRINCIPALINT", "ACCPENALTYINT",
				"ACCPENALTYINT-", "ACCPRINCIPALINT-" };
		double bal[] = new double[7];
		int size = 0;
		if (TypeofAccount.size() < Balance.size()) {
			size = TypeofAccount.size();
		} else {
			size = Balance.size();
		}

		for (int i = 0; i < size; i++) {
			if (Arrays.asList(type).contains(TypeofAccount.get(i).toString())) {
				bal[Arrays.asList(type).indexOf(TypeofAccount.get(i))] = Double
						.parseDouble(Balance.get(i).toString().equals("") ? "0.0" : Balance.get(i).toString());
			}
		}

		return bal;
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

	private Double ArrangementInterest(Connection dbconnection, String ArrangementID, String AAArrInterestTable) {
		Double EffectiveRate = 0.0;

		try {
			PreparedStatement ArrInterestPS = dbconnection
					.prepareStatement("select REGEXP_SUBSTR(EFFECTIVE_RATE, '[^^]+$') as EFFECTIVE_RATE"
							+ " from "+AAArrInterestTable+" where arrangement_id = ? "
									+ " and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc ");
			ArrInterestPS.setString(1, ArrangementID);
			ResultSet ArrInterestRS = ArrInterestPS.executeQuery();
			if (ArrInterestRS.next()) {
				EffectiveRate = Double.parseDouble(ArrInterestRS.getString("EFFECTIVE_RATE"));
			}
			ArrInterestRS.close();
			ArrInterestPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Effective Rate [ " + EffectiveRate + " ]");

		return EffectiveRate;
	}
	
	private int InstallmentPaid(Connection dbconnection, String ArrangementID, String AABillDetailsTable) {
		int InstallmentPaid=0;

		try {
			PreparedStatement ArrInterestPS = dbconnection
					.prepareStatement("select count(ID) as INSTALLMENT "
							+ " from "+AABillDetailsTable+" where arrangement_id=  ? "
									+ " and REGEXP_SUBSTR(to_char(BILL_STATUS),'[^^]+',1,1) ='SETTLED' ");
			ArrInterestPS.setString(1, ArrangementID);
			ResultSet ArrInterestRS = ArrInterestPS.executeQuery();
			if (ArrInterestRS.next()) {
				InstallmentPaid = ArrInterestRS.getInt("INSTALLMENT");
			}
			ArrInterestRS.close();
			ArrInterestPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Installment Paid [ " + InstallmentPaid + " ]");

		return InstallmentPaid;
	}
	
	
	private String NextPaymentDate(Connection dbConnection, String Arrangment_id,
			String AAAccountTable,String AccountNumber,String MaturityDate)
			throws SQLException, ParseException

	{
		String NextPayDate="";
		try(PreparedStatement AccountDetailsPS = dbConnection.prepareStatement("select payment_start_date"
				+ " from " + AAAccountTable + " where id = ? and rownum<=1"))
		{
		AccountDetailsPS.setString(1, Arrangment_id);
		ResultSet AccountDetailsRS = AccountDetailsPS.executeQuery();
		if (AccountDetailsRS.next()) {
			
				if ((AccountDetailsRS.getString("payment_start_date") != null
						&& !AccountDetailsRS.getString("payment_start_date").equals(""))) {

					Date paydate_next = TimeFormat2.parse(AccountDetailsRS.getString("payment_start_date"));
					if (!paydate_next.after(new Date())) {
						List<LocalDate> instalDates = getInstalmentDates(dbConnection,
								Arrangment_id,AccountNumber == null ? ""
										: Arrangment_id,
								null, AccountDetailsRS.getString("payment_start_date").replace("-", ""),MaturityDate);

						LocalDate currentDate = LocalDate.now();
						int startDate = 0;
						SCHED_DATES: for (int instI = 0; instI < instalDates.size(); instI++) {
							if (currentDate.isBefore(instalDates.get(instI))) {
								startDate = instI - 1;
								break SCHED_DATES;
							}
						}

						LocalDate startDt = null;
						LocalDate endDt = null;

						for (; startDate + 1 < instalDates.size(); startDate++) {
							startDt = instalDates.get(startDate);
							endDt = instalDates.get(startDate + 1);
							if (startDate == 1 && startDt.isEqual(currentDate)) {
								NextPayDate=endDt.toString();
							} else if (startDate == 0) {
								NextPayDate=endDt.toString();
							}
						}
					} 
				} 
			
		}
		AccountDetailsPS.close();
		AccountDetailsRS.close();
		
		} catch (Exception e) {
	
		}
		
        System.out.println("Next Pay Date ["+NextPayDate+"]");
        
		return (NextPayDate!=null && !NextPayDate.isEmpty()?DateFormtter.format(TimeFormat.parse(NextPayDate)):"");
	}
	
	public static List<LocalDate> getInstalmentDates(Connection externalDBConnection, String arrangementReference,
			String accountId, LocalDate filterDate, String paybaseDate,String MaturityDate) throws Exception {
		Vector<LocalDate> instalmentDets = new Vector<LocalDate>();

		/*
		 * calculate the installments due in future
		 * 
		 */
		String freq = null;
		LocalDate baseDate = null;

		try (PreparedStatement paymentFreqSt = externalDBConnection
				.prepareStatement(" SELECT PAYMENT_FREQ,DUE_FREQ FROM PAYMENT$SHEDULE WHERE"
						+ " ARRANGEMENT_ID = ? AND ROWNUM<=1 ORDER BY ID DESC ")) {
			paymentFreqSt.setString(1, arrangementReference);
			try (ResultSet paymentFreqRs = paymentFreqSt.executeQuery()) {
				if (paymentFreqRs.next()) {
					freq = paymentFreqRs.getString("PAYMENT_FREQ");
					baseDate = LocalDate.parse(paybaseDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
					if (freq == null || freq.trim().isEmpty()) {
						freq = paymentFreqRs.getString("DUE_FREQ");
					}
				} else {
					return instalmentDets;
				}
			}
		}

		int frequency = 0;
		String unit = "";
		String freqLength = "";
		String scheduledDay = "";
		if (freq != null && !freq.trim().isEmpty()) {
			FREQ_LOOP: for (String freqElem : freq.split(" ")) {
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("e") && (frequency == 0)) {
					freqLength = freqElem.substring(1, freqElem.length() - 1);
					try {
						frequency = Integer.parseInt(freqLength);
					} catch (Exception except) {
						frequency = 1;
					}
					unit = "" + freqElem.charAt(freqElem.length() - 1);
					if (frequency == 0) {
						unit = "";
					}
				}
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("o")) {
					scheduledDay = freqElem.substring(1, freqElem.length() - 1);
					if (scheduledDay.equals("0")) {
						scheduledDay = "";
					}
				}
			}
		}
		/*
		 * roll the frequency till maturity date
		 */
		ChronoUnit cycleUnit = null;
		if (unit.length() > 0) {
			switch (unit.charAt(0)) {
			case 'W':
				cycleUnit = ChronoUnit.WEEKS;
				break;
			case 'M':
				cycleUnit = ChronoUnit.MONTHS;
				break;
			case 'D':
				cycleUnit = ChronoUnit.DAYS;
				break;
			case 'Y':
				cycleUnit = ChronoUnit.YEARS;
				break;
			}
		}
		if (frequency == 0) {
			frequency = 1;
		}

		LocalDate maturityDate = LocalDate.parse(MaturityDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalDate nextInstalmentDate = getNextScheduleDate(externalDBConnection, arrangementReference, accountId,
				paybaseDate,maturityDate);

		/*
		 * evaluate previous instalment date from next instalment date
		 * 
		 */
		nextInstalmentDate = cycleUnit.addTo(nextInstalmentDate, -1 * frequency);

		if (nextInstalmentDate != null && maturityDate != null && cycleUnit != null) {
			do {
				instalmentDets.add(nextInstalmentDate);
				nextInstalmentDate = cycleUnit.addTo(nextInstalmentDate, frequency);
				if (nextInstalmentDate.isAfter(maturityDate)) {

					CurrentInstalmentDates cur = new CurrentInstalmentDates(nextInstalmentDate, maturityDate);
					nextInstalmentDate = nextInstalmentDate.plusDays(cur.daysBetween());
					instalmentDets.add(nextInstalmentDate);
					break;

				}
				if (scheduledDay != null && !scheduledDay.trim().isEmpty()) {
					switch (scheduledDay) {
					case "L":
						baseDate = baseDate.with(TemporalAdjusters.lastDayOfMonth());
						break;
					}
					try {
						if (cycleUnit.equals(ChronoUnit.MONTHS)) {
							int val = Integer.parseInt(scheduledDay);
							baseDate = baseDate.withDayOfMonth(val);

						}
					} catch (Exception except) {

					} 
				}
			} while (nextInstalmentDate.isBefore(maturityDate) || nextInstalmentDate.isEqual(maturityDate));
		}
		List<LocalDate> instalDates = instalmentDets.stream().filter(elem -> {
			if (filterDate != null) {
				return elem.isAfter(filterDate);
			} else {
				return true;
			}
		}).collect(Collectors.toList());
		return instalDates;
	}
	
	public static LocalDate getNextScheduleDate(Connection externalDBConnection, String arrangementReference,
			String accountId, String paybaseDate,LocalDate maturityDate) throws Exception {
		LocalDate scheduleDate = LocalDate.now();
		/*
		 * calculate the installments due in future
		 * 
		 */
		String freq = null;
		LocalDate baseDate = null;

		try (PreparedStatement paymentFreqSt = externalDBConnection
				.prepareStatement("SELECT PAYMENT_FREQ,DUE_FREQ,ID FROM PAYMENT$SHEDULE "
						+ "  WHERE ARRANGEMENT_ID = '" + arrangementReference
						+ "' AND ROWNUM<=1 ORDER BY ID DESC")) {

			try (ResultSet paymentFreqRs = paymentFreqSt.executeQuery()) {
				if (paymentFreqRs.next()) {
					freq = paymentFreqRs.getString("PAYMENT_FREQ");
					if (freq != null && freq.indexOf("^") != -1) {
						freq = freq.split("\\^")[0];
					}

					baseDate = LocalDate.parse(paybaseDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
					if (freq == null || freq.trim().isEmpty()) {
						freq = paymentFreqRs.getString("DUE_FREQ");
					}
				} else {
					return null;
				}
			}
		}
		int frequency = 0;
		String unit = "";
		String freqLength = "";
		String scheduledDay = "";
		if (freq != null && !freq.isEmpty()) {
			FREQ_LOOP: for (String freqElem : freq.split(" ")) {
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("e") && (frequency == 0)) {
					freqLength = freqElem.substring(1, freqElem.length() - 1);
					try {
						frequency = Integer.parseInt(freqLength);
					} catch (Exception except) {
						frequency = 1;
					}
					unit = "" + freqElem.charAt(freqElem.length() - 1);
					if (frequency == 0) {
						unit = "";
					}
				}
				if (freqElem.length() > 0 && (freqElem.charAt(0) + "").equalsIgnoreCase("o")) {
					scheduledDay = freqElem.substring(1, freqElem.length() - 1);
					if (scheduledDay.equals("0")) {
						scheduledDay = "";
					}
				}
			}
		}
		//LocalDate maturityDate = getAAMaturityDate(arrangementReference, externalDBConnection);

		/*
		 * roll the frequency till maturity date
		 */
		ChronoUnit cycleUnit = null;
		if (unit.length() > 0) {
			switch (unit.charAt(0)) {
			case 'W':
				cycleUnit = ChronoUnit.WEEKS;
				break;
			case 'M':
				cycleUnit = ChronoUnit.MONTHS;
				break;
			case 'D':
				cycleUnit = ChronoUnit.DAYS;
				break;
			case 'Y':
				cycleUnit = ChronoUnit.YEARS;
				break;
			}
		}
		switch (scheduledDay) {
		case "L":
			baseDate = baseDate.with(TemporalAdjusters.lastDayOfMonth());
			break;
		}
		LocalDate curDate = LocalDate.now();

		if (scheduledDay != null && !scheduledDay.trim().isEmpty()) {
			switch (scheduledDay) {
			case "L":
				baseDate = baseDate.with(TemporalAdjusters.lastDayOfMonth());
				break;
			}
			try {
				int val = Integer.parseInt(scheduledDay);
				if (cycleUnit.equals(ChronoUnit.MONTHS)) {
					baseDate = baseDate.withDayOfMonth(val);
				}

			} catch (Exception except) {

			}
		}
		if (baseDate.isAfter(curDate)) {
			return baseDate;
		}

		while (

		((baseDate != null && maturityDate != null)
				&& (baseDate.isBefore(maturityDate) || baseDate.isEqual(maturityDate))) && baseDate.isBefore(curDate)
				|| (maturityDate == null && baseDate.isBefore(curDate))) {

			baseDate = cycleUnit.addTo(baseDate, frequency);
			if (scheduledDay != null && !scheduledDay.trim().isEmpty()) {
				switch (scheduledDay) {
				case "L":
					baseDate = baseDate.with(TemporalAdjusters.lastDayOfMonth());
					break;
				}
				try {
					if (cycleUnit.equals(ChronoUnit.MONTHS)) {
						int val = Integer.parseInt(scheduledDay);
						baseDate = baseDate.withDayOfMonth(val);

					}
				} catch (Exception except) {

				}
			}

		}

		scheduleDate = baseDate;
		return scheduleDate;
	}
	
	private void ResponseMessages(LoanDetailsObject Loan,String unitId,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		Loan.setUnId(unitId);
		Loan.setLoanAccnumber(AccountNumber);
		Loan.setErrCode(ErrorCode);
		Loan.setErrorDesc(ErrorDescription);
	}
}
