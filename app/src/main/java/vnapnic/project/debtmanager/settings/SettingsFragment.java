package vnapnic.project.debtmanager.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.SendMail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class SettingsFragment extends PreferenceFragment
{
	public static final int DEBTHISTORY_GREEN_RED = 1;
	public static final int DEBTHISTORY_GREEN_BLUE = 2;
	public static final int SORTORDER_OWEME = 1;
	public static final int SORTORDER_OWETHEM = 2;
	private static final String PREF_KEY_CURRENCY = "currencies_list";
	private static final String PREF_KEY_DATEFORMAT = "dateformat_list";
	private static final String PREF_KEY_DEBTHISTORY_COLOURS = "debthistoryColours";
	private static final String PREF_KEY_SORTORDER = "sortOrder";

	/**
	 * @param c The Context to operate in
	 * @return The currency symbol based on the preference set by the user. If not preference has been set, the GBP symbol will be returned
	 */
	public static String getCurrencySymbol(Context c)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		String currencyPref = prefs.getString(PREF_KEY_CURRENCY, Currency.getInstance(Locale.getDefault()).getSymbol());
		if (currencyPref.equals("1"))
		{
			Currency currency = Currency.getInstance(Locale.UK);
			return currency.getSymbol();
		}
		else if (currencyPref.equals("2"))
		{
			Currency currency = Currency.getInstance(Locale.US);
			return currency.getSymbol();
		}
		else if (currencyPref.equals("3"))
		{
			Currency currency = Currency.getInstance(Locale.GERMANY);
			return currency.getSymbol();
		}
		else if (currencyPref.equals("4"))
		{
			Currency currency = Currency.getInstance(Locale.getDefault());
			return currency.getSymbol();
		}
		return currencyPref;
	}

	/**
	 * @param c    The Context to operate in
	 * @param date The date as returned from the database
	 * @return The date, formatted as the user preference specifies
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormattedDate(Context c, Date date)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// Add 1 to the month because it returns one less than it should
		String dateString = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
		String datePref = prefs.getString(PREF_KEY_DATEFORMAT, "4");

		if (datePref.equals("1"))
		{
			return dateString;
		}
		else if (datePref.equals("2"))
		{
			return (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.YEAR);
		}
		else if (datePref.equals("3"))
		{
			SimpleDateFormat format = new SimpleDateFormat("EEE d MMM yyyy");
			return format.format(date);
		}
		else if (datePref.equals("4")) // Device default
		{
			return DateFormat.getDateFormat(c).format(date);
		}
		else if (datePref.equals("5"))
		{
			return (cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	/**
	 * @param amount The amount returned from the database
	 * @return The amount specified, in a consistent format
	 */
	public static String getFormattedAmount(BigDecimal amount)
	{
		String str;
		str = amount.setScale(2, RoundingMode.CEILING).toString();
		return str;
	}

	/**
	 * Get preference on colours used in DebtHistoryFragment
	 *
	 * @param c Context to run in
	 * @return Integer representation of preference. Use constants to reveal setting
	 */
	public static int getDebtHistoryColourPreference(Context c)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return Integer.parseInt(prefs.getString(PREF_KEY_DEBTHISTORY_COLOURS, "1"));
	}

	/**
	 * @param c
	 * @return The order repesented by an int
	 */
	public static int getSortOrder(Context c)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

		return Integer.parseInt(prefs.getString(PREF_KEY_SORTORDER, "1"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.pref_general);
		// Response for when the feedback option is used
		findPreference("feedback").setOnPreferenceClickListener(new OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				SendMail.sendFeedback(getActivity());
				return true;
			}
		});
	}
}
