package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

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

//@Path("/CreditCardStatement")
public class CreditCardStatement {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccTablename = "ACCOUNT";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("ddMMyyyyhhmmss");

	public static void setDBPool(DataSource cmDBPool) {
		CreditCardStatement.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CreditCardStatement.ActualTableName = ActualTableName;
	}

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CreditCardStatementObject.class, responseDescription = "Credit Card Statement Response", responseCode = "200")
	@Operation(summary = "Credit Card Statement Request", description = "returns Credit Card Statement data")
	public Response getCreditCardStatement(
			@RequestBody(description = "Account Value Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditCardStatementRequest.class))) CreditCardStatementRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Credit Card Statement Interface Started on ["+startTime+"]");
			CreditCardStatementObject creditCard = null;
			CreditCardStatementList creditCardList = new CreditCardStatementList();
			int j = 0;
			boolean flag = false;

			/*System.out
					.println("CreditCardStatementDetails Tables Ref [ " + unitidnumber + " ]  [" + AccTablename + "]");
			AccTable = ActualTableName.get(unitidnumber + "-" + AccTablename);

			System.out.println(
					"CreditCardStatementDetails Actual Table Ref [ " + unitidnumber + " ]  [" + AccTable + "]");*/

			System.out.println("Fetching CreditCardStatement Details Request Fields For Procode: [ " + id.procCode
					+ " ] UnitId: [ " + id.unitID + " ]" + " creditCardNumber: [ " + id.creditCardNumber + " ] fromDate: [ "
					+ id.fromDate + " ] toDate: [ "+ id.toDate + " ]");
			int i=0;
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("SELECT ID,CURRENCY,TIME_STAMP FROM " + AccTablename + " WHERE ID=?")) {
				dbSt.setString(1, id.creditCardNumber);
				try (ResultSet dbRs = dbSt.executeQuery()) {
					while (dbRs.next()) {
						flag = true;
						creditCard = new CreditCardStatementObject();
						
						if(++i==1)
							creditCardList.setUnitid(id.unitID);
						
						creditCard.setTransactionProcessingDate("");
						creditCard.setReference(dbRs.getString("ID"));
						creditCard.setTransactionAmount(Double.parseDouble("0"));
						creditCard.setTransactionCode("");
						creditCard.setDebitCreditIndicator("");
						creditCard.setTransactionCurrency("");
						creditCard.setVatAmount(Double.parseDouble("0"));

						creditCardList.addAccount(creditCard);
					}

					dbRs.close();
				}
				dbSt.close();
			}

			if (!flag) {
				creditCardList.setErrCode(ERROR_CODE.NOT_FOUND);
				creditCardList.setErrorDesc("Credit Card Statement Not Found!");
			}
			return Response.status(Status.ACCEPTED).entity(creditCardList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Credit Card Statement Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

}
