package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

@Path("/LoanDisbursement")
public class LoanDisbursement {

	private static DataSource cmDBPool;

	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static HashMap<String, String> ActualTableName;

	private static String AcctTableRef = "ACCOUNT";
	private static String AAArrangementTableRef = "AA_ARRANGEMENT";
	private static String AAArrTermAmountRef = "AA$ARR$TERM$AMOUNT";

	public static void setDBPool(DataSource cmDBPool) {
		LoanDisbursement.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		LoanDisbursement.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = LoanDisbursementList.class, responseDescription = "Loan Disbursement Response", responseCode = "200")
	@Operation(summary = "Loan Disbursement Request", description = "returns Loan Disbursement")
	public Response getLoanDisbursement(
			@RequestBody(description = "Loan Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanDisbursementRequest.class))) LoanDisbursementRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Loan Disbursement Interface Started on ["+startTime+"]");

			String unitId = id.unId;
			String AccountNumber=id.loanAccnumber;
			
			System.out.println("Fetching Loan Disbursement For [ " + AccountNumber + " ]");

			LoanDisbursementList LnDisburseList = new LoanDisbursementList();
			LoanDisbursementObject LnDisburse = null;

			PreparedStatement ArrangementPs = null;

			System.out.println("Loan Disbursement Table Ref [" + AcctTableRef + "," + "" + AAArrTermAmountRef + ",["
					+ AAArrangementTableRef + "]");

			
			String AcctTable = ActualTableName.get(unitId + "-" + AcctTableRef);
			String AAArrTermAmountTable = ActualTableName.get(unitId + "-" + AAArrTermAmountRef);
			String AAArrangementTable = ActualTableName.get(unitId + "-" + AAArrangementTableRef);

			System.out.println("Loan Disbursement Table Names [" + AcctTable + "," + " " + AAArrTermAmountTable + ",["
					+ AAArrangementTable + "] ");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("select arrangment_id,Category from " + AcctTable + " WHERE id =? ")) {
				dbSt.setString(1, AccountNumber);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					boolean Exists = false;        
					if (dbRs.next()) {						
						LnDisburseList.setUnID(unitId);
						LnDisburseList.setLoanAccno(AccountNumber);

						ArrangementPs = dbConnection.prepareStatement("select currency,START_DATE,product_group "
								+ " from " + AAArrangementTable + " where id = ? ");
						ArrangementPs.setString(1, dbRs.getString("arrangment_id"));

						ResultSet ArrangementRs = ArrangementPs.executeQuery();
						while (ArrangementRs.next()) {
							Exists = true;
							LnDisburse = new LoanDisbursementObject();
							LnDisburse.setDisburseType("SINGLE");
							LnDisburse.setDisburseDate(
									DateFormtter.format(TimeFormat2.parse("" + ArrangementRs.getString("START_DATE"))));
							LnDisburse.setDisburseAmt(DisbursementAmount(dbConnection, dbRs.getString("arrangment_id"),
									AAArrTermAmountTable));
							LnDisburse.setCur(ArrangementRs.getString("currency"));
							LnDisburse.setDesc(ArrangementRs.getString("product_group"));
							LnDisburseList.addAccount(LnDisburse);
						}
						ArrangementRs.close();
						ArrangementPs.clearParameters();

					}
					dbRs.close();

					if (ArrangementPs != null)
						ArrangementPs.close();

					if (!Exists) {
						ResponseMessages(LnDisburseList,unitId,AccountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(LnDisburseList).build();
					}
					

				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ResponseMessages(LnDisburseList,unitId,AccountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(LnDisburseList).build();
				}
				dbSt.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				ResponseMessages(LnDisburseList,unitId,AccountNumber,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(LnDisburseList).build();
			}

			return Response.status(Status.ACCEPTED).entity(LnDisburseList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Loan Disbursement Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private Double DisbursementAmount(Connection dbconnection, String ArrangementID, String AAArrTermAmountTable) {
		Double DisbursementAmt = 0.0;

		try {
			PreparedStatement ArrTermPS = dbconnection.prepareStatement("select amount from " + AAArrTermAmountTable
					+ " " + " where arrangement_id=? order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ArrTermPS.setString(1, ArrangementID);
			ResultSet ArrTermRS = ArrTermPS.executeQuery();
			if (ArrTermRS.next()) {
				DisbursementAmt = ArrTermRS.getDouble(1);
			}
			ArrTermRS.close();
			ArrTermPS.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("DisbursementAmt [ " + DisbursementAmt + " ]");

		return DisbursementAmt;
	}
	
	private void ResponseMessages(LoanDisbursementList LnDisburseList,String unitId,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		LnDisburseList.setUnID(unitId);
		LnDisburseList.setLoanAccno(AccountNumber);
		LnDisburseList.setErrCode(ErrorCode);
		LnDisburseList.setErrorDesc(ErrorDescription);
	}
}
