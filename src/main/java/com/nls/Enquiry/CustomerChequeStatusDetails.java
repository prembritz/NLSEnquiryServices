package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

@Path("/CustomerChequeStatus")
public class CustomerChequeStatusDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccountTable = "ACCOUNT";
	private static String ChequeRegisterTable = "CHEQUE$REGISTER$SUPPLEMENT";
	private static String ChequeDescTable = "CHEQUE$TYPE";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat TimeFormat3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public static void setDBPool(DataSource cmDBPool) {
		CustomerChequeStatusDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		CustomerChequeStatusDetails.ActualTableName = ActualTableName;
	}

	NumberFormat amountFormat = new DecimalFormat("#,##0.00");
	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = CustomerChequeStatusList.class, responseDescription = "Customer Cheque Status Details Response", responseCode = "200")
	@Operation(summary = "Customer Cheque Status Details Request", description = "returns Deposit Details data")
	public Response getCustomerChequeDetails(
			@RequestBody(description = "Customer Cheque Status Details Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerChequeStatusRequest.class))) CustomerChequeStatusRequest id) {
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Customer Cheque Status Interface Started on ["+startTime+"]");
			String unitid = id.unitID;
			String cheq = id.chequeNo;
			String startChq = id.startChequeNo;
			String endChq = id.endChequeNo;
			String accno = id.accountNo;
			String startDate=id.fromDate;
			String endDate=id.toDate;
			System.out.println("Fetching Customer Cheque Status Details Request Fields For Procode: [ " + id.procCode
					+ " ] UnitId: [ " + unitid + " ]" + " accountno: [ " + accno + " ] Chequeno: [ " + cheq
					+ " ] FromDate: [ " + startDate + " ] ToDate: [ " + endDate + " ] StartChequno: [ " + startChq
					+ " ] EndChequeno: [ " + endChq + " ]");

			boolean flag = false;
			int i = 0;
			String query = "";
			CustomerChequeStatusObject chequeStatus = null;
			CustomerChequeStatusList customerChequeList = new CustomerChequeStatusList();
			
			String Condition="";Date Strartdate=null;Date enddate=null;
		    if(startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")){
		    	Strartdate = DateFormtter.parse(startDate);
		    	enddate = DateFormtter.parse(endDate);
		        Condition +=" and to_char(date_time,'DD-MM-YYYY') between '"+dateFormatter.format(Strartdate)+"' and  '"+dateFormatter.format(enddate) +"'";
		    }
			System.out.println("Customer Cheque Status Details Table Ref's [ " + unitid + " ] [" + AccountTable + "] ["
					+ ChequeRegisterTable + "]  [" + ChequeDescTable + "]");

			System.out.println(ActualTableName.get(unitid + "-" + AccountTable));
			String accountTable = ActualTableName.get(unitid + "-" + AccountTable);

			String chequeRegistTables = ActualTableName.get(unitid + "-" + ChequeRegisterTable);
			String chqTypeTable = ActualTableName.get(unitid + "-" + ChequeDescTable);

			System.out.println("Customer Cheque Status Details Actual Table Names [ " + unitid + " ] [" + accountTable
					+ "] [" + chequeRegistTables + "]");

			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("SELECT ID,ACCOUNT_TITLE FROM " + accountTable + " WHERE ID=?")) {
				dbSt.setString(1, accno);

				try (ResultSet dbRs = dbSt.executeQuery()) {
					if (dbRs.next()) {
						flag = true;
						if (++i == 1) {
							customerChequeList.setUnitId(unitid);
							customerChequeList.setAccountNumber(accno);
						}

						if (!cheq.equals("")) {
							boolean exist1=false;
							query = "SELECT CURRENCY,ID,AMOUNT,ISSUE_DATE,DATE_PRESENTED,STATUS,PAYEE_NAME,REGEXP_SUBSTR(ID,'[^.]+',1,1) AS CHEQUE_TYPE ,REGEXP_SUBSTR(ID,'[^.]+',1,3) AS CHEQUE_NUMBER FROM "
									+ chequeRegistTables + " WHERE REGEXP_SUBSTR(ID,'[0-9]+[.][0-9]+', 1,1)='" + accno + "." + cheq + "' "+Condition;
							System.out.println(query);
							System.out.println("Account Number [" + accno + " Cheque number [" + cheq + "]");
							PreparedStatement chequPs = null;
							ResultSet chequeRs = null;
							chequPs = dbConnection.prepareStatement(query);
							chequeRs = chequPs.executeQuery();
							if (chequeRs.next()) {
								exist1=true;
								chequeStatus = new CustomerChequeStatusObject();
								chequeStatus.setAccountNumber(dbRs.getString("ID"));
								chequeStatus.setAccountName(dbRs.getString("ACCOUNT_TITLE"));
								chequeStatus.setChequeCurrency(chequeRs.getString("CURRENCY"));
								chequeStatus.setChequeNumber(chequeRs.getString("CHEQUE_NUMBER"));
								chequeStatus.setChequeAmount(chequeRs.getDouble("AMOUNT"));
								chequeStatus.setChequeDate(chequeRs.getString("ISSUE_DATE") == null ? ""
										: DateFormtter
												.format(TimeFormat3.parse("" + chequeRs.getString("ISSUE_DATE"))));
								chequeStatus.setChequeReceivedDate(chequeRs.getString("DATE_PRESENTED") == null ? ""
										: DateFormtter
												.format(TimeFormat3.parse("" + chequeRs.getString("DATE_PRESENTED"))));
								chequeStatus.setChequePaidDate(chequeRs.getString("DATE_PRESENTED") == null ? ""
										: DateFormtter.format(TimeFormat3.parse(chequeRs.getString("DATE_PRESENTED"))));
								chequeStatus.setChequeStatus(
										chequeRs.getString("STATUS") == null ? "" : chequeRs.getString("STATUS"));
								chequeStatus.setChequePayeeName(chequeRs.getString("PAYEE_NAME") == null ? ""
										: chequeRs.getString("PAYEE_NAME"));
								chequeStatus.setSeqNo(chequeRs.getString("CHEQUE_NUMBER"));

								chequeStatus.setChequeType(getChequDescription(dbConnection,
										chequeRs.getString("CHEQUE_TYPE"), chqTypeTable));

								customerChequeList.addAccount(chequeStatus);
							}
							chequeRs.close();
							chequPs.close();
							
							if(!exist1) {
								ResponseMessages(customerChequeList,unitid,accno,
										ERROR_CODE.NOT_FOUND,ErrorResponseStatus.CHEQUE_NOT_FOUND.getValue());
								return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
							}
							
						} else if (!startChq.equals("") && !endChq.equals("") && cheq.equals("")) {
							int StartRange = Integer.parseInt(startChq);
							int endRange = Integer.parseInt(endChq);
							PreparedStatement chequPs = null;
							ResultSet chequeRs = null;
							boolean exist2=false;
							boolean rangeCheck=false;
							System.out.println("Start Range Lessthen end cheque:: "+(StartRange <= endRange));
							System.out.println("Start Range greterthen end cheque:: "+(StartRange >endRange));
							while (StartRange <= endRange) {
								rangeCheck=true;
								//System.out.println("Account Number [" + accno + " Cheuqe Start Range " + StartRange
								//		+ " Cheque End Range [" + endRange + "]");
								query = "SELECT CURRENCY,ID,AMOUNT,ISSUE_DATE,DATE_PRESENTED,DATE_PRESENTED,STATUS,PAYEE_NAME,REGEXP_SUBSTR(ID,'[^.]+',1,3) AS CHEQUE_NUMBER,REGEXP_SUBSTR(ID,'[^.]+',1,1) AS CHEQUE_TYPE FROM "
										+ chequeRegistTables + " WHERE REGEXP_SUBSTR(ID,'[0-9]+[.][0-9]+', 1,1)='" + accno + "."+ StartRange + "' "+Condition;
								// System.out.println(query);
								chequPs = dbConnection.prepareStatement(query);
								chequeRs = chequPs.executeQuery();
								while (chequeRs.next()) {
									exist2=true;
									chequeStatus = new CustomerChequeStatusObject();
									chequeStatus.setAccountNumber(dbRs.getString("ID"));
									chequeStatus.setAccountName(dbRs.getString("ACCOUNT_TITLE"));
									chequeStatus.setChequeCurrency(chequeRs.getString("CURRENCY"));
									chequeStatus.setChequeNumber(chequeRs.getString("CHEQUE_NUMBER"));
									chequeStatus.setChequeAmount(chequeRs.getDouble("AMOUNT"));
									chequeStatus.setChequeDate(chequeRs.getString("ISSUE_DATE") == null ? ""
											: DateFormtter
													.format(TimeFormat3.parse("" + chequeRs.getString("ISSUE_DATE"))));
									chequeStatus.setChequeReceivedDate(chequeRs.getString("DATE_PRESENTED") == null ? ""
											: DateFormtter.format(
													TimeFormat3.parse("" + chequeRs.getString("DATE_PRESENTED"))));
									chequeStatus.setChequePaidDate(chequeRs.getString("DATE_PRESENTED") == null ? ""
											: DateFormtter
													.format(TimeFormat3.parse(chequeRs.getString("DATE_PRESENTED"))));
									chequeStatus.setChequeStatus(
											chequeRs.getString("STATUS") == null ? "" : chequeRs.getString("STATUS"));
									chequeStatus.setChequePayeeName(chequeRs.getString("PAYEE_NAME") == null ? ""
											: chequeRs.getString("PAYEE_NAME"));
									chequeStatus.setSeqNo(chequeRs.getString("CHEQUE_NUMBER"));

									chequeStatus.setChequeType(getChequDescription(dbConnection,
											chequeRs.getString("CHEQUE_TYPE"), chqTypeTable));

									customerChequeList.addAccount(chequeStatus);
								}

								chequeRs.close();
								chequPs.clearParameters();
								chequPs.close();

								// System.out.println("Startrange before increasement " + StartRange);
								StartRange++;
								// System.out.println("Startrange after increasement " + StartRange);
							}
							
							if (!rangeCheck) {
								ResponseMessages(customerChequeList,unitid,accno,
										ERROR_CODE.NOT_FOUND,ErrorResponseStatus.INVALID_CHEQUE_RANGE.getValue());
								return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
							}else if(!exist2) {
								ResponseMessages(customerChequeList,unitid,accno,
										ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
								return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
							}
							
						}
					}
					dbRs.close();

					if (!flag) {
						ResponseMessages(customerChequeList,unitid,accno,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.ACCOUNT_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
					}
					dbSt.close();
				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(customerChequeList,unitid,accno,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
				}
			}catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(customerChequeList,unitid,accno,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
			}
			
			return Response.status(Status.ACCEPTED).entity(customerChequeList).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Customer Cheque Status Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String getChequDescription(Connection dbConnection, String chequeType, String chequTypeTable) {
		String description = "";
		try {
			PreparedStatement chqPs = dbConnection
					.prepareStatement("SELECT DESCRIPTION FROM " + chequTypeTable + " WHERE ID=?");
			chqPs.setString(1, chequeType);
			ResultSet descRs = chqPs.executeQuery();
			if (descRs.next()) {
				description = descRs.getString("DESCRIPTION");
			}
			descRs.close();
			chqPs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return description;
	}
	
	private void ResponseMessages(CustomerChequeStatusList customerChequeList,String unitid,String accno,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		customerChequeList.setUnitId(unitid);
		customerChequeList.setAccountNumber(accno);
		customerChequeList.setErrCode(ErrorCode);
		customerChequeList.setErrorDesc(ErrorDescription); 
	}


}
