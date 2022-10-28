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


@Path("/ExportLCEnquiry")
public class ExportLCEnquiry {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String LetterCreditTable = "LETTER$OF$CREDIT";
	private static String LCTypesTable = "LC$TYPES";
	private static String CustomerTable="CUSTOMER";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		ExportLCEnquiry.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		ExportLCEnquiry.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = ExportLCList.class, responseDescription = "Export LC Response", responseCode = "200")
	@Operation(summary = "Export LC Request", description = "returns Export LC data")
	public Response getExportLCDetails(
			@RequestBody(description = "CIF", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ExportLCRequest.class))) ExportLCRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Export LC Enquiry Interface Started on ["+startTime+"]");
			
			ExportLCObjects ExpLC = null;
			ExportLCList ExpList = new ExportLCList();
			boolean flag = false;
			
			Connection dbConnection = cmDBPool.getConnection();

		
			    String LetterCreditTableName = "";
			    String LcTypeTableName = "";
			    String CustomerTableName="";
				String unitId = id.unId;
				String CustomerId = id.cif;
				int j=0;
		
					System.out.println(
							"Export LC Enquiry Tables Ref [ " + unitId + " ],[" + LetterCreditTable + "],["+LCTypesTable+"],["+CustomerTable+"]");
					
					LetterCreditTableName = ActualTableName.get(unitId + "-" + LetterCreditTable);
					LcTypeTableName = ActualTableName.get(unitId + "-" + LCTypesTable);
					CustomerTableName = ActualTableName.get(unitId + "-" + CustomerTable);
					
					System.out.println(
							"Export LC Enquiry Actual Table Ref [ " + unitId + " ],[" + LetterCreditTableName + "],"
									+ "[" + LcTypeTableName + "],["+CustomerTableName+"]");
			

				System.out.println("Fetching Export LC Enquiry Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " CIF: [ " + CustomerId + " ]");

				try (PreparedStatement dbSt = dbConnection
								.prepareStatement("SELECT lc.id,Lc.issue_date,Lc.expiry_Date,lc.APPLICANT_CUSTNO,Lc.ISSUING_BANK,"
										+ " Lc.CLAUSES_TEXT,Lc.beneficiary,Lc.advising_bk_custno,"
										+ " Lc.lc_currency,Lc.lc_amount,Lc.category_code,Lc.TENOR,Lc.AMENDMENT_NO,Lc.REVOLVING_TYPE FROM "
										+ " "+LetterCreditTableName+" Lc,"+LcTypeTableName+" l where Lc.lc_type=l.id and "
												+ " lc.APPLICANT_CUSTNO=? and l.IMPORT_EXPORT='E'")) {
					dbSt.setString(1, CustomerId);
					try (ResultSet dbRs = dbSt.executeQuery()) {
						
						while (dbRs.next()) {
							flag = true;												
							ExpLC = new ExportLCObjects();
							
							if(++j==1)
							{
								ExpList.setUnitId(unitId);
								ExpList.setCustomerId(CustomerId);
							}
							
							ExpLC.setAdviseReference(dbRs.getString("ID"));
							ExpLC.setLcReference(dbRs.getString("ID"));
							ExpLC.setLcTenor(dbRs.getString("TENOR"));
							ExpLC.setLcIssueDate(DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("issue_date"))));
							ExpLC.setLcExpiryDate(DateFormtter.format(TimeFormat.parse("" + dbRs.getString("expiry_Date"))));
							ExpLC.setApplicantName(CustomerName(dbConnection, dbRs.getString("APPLICANT_CUSTNO"), CustomerTableName));
							ExpLC.setIssueBankName(dbRs.getString("ISSUING_BANK")!=null?
									dbRs.getString("ISSUING_BANK").replaceAll("\\s+", " ").replace("^"," "):"");
							ExpLC.setLcCurrency(dbRs.getString("lc_currency"));
							ExpLC.setLcAmount(dbRs.getDouble("lc_amount"));
							ExpLC.setGoodsCategory(dbRs.getString("category_code"));
							ExpLC.setTxnType((dbRs.getString("AMENDMENT_NO")==null || dbRs.getString("AMENDMENT_NO").isEmpty())?"ISSUE":"AMENDMENT");
							ExpLC.setConfirmInstructions(dbRs.getString("CLAUSES_TEXT")!=null?
									dbRs.getString("CLAUSES_TEXT").replaceAll("\\s+", " ").replace("^"," "):"");
							ExpLC.setLcStatus("CONFIRMED");							
							ExpList.addAccount(ExpLC);
						}
						dbRs.close();
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(ExpList,unitId,CustomerId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(ExpList).build();
					}
					dbSt.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(ExpList,unitId,CustomerId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(ExpList).build();
				}

		
			if (!flag) {
				ResponseMessages(ExpList,unitId,CustomerId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(ExpList).build();
			}
			return Response.status(Status.ACCEPTED).entity(ExpList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Export LC Enquiry Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private String CustomerName(Connection dbconnection,String CustomerId,String CustomerTable)
	{
		String ShortName="";
		
		try
		{
		PreparedStatement ps=dbconnection.prepareStatement("select short_name from "
				+ " "+CustomerTable+" where id=? ");
		ps.setString(1, CustomerId);
		ResultSet Rs=ps.executeQuery();
		if(Rs.next())
		{
		ShortName=Rs.getString(1);	
		}
		Rs.close();
		ps.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return ShortName;
	}
	
	private void ResponseMessages(ExportLCList ExpList,String unitId,String CustomerId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		ExpList.setUnitId(unitId);
		ExpList.setCustomerId(CustomerId);
		ExpList.setErrCode(ErrorCode);
		ExpList.setErrorDesc(ErrorDescription);
	}
}
