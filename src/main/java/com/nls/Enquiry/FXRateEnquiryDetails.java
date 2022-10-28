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

@Path("/FXRateEnquiry")
public class FXRateEnquiryDetails {

	private static DataSource cmDBPool;
	private static HashMap<String, String> ActualTableName = null;
	private static SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat DateFormtter = new SimpleDateFormat("ddMMyyyy");
	private static SimpleDateFormat TimeFormat2 = new SimpleDateFormat("yyyyMMdd");

	private static String ForexTable = "FOREX";
	private static String BnkCurrencyTable = "BNK_CURRENCY";
	private static String AccountTable = "ACCOUNT";

	public static void setDBPool(DataSource cmDBPool) {
		FXRateEnquiryDetails.cmDBPool = cmDBPool;
	}

	public static void setExternalTablenames(HashMap<String, String> ActualTableName) {
		FXRateEnquiryDetails.ActualTableName = ActualTableName;
	}

	@Timeout(value = 3, unit = ChronoUnit.SECONDS)
	@Traced()
	@Counted()
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponseSchema(value = FXRateEnquiryObject.class, responseDescription = "Eod Triggers Sweeps Response", responseCode = "200")
	@Operation(summary = "FX Rate Enquiry Request", description = "returns FX Rate Enquiry data")
	public Response getFXRateEnquiryDetails(
			@RequestBody(description = "FX Rate Enquiry Id", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FXRateEnquiryRequest.class))) FXRateEnquiryRequest id) {
		
		LocalDateTime startTime = LocalDateTime.now();
		try {
			System.out.println("FX Rate Enquiry Interface Started on ["+startTime+"]");
			boolean exist = false;
			String sellCur = id.sellCurrency;
			String buyCur = id.buyCurrency;
			String dealNo = id.dealReferenceNumber;
			String unitId = id.unitId;
			String ref = id.referenceNum;
			String custId = id.customerNum;
			String rateCode=id.rateCode;
			System.out.println("Fetching FX Rate Enquiry Request Fields --> unitid: [ " + unitId + " ] "
					+ " referencenum: [ " + ref + " ] ratecode [ " + id.rateCode + " ] buycurrency [ " + buyCur + " ] "
					+ " customer number: [ " + custId + " ] sellcurrency: [ " + sellCur + " ] " + " amount [ "
					+ id.amount + " ] dealreferencenumber [ " + dealNo + " ]");

			System.out.println("FXRateEnquiryDetails Table Ref's [ " + unitId + " ]  [" + ForexTable + "] ["
					+ BnkCurrencyTable + "] ");

			String forex = ActualTableName.get(unitId + "-" + ForexTable);
			String currencyTab = ActualTableName.get(unitId + "-" + BnkCurrencyTable);

			System.out.println(
					"FX Rate Enquiry Details Actual Table Name [ " + unitId + " ] [" + forex + "] [" + currencyTab + "]");

			FXRateEnquiryObject fxRates = new FXRateEnquiryObject();

			String query = "";
			String cur = "KES";
			String getCur = "";
			
			if (id.dealReferenceNumber.equals("")) {
				query = "select * from " + forex + " where COUNTER_PARTY='" + custId + "' and " + " CURRENCY_BOUGHT='"
						+ buyCur + "' and CURRENCY_SOLD='" + sellCur
						+ "' and notes IS NOT NULL and dealer_notes IS NOT NULL and FX_BALANCE >10 ";
			} else {
				query = "select * from " + forex + " where ID='" + dealNo + "'";
			}

			System.out.println("FX Rate Enquiry Query :[" + query + "]");
			try (Connection dbConnection = cmDBPool.getConnection();
					PreparedStatement forexdbSt = dbConnection.prepareStatement(query)) {

				try (ResultSet forexPsRs = forexdbSt.executeQuery()) {
					if (forexPsRs.next()) {
						exist = true;
						fxRates.setUnitId(unitId);
						fxRates.setDealReferencenumber(forexPsRs.getString("ID")==null?"":forexPsRs.getString("ID"));
						fxRates.setDealRate(forexPsRs.getDouble("SPOT_RATE"));
						fxRates.setDealDate(forexPsRs.getString("DEAL_DATE")==null?"":
								DateFormtter.format(TimeFormat2.parse("" + forexPsRs.getString("DEAL_DATE"))));
						fxRates.setDealCurrency(forexPsRs.getString("CURRENCY_BOUGHT")==null?"":forexPsRs.getString("CURRENCY_BOUGHT"));
						fxRates.setDealCurrencyto(forexPsRs.getString("CURRENCY_SOLD"));
						fxRates.setDealBookingAmount(forexPsRs.getDouble("AMOUNT_BOUGHT"));
						fxRates.setReferenceNum(ref);
						fxRates.setRateCode(rateCode);
   
						if (!buyCur.equals(cur) && !sellCur.equals(cur))
							getCur = buyCur;
						else if (!buyCur.equals(cur))
							getCur = buyCur;
						else if (!sellCur.equals(cur))
							getCur = sellCur;

						String data[] = getCurrencyDetails(dbConnection, getCur, currencyTab);
						fxRates.setBuyfxRate(data[0]);
						fxRates.setSellfxRate(data[1]);
						fxRates.setMidRate(data[2]);

					}
					forexPsRs.close();

					if (!exist) {
						ResponseMessages(fxRates,unitId,ref,custId,
								ERROR_CODE.NOT_FOUND,ErrorResponseStatus.DATA_NOT_FOUND.getValue());				
						return Response.status(Status.ACCEPTED).entity(fxRates).build();
					}
				} catch (Exception except) {
					except.printStackTrace();
					ResponseMessages(fxRates,unitId,ref,custId,
							ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());				
					return Response.status(Status.ACCEPTED).entity(fxRates).build();
				}

				forexdbSt.close();
			} catch (Exception except) {
				except.printStackTrace();
				ResponseMessages(fxRates,unitId,ref,custId,
						ERROR_CODE.NOT_FOUND,ErrorResponseStatus.TABLE_MAPPING_NOT_FOUND.getValue());				
				return Response.status(Status.ACCEPTED).entity(fxRates).build();
			}

			return Response.status(Status.ACCEPTED).entity(fxRates).build();
		} catch (Exception except) {
			except.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}finally {
			LocalDateTime endTime = LocalDateTime.now();
			long millis =  ChronoUnit.MILLIS.between(startTime, endTime);
			System.out.println("FX Rate Enquiry Interface Completed and Processing Time Taken [ " + millis+ " ] MilliSeconds");
		}
	}

	private String[] getCurrencyDetails(Connection dbConnection, String currencyValue, String Curtable) {

		String[] data = new String[3];

		String BuyRate = "";
		String SellRate = "";
		String MidRate = "";
		String currencyMarket = "";
		try {
			PreparedStatement forexPs = dbConnection.prepareStatement(
					"SELECT CURRENCY_MARKET,BUY_RATE,SELL_RATE,MID_RATE FROM " + "  " + Curtable + "  WHERE ID=?");
			forexPs.setString(1, currencyValue);
			ResultSet forexRs = forexPs.executeQuery();
			if (forexRs.next()) {
				Vector<String> marketValue = split(forexRs.getString("CURRENCY_MARKET"), "^");
				Vector<String> buyRate = split(forexRs.getString("BUY_RATE"), "^");
				Vector<String> sellRate = split(forexRs.getString("SELL_RATE"), "^");
				Vector<String> midRate = split(forexRs.getString("MID_RATE"), "^");

				try {
					currencyMarket = "" + marketValue.indexOf("10");
					BuyRate = buyRate.get(Integer.parseInt(currencyMarket));
					SellRate = sellRate.get(Integer.parseInt(currencyMarket));
					MidRate = midRate.get(Integer.parseInt(currencyMarket));
				} catch (Exception e) {
					System.out.println("No Currency Market Value for 10");
					BuyRate = "";
					SellRate = "";
					MidRate = "";
				}
			}
			forexRs.close();
			forexPs.clearParameters();

		} catch (Exception e) {
			e.printStackTrace();
		}

		data[0] = BuyRate;
		data[1] = SellRate;
		data[2] = MidRate;

		return data;
	}

	public Vector<String> split(String expression, String delimeter) {
		Vector<String> tokens = new Vector<String>();
		if (expression == null) {
			tokens.add("");
			return tokens;
		}
		int index = 0;
		String tempString = "";
		do {
			index = expression.indexOf(delimeter);
			if (index == -1) {
				break;
			} else {
				tempString = expression.substring(0, index);
				expression = expression.substring(index + 1, expression.length());
				tokens.add(tempString);
			}
		} while (true);
		tokens.add(expression);

		return tokens;
	}
	
	private void ResponseMessages(FXRateEnquiryObject fxRates,String unitId,String ReferenceNumber,String custId,
			ERROR_CODE ErrorCode,String ErrorDescription)
	{
		fxRates.setUnitId(unitId);
		fxRates.setCustID(custId);
		fxRates.setReferenceNum(ReferenceNumber);
		fxRates.setErrCode(ErrorCode);
		fxRates.setErrorDesc(ErrorDescription);
	}

}
