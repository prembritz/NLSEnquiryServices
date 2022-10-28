package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

@Path("/LoanRepayment")
public class LoanRepayment {

	private static DataSource cmDBPool;

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static HashMap<String, String> ActualTableName;

	private static String AcctTableRef = "ACCOUNT";
	private static String AAArrInterestRef = "AA$ARR$INTEREST";
	private static String AABillDetailsRef = "AA$BILL$DETAILS";

	enum PropertyTypes {
		ACCOUNT, PRINCIPALINT, PENALTYINT;
	}

	public static void setDBPool(DataSource cmDBPool) {
		LoanRepayment.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		LoanRepayment.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = LoanRepaymentList.class, responseDescription = "Loan Repayment Response", responseCode = "200")
	@Operation(summary = "Loan Repayment Request", description = "returns Loan Repayment")
	public Response getLoanRepayment(
			@RequestBody(description = "Loan Account Number", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoanRepaymentRequest.class))) LoanRepaymentRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Loan Repayment Interface Started on ["+startTime+"]");

			String unitId = id.unId;
			String AccountNumber=id.loanAccnumber;
			
			System.out.println("Fetching Loan Repayment For [ " + AccountNumber + " ]");

			LoanRepaymentList LnRepayList = new LoanRepaymentList();
			LoanRepaymentObject LnRepay = null;
			Vector<String> BillStatus = null;
			Vector<String> BillStatusDate = null;
			Vector<String> Property = null;
			Vector<String> PropertyAmt = null;

			System.out.println("Loan Repayment Table Ref [" + AcctTableRef + "," + "" + AABillDetailsRef + ","
					+ AAArrInterestRef + "]");
			
			String AcctTable = ActualTableName.get(unitId + "-" + AcctTableRef);
			String AAArrInterestTable = ActualTableName.get(unitId + "-" + AAArrInterestRef);
			String AABillDetailsTable = ActualTableName.get(unitId + "-" + AABillDetailsRef);

			System.out.println("Loan Repayment Table Names [" + AcctTable + "," + "" + AABillDetailsTable + ","
					+ AAArrInterestTable + "]");

			boolean Exists = false;

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement ArrDetailsPS = dbConnection.prepareStatement("select payment_date,bill_status,"
							+ " bill_status_change_date,property,os_prop_amount from" + " " + AABillDetailsTable
							+ " where arrangement_id = ? order by payment_date desc ");

					PreparedStatement dbSt = dbConnection.prepareStatement("select ID,"
							+ " OPEN_ACTUAL_BALANCE,ARRANGMENT_ID from " + AcctTable + " WHERE id =?")) {

				dbSt.setString(1, AccountNumber);

				try (ResultSet dbRs = dbSt.executeQuery()) {

					if (dbRs.next()) {
						try {
							ArrDetailsPS.setString(1, dbRs.getString("ARRANGMENT_ID"));
							ResultSet ArrDetailsRS = ArrDetailsPS.executeQuery();

							LnRepayList.setUnId(id.unId);
							LnRepayList.setLoanAccnumber(AccountNumber);

							Double TotalAmt = 0.0, PrincipalAmt = 0.0, InterestAmt = 0.0;

							while (ArrDetailsRS.next()) {
								BillStatus = new Vector<String>();
								BillStatusDate = new Vector<String>();
								Property = new Vector<String>();
								PropertyAmt = new Vector<String>();
								PrincipalAmt = 0.0;
								InterestAmt = 0.0;
								Exists = true;
								LnRepay = new LoanRepaymentObject();
								LnRepay.setInstallmentNo("1");
								BillStatus = nlsSplit(ArrDetailsRS.getString("bill_status"), "^");
								BillStatusDate = nlsSplit(ArrDetailsRS.getString("bill_status_change_date"), "^");
								Property = nlsSplit(ArrDetailsRS.getString("property"), "^");
								PropertyAmt = nlsSplit(ArrDetailsRS.getString("os_prop_amount"), "^");

								LnRepay.setInstallmentPaystatus(
										BillStatus.get(BillStatusDate.indexOf(Collections.max(BillStatusDate))));
								LnRepay.setDueDate(DateFormtter
										.format(TimeFormat.parse("" + ArrDetailsRS.getString("PAYMENT_DATE"))));
								LnRepay.setLoanOpeningbal(dbRs.getDouble("OPEN_ACTUAL_BALANCE"));
								LnRepay.setLoanclsngBal(dbRs.getDouble("OPEN_ACTUAL_BALANCE"));
								for (int i = 0; i < Property.size(); i++) {
									// if(Property.get(i).equalsIgnoreCase("ACCOUNT"))
									if (Property.get(i).equalsIgnoreCase(PropertyTypes.ACCOUNT.toString())) {
										PrincipalAmt += Double.parseDouble(PropertyAmt.get(i));
									}
									/*
									 * else if(Property.get(i).equalsIgnoreCase("PRINCIPALINT") ||
									 * Property.get(i).equalsIgnoreCase("PENALTYINT"))
									 */
									else if (Property.get(i).equalsIgnoreCase(PropertyTypes.PRINCIPALINT.toString())
											|| Property.get(i).equalsIgnoreCase(PropertyTypes.PENALTYINT.toString())) {
										InterestAmt += Double.parseDouble(PropertyAmt.get(i));
									}
								}
								// System.out.println("PrincipalAmt="+PrincipalAmt);
								// System.out.println("InterestAmt="+InterestAmt);
								TotalAmt += (PrincipalAmt + InterestAmt);
								// System.out.println("TotalAmt="+TotalAmt);

								LnRepay.setPrincipalAmt(PrincipalAmt);
								LnRepay.setIntrstAmt(InterestAmt);
								LnRepay.setIntrstRate(ArrangementInterest(dbConnection, dbRs.getString("ARRANGMENT_ID"),
										AAArrInterestTable));
								LnRepay.setTotalAmt(TotalAmt);
								LnRepayList.addAccount(LnRepay);

							}
							ArrDetailsRS.close();
							ArrDetailsPS.close();
						} catch (Exception e) {
							e.printStackTrace();
							ResponseMessages(LnRepayList,unitId,AccountNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(LnRepayList).build();
						}
					}
					dbRs.close();

					if (!Exists) {
						ResponseMessages(LnRepayList,unitId,AccountNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(LnRepayList).build();
					}

				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ResponseMessages(LnRepayList,unitId,AccountNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(LnRepayList).build();
				}
				dbSt.close();
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				ResponseMessages(LnRepayList,unitId,AccountNumber,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(LnRepayList).build();
			}
			return Response.status(Status.ACCEPTED).entity(LnRepayList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Loan Repayment Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
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
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Effective Rate [ " + EffectiveRate + " ]");

		return EffectiveRate;
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

	private void ResponseMessages(LoanRepaymentList LnRepayList,String unitId,String AccountNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		LnRepayList.setUnId(unitId);
		LnRepayList.setLoanAccnumber(AccountNumber);
		LnRepayList.setErrCode(ErrorCode);
		LnRepayList.setErrorDesc(ErrorDescription);
	}
	
}
