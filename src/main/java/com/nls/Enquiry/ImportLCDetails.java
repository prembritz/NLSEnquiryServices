package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

import javax.json.bind.annotation.JsonbPropertyOrder;
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


@Path("/ImportLCDetails")
public class ImportLCDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String LetterCreditTable = "LETTER$OF$CREDIT";
	private static String LCTypesTable = "LC$TYPES";
	private static String CustomerTable="CUSTOMER";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		ImportLCDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		ImportLCDetails.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = ImportLCDetailsObjects.class, responseDescription = "Import LC Details Response", responseCode = "200")
	@Operation(summary = "Import LC Details Request", description = "returns Import Details LC data")
	public Response getImportLCDetails(
			@RequestBody(description = "Reference No", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ImportLCDetailsRequest.class))) ImportLCDetailsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Import LC Details Interface Started on ["+startTime+"]");
			ImportLCDetailsObjects ImpLC = new ImportLCDetailsObjects();
			boolean flag = false;

			    String LetterCreditTableName = "";
			    String LcTypeTableName = "";
			    String CustomerTableName="";
				String unitId =id.unId;
				String ReferenceNumber = id.referenceNo;
		
					System.out.println(
							"Import LC Details Tables Ref [ " + unitId + " ],[" + LetterCreditTable + "],"
									+ "["+LCTypesTable+"],["+CustomerTable+"]");
					
					LetterCreditTableName = ActualTableName.get(unitId + "-" + LetterCreditTable);
					LcTypeTableName = ActualTableName.get(unitId + "-" + LCTypesTable);
					CustomerTableName = ActualTableName.get(unitId + "-" + CustomerTable);
					
					System.out.println(
							"Import LC Details Actual Table Ref [ " + unitId + " ],[" + LetterCreditTableName + "],"
									+ "[" + LcTypeTableName + "],["+CustomerTableName+"]");
			

				System.out.println("Fetching Import LC Details Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " Reference Number: [ " + ReferenceNumber + " ]");

				try (Connection dbConnection = cmDBPool.getConnection();
						PreparedStatement dbSt = dbConnection
								.prepareStatement("SELECT * FROM "
										+ " "+LetterCreditTableName+" where ID=? ")) {
					dbSt.setString(1, ReferenceNumber);
					try (ResultSet dbRs = dbSt.executeQuery()) {
						
						if (dbRs.next()) {
														
							flag = true;
							ImpLC.setUnitId(unitId);
							ImpLC.setLcReference(dbRs.getString("ID"));
							ImpLC.setAccountPartyName(CustomerName(dbConnection, dbRs.getString("APPLICANT_CUSTNO"), CustomerTableName));
							ImpLC.setApplicantName(dbRs.getString("APPLICANT")!=null?
									dbRs.getString("APPLICANT").replaceAll("\\s+", " ").replace("^"," "):"");
							ImpLC.setBeneficiaryName(dbRs.getString("beneficiary")!=null?
									dbRs.getString("beneficiary").replaceAll("\\s+", " ").replace("^"," "):"");
							ImpLC.setAddress1("");
							ImpLC.setAddress2("");
							ImpLC.setCity("");
							ImpLC.setCountry("");
							ImpLC.setAdvisingBankname(dbRs.getString("ADVISING_BK")!=null?
									dbRs.getString("ADVISING_BK").replaceAll("\\s+", " ").replace("^"," "):"");
							ImpLC.setAdviseThroughBankname("");
							ImpLC.setLcIssueDate(DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("issue_date"))));
							ImpLC.setPlaceOfExpiry(dbRs.getString("EXPIRY_PLACE"));
							ImpLC.setLcCurrency(dbRs.getString("lc_currency"));
							ImpLC.setLcAmount(dbRs.getDouble("lc_amount"));
							ImpLC.setTotValueUtilized(0.0);
							ImpLC.setLcosAmount(dbRs.getDouble("PAY_PORTION"));
							ImpLC.setOsAmountLcy(dbRs.getDouble("lc_amount_local"));
							ImpLC.setLcTenor(dbRs.getString("TENOR"));
							ImpLC.setLcTenorDetails("");
							ImpLC.setPortOfDeparture(dbRs.getString("SHIP_DESPATCH"));
							ImpLC.setNoofAmendments((dbRs.getString("AMENDMENT_NO")==null || dbRs.getString("AMENDMENT_NO").isEmpty())?"ISSUE":"AMENDMENT");
							ImpLC.setLcType(LCTypeDescription(dbConnection,dbRs.getString("LC_TYPE"),LcTypeTableName));
							ImpLC.setRevolvingLC((dbRs.getString("REVOLVING_TYPE")==null || dbRs.getString("REVOLVING_TYPE").isEmpty())?"NO":"YES");
							ImpLC.setRevolvingUnits("");					
							ImpLC.setFrequency("");
							ImpLC.setCumulativeFlag("");
							
						}
						if (!flag) {
							ResponseMessages(ImpLC,unitId,ReferenceNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(ImpLC).build();
						}
						dbRs.close();
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(ImpLC,unitId,ReferenceNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(ImpLC).build();
					}
					dbSt.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(ImpLC,unitId,ReferenceNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(ImpLC).build();
				}

	           return Response.status(Status.ACCEPTED).entity(ImpLC).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Import LC Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
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
	
	private String LCTypeDescription(Connection dbconnection,String LCType,String LCTypeTable)
	{
		String Description="";
		
		try
		{
		PreparedStatement ps=dbconnection.prepareStatement("select DESCRIPTION from "
				+ " "+LCTypeTable+" where id=? ");
		ps.setString(1, LCType);
		ResultSet Rs=ps.executeQuery();
		if(Rs.next())
		{
			Description=Rs.getString(1);	
		}
		Rs.close();
		ps.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return Description;
	}
	
	private void ResponseMessages(ImportLCDetailsObjects ImpLC,String unitId,String ReferenceNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		ImpLC.setUnitId(unitId);
		ImpLC.setLcReference(ReferenceNumber);
		ImpLC.setErrCode(ErrorCode);
		ImpLC.setErrorDesc(ErrorDescription);
	}
}
