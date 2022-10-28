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


@Path("/OutgoingBankGuaranteeEnquiry")
public class OutgoingBankGuaranteeEnquiry {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String MDDealTableRef = "MD$DEAL";
	private static String CategoryTableRef = "CATEGORY";
	LocalDate curDate=LocalDate.now();

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		OutgoingBankGuaranteeEnquiry.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		OutgoingBankGuaranteeEnquiry.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = OutgoingBankGuaranteeList.class, responseDescription = "Outgoing Bank Guarantee Response", responseCode = "200")
	@Operation(summary = "Outgoing Bank Guarantee Request", description = "returns Outgoing Bank Guarantee data")
	public Response getOutgoingBankGuaranteeEnqDetails(
			@RequestBody(description = "CIF", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = OutgoingBankGuaranteeRequest.class))) OutgoingBankGuaranteeRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Outgoing Bank Guarantee Interface Started on ["+startTime+"]");
			
			OutgoingBankGuaranteeObjects OutgngBnkGuarantee = null;
			OutgoingBankGuaranteeList OutgngBnkGuaranteeList = new OutgoingBankGuaranteeList();
			int j = 0;
			boolean flag = false;
		
			    String MDDealTable = "";
			    String CategoryTable="";
				String unitId = id.unId;
				String CustomerId = id.cif;
		
					System.out.println(
							"Outgoing Bank Guarantee Enquiry Tables Ref [ " + unitId + " ],[" + MDDealTableRef + "],["+CategoryTableRef+"]");
					
					MDDealTable = ActualTableName.get(unitId + "-" + MDDealTableRef);
					CategoryTable= ActualTableName.get(unitId + "-" + CategoryTableRef);
					System.out.println(
							"Outgoing Bank Guarantee Enquiry Actual Table Ref [ " + unitId + " ],[" + MDDealTable + "],["+CategoryTable+"]");
			

				System.out.println("Fetching Outgoing Bank Guarantee Enquiry Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " CIF: [ " + CustomerId + " ]");

				try (Connection dbConnection = cmDBPool.getConnection();
                       PreparedStatement MDDealPs=dbConnection.prepareStatement("select * from "+MDDealTable+" WHERE customer=? "
                       		+ " and status='CUR' ")) {
					MDDealPs.setString(1, CustomerId);
					try (ResultSet MDDealRs=MDDealPs.executeQuery()) {
						while(MDDealRs.next())
						{	
							flag = true;
							OutgngBnkGuarantee = new OutgoingBankGuaranteeObjects();
							
							if(++j==1)
							{
								OutgngBnkGuaranteeList.setUnitId(unitId);
								OutgngBnkGuaranteeList.setCustomerId(CustomerId);
							}
							OutgngBnkGuarantee.setGuaranteeRef(MDDealRs.getString("ID"));
							OutgngBnkGuarantee.setCbxReference(MDDealRs.getString("ID"));
							OutgngBnkGuarantee.setGuaranteeType(DescriptionPick(dbConnection, MDDealRs.getString("CATEGORY_CODE"), CategoryTable));
							OutgngBnkGuarantee.setIssueDate((MDDealRs.getString("DEAL_DATE")!=null && !MDDealRs.getString("DEAL_DATE").isEmpty())?
									DateFormtter.format(TimeFormat2.parse("" + MDDealRs.getString("DEAL_DATE"))):
										"");
							OutgngBnkGuarantee.setExpiryDate((MDDealRs.getString("MATURITY_DATE")!=null && !MDDealRs.getString("MATURITY_DATE").isEmpty())?
									DateFormtter.format(TimeFormat2.parse("" + MDDealRs.getString("MATURITY_DATE"))):
										"");
							OutgngBnkGuarantee.setBenefName(MDDealRs.getString("BEN_ADDRESS"));
							OutgngBnkGuarantee.setGuaranteeCurrency(MDDealRs.getString("CURRENCY"));
							OutgngBnkGuarantee.setGuaranteeAmt(MDDealRs.getDouble("PRINCIPAL_AMOUNT"));
							OutgngBnkGuarantee.setGuaranteeStatus(MDDealRs.getString("STATUS"));
							OutgngBnkGuarantee.setAdvisingBank("");
							OutgngBnkGuarantee.setTransType(MDDealRs.getString("CONTRACT_TYPE"));						
							OutgngBnkGuaranteeList.addAccount(OutgngBnkGuarantee);
						}
						MDDealRs.close();					
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(OutgngBnkGuaranteeList,unitId,CustomerId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeList).build();
					}
					MDDealPs.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(OutgngBnkGuaranteeList,unitId,CustomerId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeList).build();
				}

		
			if (!flag) {
				ResponseMessages(OutgngBnkGuaranteeList,unitId,CustomerId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeList).build();
				}
			
			return Response.status(Status.ACCEPTED).entity(OutgngBnkGuaranteeList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Outgoing Bank Guarantee Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
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
	
	private void ResponseMessages(OutgoingBankGuaranteeList OutgngBnkGuaranteeList,String unitId,String CustomerId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		OutgngBnkGuaranteeList.setUnitId(unitId);
		OutgngBnkGuaranteeList.setCustomerId(CustomerId);
		OutgngBnkGuaranteeList.setErrCode(ErrorCode);
		OutgngBnkGuaranteeList.setErrorDesc(ErrorDescription);
	}
}
