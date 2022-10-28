package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

//@Path("/CreditCardSummary")
public class CreditCardSummary {

	private static DataSource cmDBPool;
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private static HashMap<String, String> ActualTableName;

	private static String AcctTableRef = "ACCOUNT";
	
	public static void setDBPool(DataSource cmDBPool) {
		CreditCardSummary.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CreditCardSummary.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CreditCardSummaryObject.class, responseDescription = "Credit Card Summary Response", responseCode = "200")
	@Operation(summary = "Credit Card Summary Request", description = "returns Credit Card Summary data")
	public Response getCreditCardSummaryData(
			@RequestBody(description = "Credit Card Summary Id", required = true, 
			content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditCardSummaryRequest.class)))
			CreditCardSummaryRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Credit Card Summary Interface Started on ["+startTime+"]");

			Connection dbConnection = cmDBPool.getConnection();

			String CCAccountNumber = "";
			String UnitID = "";

			System.out.println("CreditCard Summary Table Ref [" + AcctTableRef + "]");

			String AcctTable = "";

			CreditCardSummaryList ccList = new CreditCardSummaryList();
			CreditCardSummaryObject ccObjects = null;

			boolean CreditCardDataExists = false;
			String UnitIDs = "";
			int count = 0;

			for (int i = 0; i < id.creditcardAcctdata.size(); i++) {

				CCAccountNumber = id.creditcardAcctdata.get(i).creditcardNumber;
				UnitID = id.creditcardAcctdata.get(i).uID;

				if (!UnitID.equals(UnitIDs)) {
					UnitIDs = UnitID;
					AcctTable = ActualTableName.get(UnitID + "-" + AcctTableRef);
				
					System.out.println("Credit Card Summary Table Names for Unit ID [" + UnitID + "],[" + AcctTable + "]");
				}
				
				System.out.println("Fetching Credit Card Summary Data For UnitID [ " + UnitID + " ]");
				System.out.println("Fetching Credit Card Summary Data For Product Code[ " + id.proCode + " ]");
				System.out.println("Fetching Credit Card Summary Data For Loan Account Number[ " + CCAccountNumber + " ]");

				try (PreparedStatement dbSt = dbConnection.prepareStatement("SELECT arrangment_id,COMPANY_ID, "
						+ " CURRENCY,ID,CATEGORY FROM " + AcctTable + " WHERE ID =? ")) {
					dbSt.setString(1, CCAccountNumber);

					try (ResultSet dbRs = dbSt.executeQuery()) {
						if (dbRs.next()) {

							ccObjects = new CreditCardSummaryObject();

							CreditCardDataExists = true;
							if (++count == 1)
								ccList.setUnId(UnitID);

							
							ccList.addLoanAccounts(ccObjects);

						}
					}
				} catch (Exception except) {
					// TODO: handle exception
					except.printStackTrace();
				}
			}

			if (!CreditCardDataExists) {
				ccList.setErrCode(ERROR_CODE.NOT_FOUND);
				ccList.setErrorDesc("Credit Card Account Not Found");
			}

			return Response.status(Status.ACCEPTED).entity(ccList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Credit Card Summary Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
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
