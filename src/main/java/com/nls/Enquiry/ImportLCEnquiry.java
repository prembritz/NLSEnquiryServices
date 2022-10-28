package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
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


@Path("/ImportLCEnquiry")
public class ImportLCEnquiry {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static String LetterCreditTable = "LETTER$OF$CREDIT";
	private static String LCTypesTable = "LC$TYPES";

	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	public static void setDBPool(DataSource cmDBPool) {
		ImportLCEnquiry.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		ImportLCEnquiry.ActualTableName = ActualTableName;
	}
	
	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = ImportLCList.class, responseDescription = "Import LC Response", responseCode = "200")
	@Operation(summary = "Import LC Request", description = "returns Import LC data")
	public Response getImportLCDetails(
			@RequestBody(description = "CIF", required = true, content = @Content(mediaType = "application/json",
			schema = @Schema(implementation = ImportLCRequest.class))) ImportLCRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("Import LC Enquiry Interface Started on ["+startTime+"]");
			ImportLCObjects ImpLC = null;
			ImportLCList impList = new ImportLCList();
			int j = 0;
			boolean flag = false;
			
			Connection dbConnection = cmDBPool.getConnection();
		    String LetterCreditTableName = "";
		    String LcTypeTableName = "";
			String unitId = id.unId;
			String CustomerId = id.cif;
	
				System.out.println(
						"Import LC Enquiry Tables Ref [ " + unitId + " ],[" + LetterCreditTable + "],["+LCTypesTable+"]");
				
				LetterCreditTableName = ActualTableName.get(unitId + "-" + LetterCreditTable);
				LcTypeTableName = ActualTableName.get(unitId + "-" + LCTypesTable);
				
				System.out.println(
						"Import LC Enquiry Actual Table Ref [ " + unitId + " ],[" + LetterCreditTableName + "],[" + LcTypeTableName + "]");
		

				System.out.println("Fetching Import LC Enquiry Request Fields For Procode: [ " + id.proCode
						+ " ] UnitId: [ " + unitId + " ]" + " CIF: [ " + CustomerId + " ]");

				try (PreparedStatement dbSt = dbConnection
								.prepareStatement("SELECT lc.id,Lc.issue_date,Lc.expiry_Date,Lc.beneficiary,Lc.advising_bk_custno,"
										+ " Lc.lc_currency,Lc.lc_amount_local,Lc.category_code,Lc.TENOR,Lc.AMENDMENT_NO,Lc.REVOLVING_TYPE FROM "
										+ " "+LetterCreditTableName+" Lc,"+LcTypeTableName+" l where Lc.lc_type=l.id and "
												+ " lc.APPLICANT_CUSTNO=? and l.IMPORT_EXPORT='I'")) {
					dbSt.setString(1, CustomerId);
					try (ResultSet dbRs = dbSt.executeQuery()) {
						
						while (dbRs.next()) {
							flag = true;	
							ImpLC = new ImportLCObjects();
							
							if(++j==1)
							{
								impList.setUnitId(unitId);
								impList.setCustomerId(CustomerId);
							}
							ImpLC.setLcReference(dbRs.getString("ID"));
							ImpLC.setLcTenor(dbRs.getString("TENOR"));
							ImpLC.setLcIssueDate(DateFormtter.format(TimeFormat2.parse("" + dbRs.getString("issue_date"))));
							ImpLC.setLcExpiryDate(DateFormtter.format(TimeFormat.parse("" + dbRs.getString("expiry_Date"))));
							ImpLC.setBenefName(dbRs.getString("beneficiary")!=null?
									dbRs.getString("beneficiary").replaceAll("\\s+", " ").replace("^"," "):"");
							ImpLC.setAdviseBank(dbRs.getString("advising_bk_custno")!=null?
									dbRs.getString("advising_bk_custno").replaceAll("\\s+", " ").replace("^"," "):"");
							ImpLC.setLcCurrency(dbRs.getString("lc_currency"));
							ImpLC.setLcosAmt(dbRs.getDouble("lc_amount_local"));
							ImpLC.setGoodsCategory(dbRs.getString("category_code"));
							ImpLC.setTxnType((dbRs.getString("AMENDMENT_NO")==null || dbRs.getString("AMENDMENT_NO").isEmpty())?"ISSUE":"AMENDMENT");
							ImpLC.setRevolvingLC((dbRs.getString("REVOLVING_TYPE")==null || dbRs.getString("REVOLVING_TYPE").isEmpty())?"NO":"YES");
							ImpLC.setLcStatus("CONFIRMED");							
							impList.addAccount(ImpLC);
						}   
						dbRs.close();
					}
					catch (Exception except) {
						except.printStackTrace();
						ResponseMessages(impList,unitId,CustomerId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
						return Response.status(Status.ACCEPTED).entity(impList).build();
					}
					dbSt.close();
				}catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(impList,unitId,CustomerId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());
					return Response.status(Status.ACCEPTED).entity(impList).build();
				}

		
			if (!flag) {
				ResponseMessages(impList,unitId,CustomerId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());
				return Response.status(Status.ACCEPTED).entity(impList).build();
			}
			return Response.status(Status.ACCEPTED).entity(impList).build();

		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("Import LC Enquiry Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}
	
	private void ResponseMessages(ImportLCList impList,String unitId,String CustomerId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		impList.setUnitId(unitId);
		impList.setCustomerId(CustomerId);
		impList.setErrCode(ErrorCode);
		impList.setErrorDesc(ErrorDescription);
	}
}
