package com.nls.Enquiry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class InterestCalculation {

	public static double getDepositInterest(Connection dbConnection, String arrangeId, String arrangeTable,
			String Product, String contractDate, String maturityDate, String currency, String accountNO) {

		double DepoInterest = 0.00;
		DecimalFormat numberFormat = new DecimalFormat("#,##0.00");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dateFormatter5 = new SimpleDateFormat("yyyy-MM-dd");
		String EffectiveDate = "";
		String MaturityDate = "";
		String dailyBasis = "";
		double DepositAmount = 0.0;
		double efectiverate = 0.00;
		double number_of_diffdays = 0;
		String aa_activity_main = "";
		try {

			PreparedStatement account_ps = dbConnection.prepareStatement(
					" SELECT ID,ACCOUNT_TITLE,open_Actual_balance,online_working_balance,CUSTOMER_ID FROM account where ID = ? ");
			ResultSet account_rs = null;

			PreparedStatement activityPs = dbConnection
					.prepareStatement("SELECT ACTIVITY FROM AA_ARR_ACTIVITY WHERE ARRANGEMENT=?");
			ResultSet activityRs = null;

			PreparedStatement amount_ps = dbConnection.prepareStatement(
					"select AMOUNT  from `aa$arr$term$amount` where arrangement_ID = ? AND rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ResultSet amount_rs = null;

			PreparedStatement accountDetails = dbConnection.prepareStatement(
					"SELECT REGEXP_SUBSTR(CONTRACT_DATE,'[^^]+$') AS CONTRACT_DATE,RENEWAL_DATE,ID,VALUE_DATE,SYSDATE as CUR_DATE,MATURITY_DATE  FROM aa$account$details  WHERE ID=?");
			ResultSet accountDetailsRs = null;

			PreparedStatement interest_ps = dbConnection
					.prepareStatement("select DAY_BASIS,REGEXP_SUBSTR(EFFECTIVE_RATE, '[^^]+$') as EFFECTIVE_RATE from "
							+ "	 aa$arr$interest  where arrangement_id = ?  and rownum<=1 order by REGEXP_SUBSTR(id, '[^-]+$') desc");
			ResultSet interest_rs = null;

			PreparedStatement intBasis_ps = dbConnection.prepareStatement(
					"SELECT ID,int_basis, REGEXP_SUBSTR(int_basis,'[^/]+$') AS inteeeBasis  FROM interest$basis where id =? and int_basis is not null");
			ResultSet intBasis_rs = null;

			java.util.Date term1 = dateFormatter.parse(contractDate);
			Calendar start1_highyield = Calendar.getInstance();
			start1_highyield.setTime(term1);

			Calendar end1_highyield = Calendar.getInstance();

			if (contractDate.equals("")) {
				end1_highyield.setTime(term1);
				end1_highyield.add(Calendar.YEAR, 1);

			} else {
				java.util.Date term2 = dateFormatter.parse((contractDate));
				end1_highyield.setTime(term2);
				System.out.println("term2" + term2);
			}
			
			activityPs.setString(1, arrangeId);
			activityRs = activityPs.executeQuery();
			if (activityRs.next()) {
				aa_activity_main = activityRs.getString("ACTIVITY") == null ? "" : activityRs.getString("ACTIVITY");
				if ((aa_activity_main.equalsIgnoreCase("DEPOSITS-ACCRUE-DEPOSITINT")
						|| aa_activity_main.equalsIgnoreCase("DEPOSITS-CHANGE-DEPOSITINT")
						|| aa_activity_main.equalsIgnoreCase("DEPOSITS-ROLLOVER-ARRANGEMENT"))) {

					account_ps.setString(1, accountNO);
					account_rs = account_ps.executeQuery();
					if (account_rs.next()) {
						if (account_rs.getString("online_working_balance") != null
								&& !account_rs.getString("online_working_balance").equals(""))
							DepositAmount = Double.parseDouble(account_rs.getString("online_working_balance"));
						System.out.println("DepositAmount From account " + DepositAmount);
						if (DepositAmount == 0.0) {
							amount_ps.setString(1, arrangeId);
							amount_rs = amount_ps.executeQuery();
							if (amount_rs.next()) {
								DepositAmount = Double.parseDouble(amount_rs.getString("AMOUNT"));
								System.out.println(
										"DepositAmount From account is zero /pick term amount " + DepositAmount);
							}
							amount_rs.close();
							amount_ps.clearParameters();
						}
					} else {
						amount_ps.setString(1, arrangeId);
						amount_rs = amount_ps.executeQuery();
						if (amount_rs.next()) {
							System.out.println("DepositAmount From account is zero /pick term amount " + DepositAmount);
							DepositAmount = Double.parseDouble(amount_rs.getString("AMOUNT"));
						}
						amount_rs.close();
						amount_ps.clearParameters();
					}
					account_rs.close();
					account_ps.clearParameters();
				}
			}
			amount_ps.close();
			account_ps.close();

			System.out.println("Deposit Amount [" + DepositAmount + "]");

			accountDetails.setString(1, arrangeId);
			accountDetailsRs = accountDetails.executeQuery();
			if (accountDetailsRs.next()) {
				EffectiveDate = accountDetailsRs.getString("VALUE_DATE");
				MaturityDate = accountDetailsRs.getString("MATURITY_DATE") == null
						? accountDetailsRs.getString("PAYMENT_START_DATE")
						: accountDetailsRs.getString("MATURITY_DATE");
			}
			accountDetailsRs.close();
			accountDetails.close();
			
			
			String cal_effective_date = "";
			if ((aa_activity_main.equalsIgnoreCase("DEPOSITS-ACCRUE-DEPOSITINT")
					|| aa_activity_main.equalsIgnoreCase("DEPOSITS-CHANGE-DEPOSITINT")
					|| aa_activity_main.equalsIgnoreCase("DEPOSITS-ROLLOVER-ARRANGEMENT"))) {

			accountDetails.setString(1, arrangeId);
			accountDetailsRs = accountDetails.executeQuery();
			if (accountDetailsRs.next()) {
				if (Product.contains("FIX")) {
					cal_effective_date = accountDetailsRs.getString("NEW_CONTRACT_DATE");
					Date date1 = dateFormatter.parse(cal_effective_date);
					EffectiveDate = dateFormatter.format(date1);
					Calendar start = Calendar.getInstance();
					start.setTime(date1);
				} else {
					cal_effective_date = accountDetailsRs.getString("VALUE_DATE");
					Date date1 = dateFormatter.parse(cal_effective_date);
					EffectiveDate = dateFormatter.format(date1);
					Calendar start = Calendar.getInstance();
					start.setTime(date1);
				}

				if (Product.contains("FIX")) {
					Date date2 = dateFormatter.parse(
							accountDetailsRs.getString("MATURITY_DATE") == null ? accountDetailsRs.getString("Renewal_date")
									: accountDetailsRs.getString("MATURITY_DATE"));

					MaturityDate = dateFormatter.format(date2);
				} else {
					MaturityDate = accountDetailsRs.getString("MATURITY_DATE") == null
							? accountDetailsRs.getString("PAYMENT_START_DATE")
							: accountDetailsRs.getString("MATURITY_DATE");
					if (MaturityDate == null) {
						Date datemat = dateFormatter5.parse(accountDetailsRs.getString("CUR_DATE"));
						MaturityDate = dateFormatter.format(datemat);
						System.out.println("DATE_TIME: " + MaturityDate);

					}
				  }
				}
			}
			

			System.out.println("ValueDate: " + EffectiveDate + "  MaturityDate: " + MaturityDate);

			Calendar caleffective_Date = Calendar.getInstance();
			caleffective_Date.setTime(dateFormatter.parse(EffectiveDate));

			Calendar calmaturity_Date = Calendar.getInstance();
			calmaturity_Date.setTime(dateFormatter.parse(MaturityDate));

			interest_ps.setString(1, arrangeId);
			interest_rs = interest_ps.executeQuery();
			if (interest_rs.next()) {
				dailyBasis = interest_rs.getString("DAY_BASIS") == null ? "" : interest_rs.getString("DAY_BASIS");
				efectiverate = Double.parseDouble(
						interest_rs.getString("EFFECTIVE_RATE") == null ? "" : interest_rs.getString("EFFECTIVE_RATE"));
				if ((!EffectiveDate.equals("") && EffectiveDate != null)
						&& (!MaturityDate.equals("") && MaturityDate != null)) {
					number_of_diffdays = dayBetween(caleffective_Date, calmaturity_Date);

				}
			}

			interest_rs.close();
			intBasis_ps.clearParameters();

			System.out.println("DailyBasis [" + dailyBasis + "] Efective Rate [" + efectiverate + "]");

			/**********************************
			 * Dybasis_Cal
			 *********************************/
			int daybasis_Cur = 0;
			if (currency.equalsIgnoreCase("KES") || currency.equalsIgnoreCase("GBP")) {

				daybasis_Cur = 365;

			} else if (currency.equalsIgnoreCase("USD") || currency.equalsIgnoreCase("EUR")) {
				daybasis_Cur = 360;
			} else {
				if (!dailyBasis.equals("")) {
					intBasis_ps.setString(1, dailyBasis);
					intBasis_rs = intBasis_ps.executeQuery();
					if (intBasis_rs.next()) {
						try {
							daybasis_Cur = intBasis_rs.getInt("inteeeBasis");

						} catch (Exception e) {
							daybasis_Cur = 365;
						}
					} else {
						daybasis_Cur = 365;
					}
					intBasis_rs.close();
					intBasis_ps.clearParameters();
				}

			}
			intBasis_ps.close();

			System.out.println("Daybasis_Cur [" + daybasis_Cur + "]");

			if (Product.equalsIgnoreCase("AA.FIX.DEP.HIGHYIELD")) {
				System.out.println("Product is checking inside method:::::::::");
				Double tax_amount = 0.0;
				Double Interest_amount = 0.0;
				Double TotalInterest = 0.0;
				Double totaltax = 0.0;
				Double totalpay = 0.0;
				Double totalpayamt = 0.0;

				Calendar middle = Calendar.getInstance();

				Calendar startdate = Calendar.getInstance();
				startdate.setTime(term1);

				int m = 1;
				int daydiff1 = 0;
				String paymentfreqYear = getPaymentFreq("e1y e0m e0W e0D e0F");
				while (true) {

					if (m == 1) {

						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {

							startdate.add(Calendar.YEAR,
									Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));
							daydiff1 = dayBetween(start1_highyield, startdate);
							middle.setTime(startdate.getTime());

						}

					} else {
						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {

							startdate.add(Calendar.YEAR,
									Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));

							daydiff1 = dayBetween(middle, startdate);
							middle.setTime(startdate.getTime());

						}
					}

					if (middle.after(end1_highyield)) {

						if (paymentfreqYear.contains("Y") || paymentfreqYear.contains("y")) {
							middle.add(Calendar.YEAR,
									-Integer.parseInt(paymentfreqYear.replace("Y", "").replace("y", "")));
							daydiff1 = dayBetween(middle, end1_highyield);
							middle.add(Calendar.DAY_OF_YEAR, daydiff1);

						}

					}

					String firstDate1 = dateFormatter.format(middle.getTime());

					Calendar tmp = Calendar.getInstance();
					Interest_amount = getIntertest1(DepositAmount, efectiverate, daydiff1, tmp, daybasis_Cur);
					System.out.println("Interest_amount [" + Interest_amount + "]");
					tax_amount = gettax(Interest_amount);
					totalpay = gettotalpay(Interest_amount, tax_amount);

					TotalInterest += Interest_amount;
					totaltax += tax_amount;
					totalpayamt += totalpay;

					System.out.println(firstDate1 + "\t Days:" + daydiff1 + "\t Interst:"
							+ numberFormat
									.format((getIntertest1(DepositAmount, efectiverate, daydiff1, tmp, daybasis_Cur)))
							+ "\twtax: " + numberFormat.format(tax_amount) + "\ttotalPay: "
							+ numberFormat.format(totalpay));

					if (dateFormatter.format(middle.getTime()).equals(dateFormatter.format(end1_highyield.getTime()))) {

						break;
					}
					m++;
				}

				System.out.println("::::TOTAL_INTEREST:::" + numberFormat.format(TotalInterest));
				System.out.println(":::TOTAL::TAX::::" + numberFormat.format(totaltax));
				System.out.println("::TOTAL::PAYMENT:::::" + numberFormat.format(totalpayamt));
				DepoInterest = (TotalInterest);

				if (Double.isNaN(DepoInterest)) {
					DepoInterest = 0.0;
				}

				System.out.println("DepoInterest test1 [" + DepoInterest + "]");
				// Wtax = totaltax;

			} else {
				DepoInterest += (DepositAmount * efectiverate / 100 * number_of_diffdays / daybasis_Cur);

				if (Double.isNaN(DepoInterest)) {
					DepoInterest = 0.0;
				}

				System.out.println("After Interest: " + DepoInterest);
				System.out.println("Gross Interest: " + DepoInterest);
				// Wtax = DepoInterest * 15 / 100;
				// System.out.println("Wtax :" + Wtax);
				System.out.println("DepoInterest test2 [" + DepoInterest + "]");
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		}

		System.out.println("Final Interest: " + DepoInterest);
		return DepoInterest;
	}

	public static String getPaymentFreq(String freq) {
		String freqs = "1M";
		if (freq != null) {
			String pay[] = freq.split(" ");

			if ((pay[0].indexOf("0Y") == 1 || pay[0].indexOf("0y") == 1)
					&& (pay[1].indexOf("0M") == 1 || pay[1].indexOf("0m") == 1))
				freqs = "1M";
			else if (pay[0].indexOf("0Y") == 1 || pay[0].indexOf("0y") == 1)
				freqs = pay[1].substring(1, pay[1].length());
			else if (pay[1].indexOf("0M") == 1 || pay[1].indexOf("0m") == 1)
				freqs = pay[0].substring(1, pay[0].length());
		}
		return freqs;
	}

	public static double getIntertest1(final double priciple, final double rate, final double dayDifference,
			final Calendar startDate, int daybasis_Cur) {
		double inte = 0.0;
		// double inte1 = 0.0;
		// int daybasis;

		for (int i = 0; i < dayDifference; i++) {

			// daybasis = startDate.getActualMaximum(Calendar.DAY_OF_YEAR);
			inte += priciple * (rate / 100) * 1 / daybasis_Cur;

		}
		return (inte);
	}

	public static int dayBetween(final Calendar startDate, final Calendar endDate) throws SQLException {

		LocalDateTime loc_effectiveDate = getLocalDate(startDate);
		LocalDateTime loc_maturityDate = getLocalDate(endDate);
		int daydiff = (int) ChronoUnit.DAYS.between(loc_effectiveDate, loc_maturityDate);

		return daydiff;
	}

	public static double gettax(final double wtax) {
		// double inte = 0.0;
		double inte = 0.0;
		inte += (wtax * 15) / 100;

		return (inte);
	}

	public static double gettotalpay(final double interest, final double wtax) {
		// double inte = 0.0;
		double total_pay = 0.0;
		total_pay += interest - wtax;

		return total_pay;

	}

	public static int getMonthsDifference(final Calendar startDate, final Calendar endDate) {

		LocalDateTime loc_effectiveDate = getLocalDate(startDate);
		LocalDateTime loc_maturityDate = getLocalDate(endDate);
		int monthdiff = (int) ChronoUnit.MONTHS.between(loc_effectiveDate, loc_maturityDate);

		return monthdiff;
	}

	public static LocalDateTime getLocalDate(Calendar calendar) {

		return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
	}
}
