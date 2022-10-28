package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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

@Path("/AccountCardEStatement")
public class AccountCardEstatmentDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String AccountTable = "ACCOUNT";
	private static String StmtPrinted = "STMT$PRINTED";
	private static String CustomerBoardTable = "CIB$ONBOARD$DETAILS";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		AccountCardEstatmentDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		AccountCardEstatmentDetails.ActualTableName = ActualTableName;
	}

	@POST
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = AccountCardEstatementList.class, responseDescription = "Account Card E-statement Details Response", responseCode = "200")
	@Operation(summary = "Account Card E-statement Request", description = "returns Account Card E-statement detailss data")
	public Response GetAccountCardStatement(
			@RequestBody(description = "Account Card E-statement Details Id", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = AccountCardEstatementRequest.class))) AccountCardEstatementRequest id) {
		String unitid = id.unitId;
		String StartDate=id.startDate;
		String EndDate=id.endDate;
		String ReferenceNumber=id.referenceNum;
		String CustomerId=id.customerId;
		
		AccountCardEstatementObject account = null;
		AccountCardEstatementList accountList = new AccountCardEstatementList();
		LocalDateTime startTime = LocalDateTime.now();

		try {
			System.out.println("Account Card E-statment Details Interface Started on ["+startTime+"]");
			
			System.out.println("Fetching AccountCardE-statmentDetails Fields For unitid [ " + unitid
					+ " ] referencenum [ " + id.referenceNum + " ] " + "customerid [ " + id.customerId
					+ " ] startdate [ " + id.startDate + " ] enddate [ " + id.endDate + " ] requesttime [ "
					+ id.requestTime + " ]");

			System.out.println("Account Card E-statment Details Table Ref's [ " + unitid + " ] [" + AccountTable + "] ["
					+ StmtPrinted + "] ["+CustomerBoardTable+"]");

			String AccountTab = ActualTableName.get(unitid + "-" + AccountTable);
			String StmtPrintedTable = ActualTableName.get(unitid + "-" + StmtPrinted);
			String custBoardTable = ActualTableName.get(unitid + "-" + CustomerBoardTable);

			System.out.println("Account Card Estatment Details Actual Table Names [ " + unitid + " ] [" + AccountTab
					+ "] [" + StmtPrintedTable + "] ["+custBoardTable+"]");
			
			boolean exist = false;
			
			String StatementID="";
            String StatementDates="";
            int Stmtyear=0;
            int StmtMonth=0;
         //   int StmtDay=0;
            int CapturedMonth=0;
            int CapturedYear=0;
            int CountRepeat=0;
          //  int stmtCounts=0;
            int MonthlystmtCounts=0;
            int OVerallStmtCounts=0;
         //   int count=1;
            boolean StmtExists=false;
            
            String FromDate=TimeFormat2
					.format(DateFormtter.parse("" + StartDate));
            		String ToDate=TimeFormat2
        					.format(DateFormtter.parse("" + EndDate));
            
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement dbSt = dbConnection
							.prepareStatement("SELECT ACCOUNT_NUMBER FROM " + custBoardTable + " where CIF=?")) {
				dbSt.setString(1, CustomerId);
				
				PreparedStatement StmtPs=dbConnection.prepareStatement("select * from "+StmtPrintedTable+" "
						+ " where id like ? order by id ");
				
				try (ResultSet dbRs = dbSt.executeQuery()) {
					 if (dbRs.next()) {
						 
						exist = true;
						
						StmtPs.setString(1,""+dbRs.getString(1)+"%");
						ResultSet stmtRs=StmtPs.executeQuery();
						while(stmtRs.next())
						{
							StatementID=stmtRs.getString(1);
							//System.out.println(id);	
							StatementDates=StatementID.substring(StatementID.indexOf("-")+1,StatementID.length());
							//System.out.println(StatementDates);
							Stmtyear=Integer.parseInt(StatementDates.substring(0,4));
						//	System.out.println(Stmtyear);
							StmtMonth=Integer.parseInt(StatementDates.substring(4,6));
							//System.out.println(StmtMonth);
							//StmtDay=Integer.parseInt(StatementDates.substring(6,8));	
						//	System.out.println(StmtDay);
						//	System.out.println(Stmtyear+","+StmtMonth+","+StmtDay);
							
						
							if(Integer.parseInt(StatementDates)>=Integer.parseInt(FromDate) && Integer.parseInt(StatementDates)<=Integer.parseInt(ToDate))
							{
								
								//System.out.println("stmtMonth["+StmtMonth+"],CapturedMonth["+CapturedMonth+"]");
								StmtExists=true;								
								
								if(StmtMonth!=CapturedMonth)
								{	

								if(CountRepeat!=0)
								{
									
									account = new AccountCardEstatementObject();									
									account.setCustomerId(CustomerId);
									account.setYear(String.valueOf(CapturedYear));
									account.setMonth(String.valueOf(CapturedMonth));
									account.setAccountNumber(dbRs.getString(1));	
									OVerallStmtCounts+=MonthlystmtCounts;
									account.setMonthlyStatementCount(MonthlystmtCounts);
									accountList.addAccount(account);
									MonthlystmtCounts=0;
									//stmtCounts=0;
									//count=1;
								}
								CapturedMonth=StmtMonth;	
								CapturedYear=Stmtyear;
								CountRepeat++;
								//MonthlystmtCounts+=stmtCounts;
								
								}										
								MonthlystmtCounts+=stmtRs.getString(2).split("\\^").length;
								
					System.out.println("Month["+StmtMonth+"],year["+Stmtyear+"],MonthlystmtCounts["+MonthlystmtCounts+"]");
								
							}
						}
						StmtPs.clearParameters();
						stmtRs.close();
						
						if(StmtExists)
						{
							account = new AccountCardEstatementObject();
							account.setCustomerId(CustomerId);
							account.setYear(String.valueOf(CapturedYear));
							account.setMonth(String.valueOf(CapturedMonth));
							account.setAccountNumber(dbRs.getString(1));
							OVerallStmtCounts+=MonthlystmtCounts;
							account.setMonthlyStatementCount(MonthlystmtCounts);
							accountList.addAccount(account);
						}
						
						if(MonthlystmtCounts==0)
						{
							ResponseMessages(accountList,unitid,ReferenceNumber,CustomerId,ERROR_CODE.NOT_FOUND,
									ErrorResponseStatus.TRANSACTION_NOT_FOUND.getValue());		
							return Response.status(Status.ACCEPTED).entity(accountList).build();
						}
						
						accountList.setUnitId(unitid);
						accountList.setReferenceNum(ReferenceNumber);
						accountList.setCustomerId(CustomerId);
						accountList.setStatementsCount(OVerallStmtCounts);
						

					}
					 dbRs.close();

					if (!exist) {
						ResponseMessages(accountList,unitid,ReferenceNumber,CustomerId,ERROR_CODE.NOT_FOUND,
								ErrorResponseStatus.CUSTOMER_NOT_ONBOARDED.getValue());
						return Response.status(Status.ACCEPTED).entity(accountList).build();
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					ResponseMessages(accountList,unitid,ReferenceNumber,CustomerId,ERROR_CODE.NOT_FOUND,
							ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(accountList).build();
				}
				dbSt.close();
				StmtPs.close();

			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(accountList,unitid,ReferenceNumber,CustomerId,ERROR_CODE.NOT_FOUND,
						ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(accountList).build();
			}
			return Response.status(Status.ACCEPTED).entity(accountList).build();

		} catch (Exception except) {
			except.printStackTrace();
			ResponseMessages(accountList,unitid,ReferenceNumber,CustomerId,ERROR_CODE.NOT_FOUND,
					ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
			return Response.status(Status.ACCEPTED).entity(accountList).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Account Card E-Statement Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private void ResponseMessages(AccountCardEstatementList accountList,String unitid,String ReferenceNumber,String CustomerId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		//accountList = new AccountCardEstatementList();
		accountList.setUnitId(unitid);
		accountList.setReferenceNum(ReferenceNumber);
		accountList.setCustomerId(CustomerId);
		accountList.setStatementsCount(0);
		accountList.setErrCode(ErrorCode);
		accountList.setErrorDesc(ErrorDescription);
	}

}
