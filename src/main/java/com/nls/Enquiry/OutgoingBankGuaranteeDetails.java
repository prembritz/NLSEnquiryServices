package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
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


@Path("/OutgoingBankGuaranteeDetails")
public class OutgoingBankGuaranteeDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String MDDealTableRef = "MD$DEAL";
	private static String CustomerTableRef = "CUSTOMER";
	private static String AccountTableRef = "ACCOUNT";
	private static String CategoryTableRef = "CATEGORY";
	LocalDate curDate=LocalDate.now();

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		OutgoingBankGuaranteeDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		OutgoingBankGuaranteeDetails.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = OutgoingBankGuaranteeDetailsObjects.class, responseDescription = "Outgoing Bank Guarantee Details Response", responseCode = "200")
	@Operation(summary = "Outgoing Bank Guarantee Details Request", description = "returns Outgoing Bank Guarantee Details data")
	public Response getOutgoingBankGuaranteeEnqDetails(
			@RequestBody(description = "ReferenceNo", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = OutgoingBankGuaranteeDetailsRequest.class))) OutgoingBankGuaranteeDetailsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Outgoing Bank Guarantee Details Interface Started on ["+startTime+"]");
			
			OutgoingBankGuaranteeDetailsObjects OutgngBnkGuaranteeDetail = new OutgoingBankGuaranteeDetailsObjects();
			
			
			    String MDDealTable = "";
			    String CategoryTable="";
			    String CustomerTable="";
			    String AccountTable="";
				String unitId = id.unId;
				String Referenceno = id.referenceNo;
		
					System.out.println(
							"Outgoing Bank Guarantee Details Tables Ref [ " + unitId + " ],[" + MDDealTableRef + "],"
									+ "["+CustomerTableRef+"],["+AccountTableRef+"],["+CategoryTableRef+"]");
					
					MDDealTable = ActualTableName.get(unitId + "-" + MDDealTableRef);
					CategoryTable = ActualTableName.get(unitId + "-" + CategoryTableRef);
					CustomerTable = ActualTableName.get(unitId + "-" + CustomerTableRef);
					AccountTable = ActualTableName.get(unitId + "-" + AccountTableRef);
					
					System.out.println(
							"Outgoing Bank Guarantee Details Actual Table Ref [ " + unitId + " ],[" + MDDealTable + "],"
									+ "[" + CustomerTable + "],[" + AccountTable + "],["+CategoryTable+"]");
			

				System.out.println("Fetching Outgoing Bank Guarantee Details Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " ReferenceNo: [ " + Referenceno + " ]");

				try (Connection dbConnection = cmDBPool.getConnection();
                       PreparedStatement dbSt=dbConnection.prepareStatement("select * from "+MDDealTable+" WHERE id=? and status='CUR' ")) {
					dbSt.setString(1, Referenceno);
					try (ResultSet dbRs = dbSt.executeQuery()) {						
						if (dbRs.next()) {	
									
							OutgngBnkGuaranteeDetail.setUnitId(unitId);
							OutgngBnkGuaranteeDetail.setGuaranteeRef(dbRs.getString("ID"));
							OutgngBnkGuaranteeDetail.setCbxReference(dbRs.getString("ID"));
							OutgngBnkGuaranteeDetail.setIssueDate((dbRs.getString("DEAL_DATE")!=null && !dbRs.getString("DEAL_DATE").isEmpty())?
									DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("DEAL_DATE"))):
										"");
							OutgngBnkGuaranteeDetail.setAccountPartyNameAdd(CustomerName(dbConnection, dbRs.getString("CUSTOMER"), CustomerTable));
							OutgngBnkGuaranteeDetail.setApplicantPartyNameAdd(AccountTitle(dbConnection, dbRs.getString("CSN_ACCOUNT"), AccountTable));
							OutgngBnkGuaranteeDetail.setBenefName(dbRs.getString("BEN_ADDRESS"));
							OutgngBnkGuaranteeDetail.setAddress1("");
							OutgngBnkGuaranteeDetail.setAddress2("");
							OutgngBnkGuaranteeDetail.setCity("");
							OutgngBnkGuaranteeDetail.setCountry("");
							OutgngBnkGuaranteeDetail.setAdvisingBankname(dbRs.getString("COUNTRY_RISK"));
							OutgngBnkGuaranteeDetail.setGuaranteeType(DescriptionPick(dbConnection, dbRs.getString("CATEGORY_CODE"), CategoryTable));
							OutgngBnkGuaranteeDetail.setGuaranteeCurrency(dbRs.getString("CURRENCY"));
							OutgngBnkGuaranteeDetail.setGuaranteeAmt(dbRs.getDouble("PRINCIPAL_AMOUNT"));
							OutgngBnkGuaranteeDetail.setExpiryDate((dbRs.getString("MATURITY_DATE")!=null && !dbRs.getString("MATURITY_DATE").isEmpty())?
									DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("MATURITY_DATE"))):
										"");
							OutgngBnkGuaranteeDetail.setGuaranteePurpose("");
							OutgngBnkGuaranteeDetail.setClaimAmt(0.0);
							OutgngBnkGuaranteeDetail.setGuarOSAmt(0.0);
							OutgngBnkGuaranteeDetail.setGuarOSAmtinLCY(0.0);
							OutgngBnkGuaranteeDetail.setTransmissionBy("");				
						} 						
						else {	
							ResponseMessages(OutgngBnkGuaranteeDetail,unitId,Referenceno,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeDetail).build();
							}
						dbRs.close();
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(OutgngBnkGuaranteeDetail,unitId,Referenceno,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeDetail).build();
					}
					dbSt.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(OutgngBnkGuaranteeDetail,unitId,Referenceno,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeDetail).build();
				}
				
			return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeDetail).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Outgoing Bank Guarantee Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
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
	
	private String AccountTitle(Connection dbconnection,String AccountId,String AccountTable)
	{
		String AccountTitle="";
		
		try
		{
		PreparedStatement ps=dbconnection.prepareStatement("select ACCOUNT_TITLE from "
				+ " "+AccountTable+" where id=? ");
		ps.setString(1, AccountId);
		ResultSet Rs=ps.executeQuery();
		if(Rs.next())
		{
			AccountTitle=Rs.getString(1);	
		}
		Rs.close();
		ps.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return AccountTitle;
	}
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
	
	private void ResponseMessages(OutgoingBankGuaranteeDetailsObjects OutgngBnkGuaranteeDetail,String unitId,String Referenceno,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		OutgngBnkGuaranteeDetail.setUnitId(unitId);
		OutgngBnkGuaranteeDetail.setGuaranteeRef(Referenceno);
		OutgngBnkGuaranteeDetail.setErrCode(ErrorCode);
		OutgngBnkGuaranteeDetail.setErrorDesc(ErrorDescription);
	}
}
