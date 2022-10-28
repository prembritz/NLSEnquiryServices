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


@Path("/ExportLCDetails")
public class ExportLCDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String LetterCreditTable = "LETTER$OF$CREDIT";
	private static String LCTypesTable = "LC$TYPES";
	private static String CustomerTable="CUSTOMER";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		ExportLCDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		ExportLCDetails.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 15, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = ExportLCDetailsObjects.class, responseDescription = "Export LC Details Response", responseCode = "200")
	@Operation(summary = "Export LC Details Request", description = "returns Export Details LC data")
	public Response getExportLCDetails(
			@RequestBody(description = "Reference No", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ExportLCDetailsRequest.class))) ExportLCDetailsRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Export LC Details Interface Started on ["+startTime+"]");
			
			ExportLCDetailsObjects ExpLC = new ExportLCDetailsObjects();
			boolean flag = false;

			    String LetterCreditTableName = "";
			    String LcTypeTableName = "";
			    String CustomerTableName="";
				String unitId =id.unId;
				String ReferenceNumber = id.referenceNo;
		
					System.out.println(
							"Export LC Details Tables Ref [ " + unitId + " ],[" + LetterCreditTable + "],"
									+ "["+LCTypesTable+"],["+CustomerTable+"]");
					
					LetterCreditTableName = ActualTableName.get(unitId + "-" + LetterCreditTable);
					LcTypeTableName = ActualTableName.get(unitId + "-" + LCTypesTable);
					CustomerTableName = ActualTableName.get(unitId + "-" + CustomerTable);
					
					System.out.println(
							"Export LC Details Actual Table Ref [ " + unitId + " ],[" + LetterCreditTableName + "],"
									+ "[" + LcTypeTableName + "],["+CustomerTableName+"]");
			

				System.out.println("Fetching Export LC Details Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " Reference Number: [ " + ReferenceNumber + " ]");

				try (Connection dbConnection = cmDBPool.getConnection();
						PreparedStatement dbSt = dbConnection
								.prepareStatement("SELECT * from "+LetterCreditTableName+" "
										+ " where ID=? ")) {
					dbSt.setString(1, ReferenceNumber);
					try (ResultSet dbRs = dbSt.executeQuery()) {
						
						if (dbRs.next()) {
							
							flag = true;	
							
							ExpLC.setAdviseReference(dbRs.getString("ID"));
							ExpLC.setLcReference(dbRs.getString("ID"));
							ExpLC.setLcTenor(dbRs.getString("TENOR"));
							ExpLC.setLcIssueDate(DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("issue_date"))));
							ExpLC.setApplicantName(dbRs.getString("APPLICANT")!=null?
									dbRs.getString("CLAUSES_TEXT").replaceAll("\\s+", " ").replace("^"," "):"");
							ExpLC.setBankName(dbRs.getString("ISSUING_BANK")!=null?
									dbRs.getString("ISSUING_BANK").replaceAll("\\s+", " ").replace("^"," "):"");
							ExpLC.setShipmentDate("");
							ExpLC.setLcExpiryDate(DateFormtter.format(TimeFormat.parse("" + dbRs.getString("expiry_Date"))));
							ExpLC.setExpiryPlace(dbRs.getString("EXPIRY_PLACE"));
							ExpLC.setLcCurrency(dbRs.getString("lc_currency"));
							ExpLC.setLcAmount(dbRs.getDouble("lc_amount"));
							ExpLC.setNegotiationValue(0.0);
							ExpLC.setOutstandingLCAmount(dbRs.getDouble("lc_amount_local"));
							ExpLC.setTransferableFlag("");
							ExpLC.setConfirmInstructions(dbRs.getString("CLAUSES_TEXT")!=null?
									dbRs.getString("CLAUSES_TEXT").replaceAll("\\s+", " ").replace("^"," "):"");
							ExpLC.setConfirmationStatus("CONFIRMED");
							ExpLC.setAdviseDetails("");
							ExpLC.setNoofAmendments((dbRs.getString("AMENDMENT_NO")==null || dbRs.getString("AMENDMENT_NO").isEmpty())?"ISSUE":"AMENDMENT");
							ExpLC.setGoodsCategory(dbRs.getString("category_code"));
						}
						dbRs.close();
						
						if (!flag) {
							ResponseMessages(ExpLC,unitId,ReferenceNumber,
									ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
							return Response.status(Status.ACCEPTED).entity(ExpLC).build();
						}
						
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(ExpLC,unitId,ReferenceNumber,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(ExpLC).build();
					}
					dbSt.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(ExpLC,unitId,ReferenceNumber,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(ExpLC).build();
				}
			return Response.status(Status.ACCEPTED).entity(ExpLC).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Export LC Details Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private void ResponseMessages(ExportLCDetailsObjects ExpLC,String unitId,String ReferenceNumber,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		ExpLC.setUnitId(unitId);
		ExpLC.setLcReference(ReferenceNumber);
		ExpLC.setErrCode(ErrorCode);
		ExpLC.setErrorDesc(ErrorDescription);
	}
}
