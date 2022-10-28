package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

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

@Path("/DepositAdvise")
public class DepositAdviseEnquiry {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static NumberFormat amountFormat = new DecimalFormat("#,##0.00");
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat dateFormatter1 = new SimpleDateFormat("ddMMyyyy");

	public static void setDBPool(DataSource cmDBPool) {
		DepositAdviseEnquiry.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		DepositAdviseEnquiry.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)  
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = DepositAdviseObject.class, responseDescription = "Deposit Advise Response", responseCode = "200")
	@Operation(summary = "Deposit Advise Request", description = "returns Deposit Advise Data")
	public Response getDealRateDetails(
			@RequestBody(description = "Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepositAdviseRequest.class))) DepositAdviseRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Deposit Advise Interface Started on ["+startTime+"]");
			String unitId = id.unitID;
			String AccountNumber = id.accountNumber;
			ArrayList<Boolean> RespStatus = new ArrayList<Boolean>();
			boolean flag = false;
			System.out.println("Fetching Deposit Advise Request Fields for  Unitid [ " + unitId + " ]"
					+ " Account number [ " + AccountNumber + " ] ");

			DepositAdviseObject DepAdviseObj = new DepositAdviseObject(); 

			try (Connection dbConnection = cmDBPool.getConnection();) {

				LinkedHashMap<String, String> AdviseDetails = DepositAdvise(dbConnection, AccountNumber, unitId);

				if (AdviseDetails.size() > 0) {
					flag = true;
					RespStatus.add(flag);
					DepAdviseObj.setUnitId(unitId);
					DepAdviseObj.setAccountNumber(AccountNumber);
					DepAdviseObj.setDate(AdviseDetails.get("ReportDate"));
					DepAdviseObj.setName(AdviseDetails.get("Name"));
					DepAdviseObj.setAddress(AdviseDetails.get("Address"));
					DepAdviseObj.setPostCode(AdviseDetails.get("PostCode"));
					DepAdviseObj.setCountry(AdviseDetails.get("Country"));
					DepAdviseObj.setPrimaryOwner(AdviseDetails.get("PrimaryOwner"));
					DepAdviseObj.setDescription(AdviseDetails.get("Description"));
					DepAdviseObj.setOwner(AdviseDetails.get("Owner"));
					DepAdviseObj.setRefNumber(AdviseDetails.get("ReferenceNumber"));
					DepAdviseObj.setCurrency(AdviseDetails.get("Currency"));

					DepAdviseObj.setProduct(AdviseDetails.get("Product"));
					DepAdviseObj.setEffectiveDate(AdviseDetails.get("EffectiveDate"));
					DepAdviseObj.setDepositAmount(AdviseDetails.get("DepositAmount"));
					DepAdviseObj.setTerm(AdviseDetails.get("Term"));
					DepAdviseObj.setStartDate(AdviseDetails.get("StartDate"));
					DepAdviseObj.setMaturityDate(AdviseDetails.get("MaturityDate"));

					DepAdviseObj.setNominalRate(AdviseDetails.get("NominalRate"));
					DepAdviseObj.setTotalDue(AdviseDetails.get("TotalDue"));
					
					DepAdviseObj.setPridueDate(AdviseDetails.get("PriDate"));
					System.out.println("Principal type "+AdviseDetails.get("PriType"));
					DepAdviseObj.setPridueType(AdviseDetails.get("PriType"));
					DepAdviseObj.setPridueAmount(AdviseDetails.get("PriAmount")); 
					
					DepAdviseObj.setDepodueDate(AdviseDetails.get("DepoDate"));
					DepAdviseObj.setDepodueType(AdviseDetails.get("DepoType"));
					DepAdviseObj.setDepodueAmount(AdviseDetails.get("DepoAmount"));
					
					DepAdviseObj.setWatxdueDate(AdviseDetails.get("WatxDate"));
					DepAdviseObj.setWatxdueType(AdviseDetails.get("WatxType"));
					DepAdviseObj.setWatxdueAmount(AdviseDetails.get("WatxAmount"));
				} else {
					RespStatus.add(flag);
					ResponseMessages(DepAdviseObj, unitId, AccountNumber, ERROR_CODE.NOT_FOUND,
							ErrorResponseStatus.DATA_NOT_FOUND.getValue());
				}

			} catch (Exception except) {
				RespStatus.add(flag);
				except.printStackTrace();
				ResponseMessages(DepAdviseObj, unitId, AccountNumber, ERROR_CODE.NOT_FOUND,
						ErrorResponseStatus.FAILED.getValue());
				return Response.status(Status.ACCEPTED).entity(DepAdviseObj).build();
			}

			if (!RespStatus.contains(true)) {
				DepAdviseObj.setErrCode(ERROR_CODE.NOT_FOUND);
				DepAdviseObj.setErrorDesc(ErrorResponseStatus.DATA_NOT_FOUND.getValue());
				DepAdviseObj.setUnitId(unitId);
				DepAdviseObj.setAccountNumber(AccountNumber);
			}

			return Response.status(Status.ACCEPTED).entity(DepAdviseObj).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Deposit Advise Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private void ResponseMessages(DepositAdviseObject DepAdviseObj, String unitId, String AccountNumber,
			ERROR_CODE ErrorCode, String ErrorDescription) {
		DepAdviseObj.setUnitId(unitId);
		DepAdviseObj.setAccountNumber(AccountNumber);
		DepAdviseObj.setErrCode(ErrorCode);
		DepAdviseObj.setErrorDesc(ErrorDescription);
	}

	private LinkedHashMap<String, String> DepositAdvise(Connection dbConnection, String accountNo, String unitId)
			throws SQLException, ParseException {

		String table_currentDate = "";
		String tableolddate = "";

		String DescriptionOwner = "";
		String Owner = "";
		String EffectiveDate = "";
		String EffectDate = "";
		String RefNumber = null;
		String Currency = null;
		String Product = null;
		String StartDate = "";
		String MaturityDate = "";
		String AccountNumber = "";
		String Street = "";
		String POCode = "";
		String Country = "";
		String Country1 = "";
		String shortname = "";
		String description = "";
		String shorttile = "";
		String accounttile = "";
		String productType = "";
		
		
		//last variables
		String principlaDate="";
		String principalType="";
		String principalAmount="";
		
		String depoDate="";
		String depoType="";
		String depoAmount="";
		
		String watxDate="";
		String watxType="";
		String watxAmount="";

		int month_Value = 0;

		String StDate = "";
		String MatDate = "";
		ArrayList<String> DueDates = new ArrayList<>();
		ArrayList<String> DueType = new ArrayList<>();
		ArrayList<String> DueAmount = new ArrayList<>();
		double Interestamt = 0.0;

		LinkedHashMap<String, String> ResponseDetails = new LinkedHashMap<>();

		double Wtax = 0.00;
		double DepositAmount = 0.00;
		double DepoInterest = 0.00;
		int taxCode = 0;

		PreparedStatement datesPs = dbConnection.prepareStatement("select ID,TODAY from dates where ID=?");
		datesPs.setString(1, unitId);
		ResultSet datesRs = datesPs.executeQuery();
		if (datesRs.next()) {
			table_currentDate = datesRs.getString("TODAY");
			System.out.println("TABLE TODAY DATE :" + table_currentDate);
		}
		datesRs.close();
		datesPs.close();

		java.util.Date tableDate = dateFormat.parse(table_currentDate);
		java.util.Date curdate = new java.util.Date();
		// Compare the dates using compareTo()
		if (tableDate.compareTo(curdate) > 0) {
			// When Date d1 > Date d2
			tableolddate = dateFormat.format(curdate);
		} else if (tableDate.compareTo(curdate) < 0) {
			tableolddate = dateFormat.format(tableDate);
			// When Date d1 < Date d2
		} else if (tableDate.compareTo(curdate) == 0) {
			// When Date d1 = Date d2
			tableolddate = dateFormat.format(tableDate);
		}

		java.util.Date table_date = dateFormat.parse(tableolddate);
		Calendar tablecaldate = Calendar.getInstance();
		tablecaldate.setTime(table_date);
		tablecaldate.add(Calendar.DATE, -1);

		System.out.println("CURDATE :" + dateFormat.format(curdate));
		System.out.println("CURRENT DATE PASSED IN QUERY : " + tableolddate);
		String olddate = dateFormat.format(tablecaldate.getTime());
		System.out.println("CURRENT DATE PASSED IN QUERY minus one day: " + olddate);

		String moneyMarketQuery = "SELECT arr.id, REGEXP_SUBSTR(contract_date, '[^^]+$')  as NEW_CONTRACT_DATE,Renewal_date, to_char(arr.ACTIVITY) as ACTIVITY ,TO_CHAR(arr.DATE_TIME,'YYYY-MM-DD') DATE_TIME ,PAYMENT_START_DATE,aa.ID as AA_ID, "
				+ " to_char(aa.LINKED_APPL_ID) as LINKED_APPL_ID, aa.CUSTOMER, aa.CURRENCY, to_char(aa.PRODUCT) as PRODUCT, aa.START_DATE, ac.CONTRACT_DATE, ac.VALUE_DATE,TO_CHAR(SYSDATE,'YYYY-MM-DD') as dates, "
				+ " MATURITY_DATE,REGEXP_SUBSTR(to_char(ac.ACTIVITY_REF), '[^^]+') as ACTIVITY_REF, arr.DATE_TIME from aa_arrangement aa, aa$account$details ac,aa_arr_activity arr "
				+ " where aa.id=ac.id  and product_line = 'DEPOSITS' and  arr_status='CURRENT' and TO_CHAR(REGEXP_SUBSTR(to_char(ac.ACTIVITY_REF), '[^^]+'))=arr.id and VALUE_DATE is not null "
				+ " and TO_CHAR(aa.product)!='AA.FIX.DEP.HIGHYIELD' and TO_CHAR(arr.activity) "
				+ " not in ('DEPOSITS-CHANGE-DEPOSITINT','DEPOSITS-ACCRUE-DEPOSITINT','DEPOSITS-ROLLOVER-ARRANGEMENT') and TO_CHAR(aa.LINKED_APPL_ID)='"
				+ accountNo + "'" + " union "
				+ " SELECT  arr.id, REGEXP_SUBSTR(contract_date, '[^^]+$') as NEW_CONTRACT_DATE,Renewal_date, to_char(arr.ACTIVITY) as ACTIVITY, TO_CHAR(arr.DATE_TIME,'YYYY-MM-DD') DATE_TIME ,"
				+ " PAYMENT_START_DATE,aa.ID, to_char(aa.LINKED_APPL_ID) as LINKED_APPL_ID, aa.CUSTOMER, aa.CURRENCY, to_char(aa.PRODUCT) as PRODUCT, aa.START_DATE, ac.CONTRACT_DATE, ac.VALUE_DATE,TO_CHAR(SYSDATE,'YYYY-MM-DD') as dates, "
				+ " MATURITY_DATE,REGEXP_SUBSTR(to_char(ac.ACTIVITY_REF), '[^^]+') as ACTIVITY_REF, arr.DATE_TIME from aa_arrangement aa, aa$account$details ac,aa_arr_activity arr where aa.id=ac.id "
				+ " and product_line = 'DEPOSITS' and arr_status='CURRENT'  and TO_CHAR(REGEXP_SUBSTR(to_char(ac.ACTIVITY_REF), '[^^]+'))=arr.id and VALUE_DATE is not null  and  TO_CHAR(aa.product)='AA.FIX.DEP.HIGHYIELD' and TO_CHAR(arr.activity) "
				+ " not in ('DEPOSITS-CHANGE-DEPOSITINT','DEPOSITS-ACCRUE-DEPOSITINT','DEPOSITS-ROLLOVER-ARRANGEMENT')  and TO_CHAR(aa.LINKED_APPL_ID)='"
				+ accountNo + "' ";

		System.out.println("MoneyMarketQuery=" + moneyMarketQuery);

		PreparedStatement moneyMorket_ps = dbConnection.prepareStatement(moneyMarketQuery);

		ResultSet moneyMorket_rs = moneyMorket_ps.executeQuery();
		PreparedStatement account_ps = dbConnection.prepareStatement(
				" SELECT ID,ACCOUNT_TITLE,open_Actual_balance,CUSTOMER_ID FROM bnk_account where ID = ? ");
		ResultSet account_rs = null;

		// New Ammendments
		PreparedStatement aa_arr_accPS = dbConnection
				.prepareStatement("select id,SHORT_TITLE,ACCOUNT_TITLE_1 from aa$arr$account "
						+ "  where id like ?  order by REGEXP_SUBSTR(REGEXP_SUBSTR(ID,'[^.]+',1,1),'[^-]+$') DESC,"
						+ "  REGEXP_SUBSTR(ID,'[^.]+',1,2) DESC FETCH FIRST 1 ROW ONLY");
		ResultSet aa_arr_accRS = null;

		PreparedStatement customer_ps_email = dbConnection.prepareStatement(
				"select ID,TAX_EXEMPT,short_name,NAME,short_name,ADDRESS,COUNTRY,POST_CODE,LEGAL_ID,COUNTRY1 "
						+ " from customer where id=? ");
		ResultSet customer_rs_email = null;

		PreparedStatement aaTax_ps = dbConnection 
				.prepareStatement(" select tax$code from aa$arr$tax WHERE ID LIKE  ? order by "
						+ " REGEXP_SUBSTR(REGEXP_SUBSTR(ID,'[^.]+',1,1),'[^-]+$') DESC,"
						+ " REGEXP_SUBSTR(ID,'[^.]+',1,2) DESC FETCH FIRST 1 ROW ONLY");
		ResultSet aaTax_Rs = null;

		PreparedStatement interest_ps = dbConnection.prepareStatement(
				"SELECT arrangement_id,Activity,EFFECTIVE_RATE,FIXED_RATE,DAY_BASIS,REGEXP_SUBSTR(REGEXP_SUBSTR(ID,'[^.]+',1,1),'[^-]+$') AS dates "
						+ " FROM aa$arr$interest where id like ? order by "
						+ " REGEXP_SUBSTR(REGEXP_SUBSTR(ID,'[^.]+',1,1),'[^-]+$') desc, "
						+ " REGEXP_SUBSTR(ID,'[^.]+',1,2) DESC FETCH FIRST 1 ROW ONLY");
		ResultSet interest_rs = null;

		PreparedStatement amount_ps = dbConnection
				.prepareStatement("select AMOUNT  from aa$arr$term$amount where id like ?  "
						+ " order by REGEXP_SUBSTR(REGEXP_SUBSTR(ID,'[^.]+',1,1),'[^-]+$') desc,"
						+ " REGEXP_SUBSTR(ID,'[^.]+',1,2) DESC FETCH FIRST 1 ROW ONLY ");
		ResultSet amount_rs = null;

		PreparedStatement intBasis_ps = dbConnection.prepareStatement(
				"SELECT int_basis,REGEXP_SUBSTR(int_basis,'[^/]+$') as inteeeBasis FROM interest$basis where id =? and int_basis is not null");
		ResultSet intBasis_rs = null;

		if (moneyMorket_rs.next()) {
			String Highyieldstartdate = moneyMorket_rs.getString("NEW_CONTRACT_DATE");
			String Highyieldenddate = moneyMorket_rs.getString("MATURITY_DATE") == null ? ""
					: moneyMorket_rs.getString("MATURITY_DATE");

			java.util.Date term1 = dateFormatter.parse(Highyieldstartdate);
			Calendar start1_highyield = Calendar.getInstance();
			start1_highyield.setTime(term1);

			Calendar end1_highyield = Calendar.getInstance();

			if (Highyieldenddate.equals("")) {
				end1_highyield.setTime(term1);
				end1_highyield.add(Calendar.YEAR, 1);

			} else {
				java.util.Date term2 = dateFormatter.parse((Highyieldenddate));
				end1_highyield.setTime(term2);
				System.out.println("term2" + term2);
			}

			if (moneyMorket_rs.getString("PRODUCT").equalsIgnoreCase("AA.FIX.DEP.HIGHYIELD")) {
				java.util.Date cumat_date = dateFormat.parse(olddate);
				if (end1_highyield.getTime().compareTo(cumat_date) > 0
						|| end1_highyield.getTime().compareTo(cumat_date) < 0) {
					return ResponseDetails;
				}
			}

			String taxexempt_cus = "";

			customer_ps_email.setString(1, moneyMorket_rs.getString("CUSTOMER"));
			customer_rs_email = customer_ps_email.executeQuery();
			if (customer_rs_email.next()) {

				Street = customer_rs_email.getString("ADDRESS") == null ? "" : customer_rs_email.getString("ADDRESS");

				POCode = customer_rs_email.getString("POST_CODE") == null ? ""
						: customer_rs_email.getString("POST_CODE");
				Country = customer_rs_email.getString("COUNTRY") == null ? "" : customer_rs_email.getString("COUNTRY");

				shortname = customer_rs_email.getString("short_name") == null ? ""
						: customer_rs_email.getString("short_name");
				taxexempt_cus = customer_rs_email.getString("TAX_EXEMPT") == null ? ""
						: customer_rs_email.getString("TAX_EXEMPT");

			}
			customer_rs_email.close();
			customer_ps_email.clearParameters();

			/********************************** TaxCode ********************************/
			taxCode = 0;
			aaTax_ps.setString(1, moneyMorket_rs.getString("AA_ID") + "%");
			aaTax_Rs = aaTax_ps.executeQuery();
			if (aaTax_Rs.next()) {
				try {

					if (aaTax_Rs.getString("tax$code") != null) {
						taxCode = Integer.parseInt(aaTax_Rs.getString("tax$code"));
					}

				}

				catch (Exception e) {
					e.printStackTrace();
				}
			}

			aaTax_ps.clearParameters();
			aaTax_Rs.close();

			System.out.println("taxCode" + taxCode);
			System.out.println("taxexempt_cus" + taxexempt_cus);

			Product = moneyMorket_rs.getString("PRODUCT") == null ? "" : moneyMorket_rs.getString("PRODUCT");

			/**********************************
			 * AccountNumber
			 *******************************/
			account_ps.setString(1, moneyMorket_rs.getString("LINKED_APPL_ID"));
			account_rs = account_ps.executeQuery();
			if (account_rs.next()) {
				AccountNumber = account_rs.getString("ID") == null ? "" : account_rs.getString("ID");

			}
			account_ps.clearParameters();
			account_rs.close();

			/**********************************
			 * aa_arr_account
			 *******************************/
			aa_arr_accPS.setString(1, "%" + moneyMorket_rs.getString("AA_ID") + "%");
			aa_arr_accRS = aa_arr_accPS.executeQuery();
			if (aa_arr_accRS.next()) {

				shorttile = aa_arr_accRS.getString("SHORT_TITLE") == null ? "" : aa_arr_accRS.getString("SHORT_TITLE");

				accounttile = aa_arr_accRS.getString("ACCOUNT_TITLE_1") == null ? ""
						: aa_arr_accRS.getString("ACCOUNT_TITLE_1");

			}

			if (!accounttile.equals("")) {
				DescriptionOwner = accounttile;

			} else if (!shorttile.equals("")) {
				DescriptionOwner = shorttile;

			} else {
				DescriptionOwner = shortname;
			}
			System.out.println("Name " + DescriptionOwner);

			if (!shorttile.equals("")) {
				description = shorttile;
			} else {
				description = shortname;
			}
			System.out.println("Description " + description);

			aa_arr_accPS.clearParameters();
			aa_arr_accRS.close();

			/********************************************
			 * aa_activity
			 **********************************/
			/*********************
			 * DEPOSITS-ACCRUE-DEPOSITINT/DEPOSITS-CHANGE-DEPOSITINT
			 **********************************/

			amount_ps.setString(1, moneyMorket_rs.getString("AA_ID") + "%");
			amount_rs = amount_ps.executeQuery();
			if (amount_rs.next()) {
				if (amount_rs.getString("amount") != null && !amount_rs.getString("amount").equals(""))
					DepositAmount = Double.parseDouble(amount_rs.getString("amount"));

			}

			System.out.println("AMOUNT : " + DepositAmount);

			/*******************************************
			 * PaymentFrequency
			 **********************************/
			/*********************
			 * DEPOSITS-ACCRUE-DEPOSITINT/DEPOSITS-CHANGE-DEPOSITINT
			 **********************************/

			EffectiveDate = moneyMorket_rs.getString("VALUE_DATE");
			MaturityDate = moneyMorket_rs.getString("MATURITY_DATE") == null
					? moneyMorket_rs.getString("PAYMENT_START_DATE")
					: moneyMorket_rs.getString("MATURITY_DATE");
			if (MaturityDate == null) {

				Date datemat = dateFormat.parse(moneyMorket_rs.getString("DATE_TIME"));
				MaturityDate = dateFormatter.format(datemat);
				System.out.println("DATE_TIME: " + MaturityDate);

			}

			StartDate = moneyMorket_rs.getString("START_DATE");// used for write
			// pdf start
			// date only

			/**************************************
			 * Daydiff_Calculation
			 **********************************/
			interest_ps.setString(1, moneyMorket_rs.getString("AA_ID") + "%");
			interest_rs = interest_ps.executeQuery();

			System.out.println("ValueDate: " + EffectiveDate + "  MaturityDate: " + MaturityDate);
			Calendar caleffective_Date = Calendar.getInstance();
			caleffective_Date.setTime(dateFormatter.parse(EffectiveDate));

			Calendar calmaturity_Date = Calendar.getInstance();
			calmaturity_Date.setTime(dateFormatter.parse(MaturityDate));

			double efectiverate = 0.00;
			double number_of_diffdays = 0;
			if (interest_rs.next()) {
				// efectiverate = Double.parseDouble(interest_rs.getString("EFFECTIVE_RATE") ==
				// null
				// || interest_rs.getString("EFFECTIVE_RATE").equals("") ? "0.0"
				// : interest_rs.getString("EFFECTIVE_RATE"));

				efectiverate = getInterestRate(dbConnection, interest_rs.getString("arrangement_id"),
						"aa$arr$interest");

				if ((!EffectiveDate.equals("") && EffectiveDate != null)
						&& (!MaturityDate.equals("") && MaturityDate != null)) {
					number_of_diffdays = dayBetween(caleffective_Date, calmaturity_Date);

				}
			}
			/**********************************
			 * Dybasis_Cal
			 *********************************/
			int daybasis_Cur = 0;
			if (moneyMorket_rs.getString("CURRENCY").equalsIgnoreCase("KES")
					|| moneyMorket_rs.getString("CURRENCY").equalsIgnoreCase("GBP")) {

				daybasis_Cur = 365;

			} else if (moneyMorket_rs.getString("CURRENCY").equalsIgnoreCase("USD")
					|| moneyMorket_rs.getString("CURRENCY").equalsIgnoreCase("EUR")) {
				daybasis_Cur = 360;
			} else {

				intBasis_ps.setString(1, interest_rs.getString("DAY_BASIS"));
				intBasis_rs = intBasis_ps.executeQuery();
				if (intBasis_rs.next()) {
					try {
						daybasis_Cur = intBasis_rs.getInt("inteeeBasis");

					} catch (Exception e) {
						daybasis_Cur = 365;
					}
				} else {
					daybasis_Cur = 365;
				}
			}

			interest_rs.close();
			intBasis_ps.clearParameters();

			System.out.println("Currency :  " + (moneyMorket_rs.getString("CURRENCY")));

			/*************************************************
			 * AA.FIX.DEP.HIGHYIELD
			 ***********************************************/

			DepoInterest = 0.00;
			if (moneyMorket_rs.getString("PRODUCT").equalsIgnoreCase("AA.FIX.DEP.HIGHYIELD")) {
				DecimalFormat numberFormat = new DecimalFormat("#,##0.00");
				System.out.println("MATURITY_DATE_HIGHYIELD " + dateFormat.format(end1_highyield.getTime()));
				Double tax_amount = 0.0;
				Double Interest_amount = 0.0;
				Double TotalInterest = 0.0;
				Double totaltax = 0.0;
				Double totalpay = 0.0;
				Double totalpayamt = 0.0;

				Calendar middle = Calendar.getInstance();

				Calendar startdate = Calendar.getInstance();
				startdate.setTime(term1);

				int m = 1;
				int daydiff1 = 0;
				String paymentfreqYear = getPaymentFreq("e1y e0m e0W e0D e0F");
				while (true) {

					if (m == 1) {

						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {

							startdate.add(Calendar.YEAR,
									Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));
							daydiff1 = dayBetween(start1_highyield, startdate);
							middle.setTime(startdate.getTime());

						}

					} else {
						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {

							startdate.add(Calendar.YEAR,
									Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));

							daydiff1 = dayBetween(middle, startdate);
							middle.setTime(startdate.getTime());

						}
					}

					if (middle.after(end1_highyield)) {

						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {
							middle.add(Calendar.YEAR,
									-Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));
							daydiff1 = dayBetween(middle, end1_highyield);
							middle.add(Calendar.DAY_OF_YEAR, daydiff1);

						}

					}

					String firstDate1 = dateFormatter.format(middle.getTime());

					Calendar tmp = Calendar.getInstance();
					Interest_amount = getIntertest1(DepositAmount, efectiverate, daydiff1, tmp, daybasis_Cur);
					tax_amount = gettax(Interest_amount);
					totalpay = gettotalpay(Interest_amount, tax_amount);

					TotalInterest += Interest_amount;
					totaltax += tax_amount;
					totalpayamt += totalpay;

					System.out.println(firstDate1 + "\t Days:" + daydiff1 + "\t Interst:"
							+ numberFormat
									.format((getIntertest1(DepositAmount, efectiverate, daydiff1, tmp, daybasis_Cur)))
							+ "\twtax: " + numberFormat.format(tax_amount) + "\ttotalPay: "
							+ numberFormat.format(totalpay));

					if (dateFormatter.format(middle.getTime()).equals(dateFormatter.format(end1_highyield.getTime()))) {

						break;
					}
					m++;
				}

				System.out.println("::::TOTAL_INTEREST:::" + numberFormat.format(TotalInterest));
				System.out.println(":::TOTAL::TAX::::" + numberFormat.format(totaltax));
				System.out.println("::TOTAL::PAYMENT:::::" + numberFormat.format(totalpayamt));
				DepoInterest = (TotalInterest);
				Wtax = totaltax;

			} else {

				/*******************************************/
				System.out.println("Reference:" + moneyMorket_rs.getString("AA_ID") + "\t Amount:" + DepositAmount
						+ "\tEffRate:" + efectiverate + "\tDiffDays:" + number_of_diffdays + "\tDaybases:"
						+ daybasis_Cur + "\tInterest:"
						+ DepositAmount * efectiverate / 100 * number_of_diffdays / daybasis_Cur);
				/*******************************************/

				DepoInterest += (DepositAmount * efectiverate / 100 * number_of_diffdays / daybasis_Cur);

				System.out.println("After Interest: " + DepoInterest);
				System.out.println("Gross Interest: " + DepoInterest);
				Wtax = DepoInterest * 15 / 100;
				System.out.println("Wtax :" + Wtax);
			}

			RefNumber = moneyMorket_rs.getString("AA_ID");
			Owner = moneyMorket_rs.getString("CUSTOMER") == null ? "" : moneyMorket_rs.getString("CUSTOMER");
			Currency = moneyMorket_rs.getString("CURRENCY") == null ? "" : moneyMorket_rs.getString("CURRENCY");

			account_rs.close();
			if (amount_rs != null) {
				amount_rs.close();
			}
			if (interest_rs != null) {
				interest_rs.close();
			}

			if (intBasis_rs != null) {
				intBasis_rs.close();
			}
			amount_ps.clearParameters();
			interest_ps.clearParameters();
			intBasis_ps.clearParameters();

			java.util.Date Effective_Date_term1 = dateFormatter.parse(EffectiveDate);
			Calendar start1 = Calendar.getInstance();
			start1.setTime(Effective_Date_term1);

			java.util.Date Effective_Date_term2 = dateFormatter.parse((MaturityDate));
			Calendar end1 = Calendar.getInstance();
			end1.setTime(Effective_Date_term2);

			if (Currency.equalsIgnoreCase("KES")) {
				Interestamt = Double.parseDouble(getDecimalAmt(DepoInterest));
			} else {
				Interestamt = DepoInterest;
			}

			if (taxCode == 5 || taxexempt_cus.equalsIgnoreCase("Y")) {
				Wtax = 0.00;
			}
			double Wtaxamt = 0.0;// getDecimalAmt(Wtax);
			if (Currency.equalsIgnoreCase("KES")) {
				Wtaxamt = Double.parseDouble(getDecimalAmt(Wtax));
			} else {
				Wtaxamt = (Wtax);
			}

			if (moneyMorket_rs.getString("PRODUCT").equalsIgnoreCase("AA.FIX.DEP.HIGHYIELD")) {
				month_Value = getMonthsDifference(start1_highyield, end1_highyield);
			} else {
				month_Value = getMonthsDifference(start1, end1);
			}

			double totaldueamt = 0.0;
			if (Currency.equalsIgnoreCase("KES")) {
				totaldueamt = (DepositAmount) + Double.parseDouble(getDecimalAmt(DepoInterest))
						- Double.parseDouble(getDecimalAmt(Wtax));
			} else {
				totaldueamt = (DepositAmount + DepoInterest - Wtax);
			}

			String TermDetails = "";

			if (Product.contains("HIGHYIELD"))
				productType = "Highyield Fixed Deposit";
			else if (Product.contains("FIX")) {
				productType = "FIXED Deposit"; 

				if (month_Value != 0) {
					TermDetails = String.valueOf(month_Value).replace("-", "") + " M";
				} else {
					month_Value = dayBetween(start1, end1);
					TermDetails = String.valueOf(month_Value).replace("-", "") + " D";
				}
				System.out.println("Month Difference : " + month_Value);

				if (Product.contains("HIGHYIELD")) {
					StDate = dateFormatter1.format(dateFormatter.parse(Highyieldstartdate)).toUpperCase();
					DueDates.add(StDate);
				} else {
					StDate = dateFormatter1.format(dateFormatter.parse(StartDate)).toUpperCase();
					DueDates.add(StDate);
				}
				
				principalType="Principal";
				principlaDate=StDate;
				principalAmount=String.valueOf(amountFormat.format(DepositAmount));

				if (moneyMorket_rs.getString("PRODUCT").equalsIgnoreCase("AA.FIX.DEP.HIGHYIELD")) {
					MatDate = dateFormatter1.format((end1_highyield.getTime())).toUpperCase();
					DueDates.add(MatDate);
				} else {
					MatDate = dateFormatter1.format(dateFormatter.parse(MaturityDate)).toUpperCase();
					DueDates.add(MatDate);
				}
				
				depoType="Deposit Interest";
				depoAmount=amountFormat.format(Interestamt);
				depoDate=MatDate;

				watxDate="";
				watxType="W-Tax"; 
				watxAmount=amountFormat.format(Wtaxamt);

			} else
				productType = "Call Deposit";

			if (Product.contains("HIGHYIELD"))
				EffectDate = dateFormatter1.format(dateFormatter.parse(Highyieldstartdate)).toUpperCase();
			else
				EffectDate = dateFormatter1.format(dateFormatter.parse(EffectiveDate)).toUpperCase();

			ResponseDetails.put("ReportDate", dateFormatter1.format(new Date()).toUpperCase());
			ResponseDetails.put("Name", DescriptionOwner);
			ResponseDetails.put("Address", Street);
			ResponseDetails.put("PostCode", POCode);
			ResponseDetails.put("Country", Country + " " + Country1);
			ResponseDetails.put("PrimaryOwner", shortname);
			ResponseDetails.put("Description", description);
			ResponseDetails.put("Owner", Owner);

			ResponseDetails.put("AccountNumber", AccountNumber);
			ResponseDetails.put("ReferenceNumber", RefNumber);
			ResponseDetails.put("Currency", Currency);
			ResponseDetails.put("Product", productType);
			ResponseDetails.put("EffectiveDate", EffectDate);
			ResponseDetails.put("DepositAmount", amountFormat.format(DepositAmount));

			ResponseDetails.put("Term", TermDetails);
			ResponseDetails.put("StartDate", StDate);
			ResponseDetails.put("MaturityDate", MatDate);
			ResponseDetails.put("NominalRate", amountFormat.format(efectiverate) + " % p.a");
			
			ResponseDetails.put("PriDate", principlaDate.toString());
			ResponseDetails.put("PriType", principalType.toString());
			ResponseDetails.put("PriAmount", principalAmount.toString());
			
			ResponseDetails.put("DepoDate", depoDate.toString());
			ResponseDetails.put("DepoType", depoType.toString());
			ResponseDetails.put("DepoAmount", depoAmount.toString());
			
			ResponseDetails.put("WatxDate", watxDate.toString());
			ResponseDetails.put("WatxType", watxType.toString());
			ResponseDetails.put("WatxAmount", watxAmount.toString());
			
			
			
			ResponseDetails.put("TotalDue", amountFormat.format(totaldueamt));

		}

		aaTax_ps.close();
		customer_ps_email.close();
		account_ps.close();
		amount_ps.close();
		interest_ps.close();
		moneyMorket_rs.close();
		moneyMorket_ps.close();
		intBasis_ps.close();

		return ResponseDetails;
	}

	public Integer getYearDays(int year) {
		int yeardays = 0;
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0));
		if (isLeapYear)
			yeardays = 366;
		else
			yeardays = 365;

		return yeardays;
	}

	public Vector<String> split(String expression, String delimeter) {
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

	public int dayBetween(final Calendar startDate, final Calendar endDate) throws SQLException {

		LocalDateTime loc_effectiveDate = getLocalDate(startDate);
		LocalDateTime loc_maturityDate = getLocalDate(endDate);
		int daydiff = (int) ChronoUnit.DAYS.between(loc_effectiveDate, loc_maturityDate);

		return daydiff;
	}

	public static int getMonthsDifference(final Calendar startDate, final Calendar endDate) {

		LocalDateTime loc_effectiveDate = getLocalDate(startDate);
		LocalDateTime loc_maturityDate = getLocalDate(endDate);
		int monthdiff = (int) ChronoUnit.MONTHS.between(loc_effectiveDate, loc_maturityDate);

		return monthdiff;
	}

	public static LocalDateTime getLocalDate(Calendar calendar) {

		return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
	}

	public static String getPaymentFreq(String freq) {
		String freqs = "1M";
		if (freq != null) {
			String pay[] = freq.split(" ");

			if ((pay[0].indexOf("0Y") == 1 || pay[0].indexOf("0y") == 1)
					&& (pay[1].indexOf("0M") == 1 || pay[1].indexOf("0m") == 1))
				freqs = "1M";
			else if (pay[0].indexOf("0Y") == 1 || pay[0].indexOf("0y") == 1)
				freqs = pay[1].substring(1, pay[1].length());
			else if (pay[1].indexOf("0M") == 1 || pay[1].indexOf("0m") == 1)
				freqs = pay[0].substring(1, pay[0].length());
		}
		return freqs;
	}

	public static double getIntertest1(final double priciple, final double rate, final double dayDifference,
			final Calendar startDate, int daybasis_Cur) {
		double inte = 0.0;
		// double inte1 = 0.0;
		// int daybasis;

		for (int i = 0; i < dayDifference; i++) {

			// daybasis = startDate.getActualMaximum(Calendar.DAY_OF_YEAR);
			inte += priciple * (rate / 100) * 1 / daybasis_Cur;

		}
		return (inte);
	}

	public static double gettax(final double wtax) {
		// double inte = 0.0;
		double inte = 0.0;
		inte += (wtax * 15) / 100;

		return (inte);
	}

	public static double gettotalpay(final double interest, final double wtax) {
		// double inte = 0.0;
		double total_pay = 0.0;
		total_pay += interest - wtax;

		return total_pay;

	}

	public static String getDecimalAmt(double amount) {

		NumberFormat amountFormat = new DecimalFormat("######0.00");
		NumberFormat amtFormat = new DecimalFormat("######0.0");
		// NumberFormat amountFormating = new DecimalFormat("#,###,##0.00");
		String checkLast = "0.00";
		int lastValue = 0;
		String value = "0.00";
		String conversion_value = "0.00";

		checkLast = amountFormat.format(amount);

		lastValue = Integer.parseInt(String.valueOf(checkLast).substring(String.valueOf(checkLast).length() - 1));
		if (lastValue >= 5 && lastValue <= 9) {
			value = amountFormat.format(amount);

			conversion_value = value.substring(0, value.length() - 1) + "5";
		} else {
			conversion_value = amtFormat.format(amount);

		}

		return conversion_value;

	}

	private Double getInterestRate(Connection dbconnection, String ArrangementID, String AAArrInterestTable) {
		Double EffectiveRate = 0.0;

		try {
			PreparedStatement ArrInterestPS = dbconnection
					.prepareStatement("select REGEXP_SUBSTR(EFFECTIVE_RATE, '[^^]+$') as EFFECTIVE_RATE" + " from "
							+ AAArrInterestTable + " where arrangement_id = ? "
							+ " and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc ");
			ArrInterestPS.setString(1, ArrangementID);
			ResultSet ArrInterestRS = ArrInterestPS.executeQuery();
			if (ArrInterestRS.next()) {
				EffectiveRate = Double.parseDouble(ArrInterestRS.getString("EFFECTIVE_RATE") == null
						|| ArrInterestRS.getString("EFFECTIVE_RATE").equals("") ? "0.0"
								: ArrInterestRS.getString("EFFECTIVE_RATE"));
			}
			ArrInterestRS.close();
			ArrInterestPS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Effective Rate [ " + EffectiveRate + " ]");

		return EffectiveRate;
	}
}
