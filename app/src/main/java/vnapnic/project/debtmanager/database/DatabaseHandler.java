package vnapnic.project.debtmanager.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import vnapnic.project.debtmanager.model.Debt;
import vnapnic.project.debtmanager.model.Friend;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

@SuppressLint("SimpleDateFormat")
public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHandler.class.getName();

	public static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

	public static final String DB_NAME = "repay.db";
	public static final int 		DB_VERSION = 2;

	private static class Names {

		private static final String F_TABLENAME = "friends";
		// Friends table in this order:
		private static final String F_REPAYID = "repayID";
		private static final String F_LOOKUPURI = "lookupURI";
		private static final String F_NAME = "name";
		private static final String F_DEBT = "debt";

		private static final String D_TABLENAME = "debts";
		// Debt table in this order:
		private static final String D_DEBTID = "debtID";
		private static final String D_REPAYID = "repayID";
		private static final String D_DATE = "date";
		private static final String D_AMOUNT = "amount";
		private static final String D_DESCRIPTION = "description";
	}

	/**
	 * Finds or creates a SQLite database corresponding to the current user of the application
	 * @param context The current context
	 */
	public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	/**
	 * Generate an ID for entering into the database as a primary key
	 * @return String representation of the Repay ID
	 */
	public static String generateRepayID() {
		long rand = new SecureRandom().nextLong();
		return String.valueOf(rand);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create Friends table
		db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)",
				Names.F_TABLENAME, Names.F_REPAYID, Names.F_LOOKUPURI, Names.F_NAME, Names.F_DEBT));

		// Create Debts table
		db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, "
						+ "%s TEXT, %s TEXT, %s TEXT, FOREIGN KEY(%s) REFERENCES %s(%s))", Names.D_TABLENAME,
				Names.D_DEBTID, Names.D_REPAYID, Names.D_DATE, Names.D_AMOUNT,
				Names.D_DESCRIPTION, Names.D_REPAYID, Names.F_TABLENAME, Names.F_REPAYID));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion==1 && newVersion==2){
			// Change to include a debtID
			try{
				ArrayList<Debt> debts = new ArrayList<Debt>();
				Cursor c;
				// Get all current debts from database
				c = db.query(Names.D_TABLENAME, new String[]{Names.D_REPAYID, Names.D_DATE, Names.D_AMOUNT, Names.D_DESCRIPTION},
						null, null, null, null, null);
				c.moveToFirst();
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				do{
					// Entering 0 for debtID since it isn't put into the database
					debts.add(new Debt(0, c.getString(0), sdf.parse(c.getString(1)), new BigDecimal(c.getString(2)), c.getString(3)));
				} while (c.moveToNext());

				// Drop old table and create new one
				db.execSQL(String.format("ALTER TABLE %s RENAME TO oldDebts", Names.D_TABLENAME));
				db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL, "
								+ "%s TEXT, %s TEXT, %s TEXT, FOREIGN KEY(%s) REFERENCES %s(%s))", Names.D_TABLENAME,
						Names.D_DEBTID, Names.D_REPAYID, Names.D_DATE, Names.D_AMOUNT,
						Names.D_DESCRIPTION, Names.D_REPAYID, Names.F_TABLENAME, Names.F_REPAYID));

				// Add data back in new format
				ContentValues values;
				for(int i=0;i<=debts.size()-1;i++){
					values = new ContentValues();
					values.put(Names.D_REPAYID, debts.get(i).getRepayID());
					values.put(Names.D_AMOUNT, debts.get(i).getAmount().toString());
					values.put(Names.D_DATE, debts.get(i).getDate().toString());
					values.put(Names.D_DESCRIPTION, debts.get(i).getDescription());
					db.insert(Names.D_TABLENAME, null, values);
				}
			} catch (Exception e){
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return All friends stored in the database as ArrayList
	 * @throws android.database.SQLException
	 */
	public ArrayList<Friend> getAllFriends() throws SQLException, NullPointerException, CursorIndexOutOfBoundsException {
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Friend> friends = new ArrayList<Friend>();

		Cursor c = db.query(Names.F_TABLENAME, new String[]{Names.F_REPAYID, Names.F_LOOKUPURI, Names.F_NAME, Names.F_DEBT},
				null, null, null, null, null);

		if (c != null && c.getCount() > 0)
		{
			c.moveToFirst();

			do
			{
				friends.add(new Friend(c.getString(0), c.getString(1), c.getString(2), new BigDecimal(c.getString(3))));
			}
			while (c.moveToNext());
		}
		db.close();

		return friends;
	}

	/**
	 * Convenience method for knowing how many friend entries are in the database
	 * @return Number of friends in database
	 * @throws NullPointerException If no records are found
	 */
	public int getNumberOfPeople() throws NullPointerException {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(Names.F_TABLENAME,null,null,null,null,null,null);
		return c.getCount();
	}

	/**
	 * Convenience method for knowing how many debt entries are in the database
	 * @return Number of debts in database
	 * @throws NullPointerException If no records are found
	 */
	public int getNumberOfDebts() throws NullPointerException {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(Names.D_TABLENAME,null,null,null,null,null,null);
		return c.getCount();
	}

	/**
	 * Add a friend into the database. To get a RepayID, use generateRepayID()
	 * @param friend
	 * @throws android.database.SQLException
	 */
	public void addFriend(final Friend friend) throws SQLException {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Names.F_REPAYID, friend.getRepayID());
		try {
			values.put(Names.F_LOOKUPURI, friend.getLookupURI());
		} catch (NullPointerException e) {
			Log.i(TAG, "Added by name, Null for lookupUri");
			values.putNull(Names.F_LOOKUPURI);
		}
		values.put(Names.F_NAME, friend.getName());
		values.put(Names.F_DEBT, friend.getDebt().toString());
		db.insert(Names.F_TABLENAME, null, values);
		db.close();
	}

	/**
	 * Add a debt into the database, linked to a RepayID
	 * @param repayID
	 * @param amount
	 * @param description
	 * @throws android.database.SQLException
	 * @throws NullPointerException
	 */
	public void addDebt(final String repayID, final BigDecimal amount, String description)
			throws SQLException, NullPointerException
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(Names.D_REPAYID, repayID);
		values.put(Names.D_DATE, new Date().toString());
		values.put(Names.D_AMOUNT, amount.toString());
		values.put(Names.D_DESCRIPTION, description.replaceAll("[-+.^:,']",""));
		db.insert(Names.D_TABLENAME, null, values);
		db.close();
	}

	public void updateDebt(Debt debt) throws SQLException, NullPointerException {
		ContentValues values = new ContentValues();
		values.put(Names.D_AMOUNT, debt.getAmount().toString());
		values.put(Names.D_REPAYID, debt.getRepayID());
		values.put(Names.D_DESCRIPTION, debt.getDescription().replaceAll("[-+.^:,']",""));
		values.put(Names.D_DATE, debt.getDate().toString());
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(Names.D_TABLENAME, values, Names.D_DEBTID+"=?",
				new String[]{Integer.toString(debt.getDebtID())});
		db.close();
	}

	/**
	 * @param repayID
	 * @return List of all debts related to given Friend
	 * @throws android.database.SQLException
	 * @throws java.text.ParseException
	 * @throws android.database.CursorIndexOutOfBoundsException
	 */
	public ArrayList<Debt> getDebtsByRepayID(final String repayID) throws SQLException, ParseException,
			CursorIndexOutOfBoundsException, NullPointerException {
		ArrayList<Debt> debts = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(Names.D_TABLENAME, new String[]{Names.D_DEBTID, Names.D_DATE, Names.D_AMOUNT, Names.D_DESCRIPTION},
				Names.D_REPAYID+"=?", new String[]{repayID}, null, null, null);

		if(c != null && c.getCount() > 0){
			c.moveToFirst();
			debts = new ArrayList<Debt>();
			do{
				SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
				String description, dateString;
				Date date;
				// Try and catch errors to stop this failing silently
				try{
					dateString = c.getString(1);
					date = sdf.parse(dateString);
				} catch (Exception e){
					date = new Date();
				}
				try{
					description = c.getString(3);
				} catch (Exception e){
					description = "";
				}
				debts.add(new Debt(c.getInt(0), repayID, date, new BigDecimal(c.getString(2)), description));
			} while(c.moveToNext());
		}
		db.close();
		return debts;
	}

	/**
	 * Get information on friend by passing in their RepayID
	 * @param repayID
	 * @return Friend object representation of person
	 * @throws IndexOutOfBoundsException
	 * @throws android.database.SQLException
	 */
	public Friend getFriendByRepayID(String repayID) throws IndexOutOfBoundsException, SQLException, NullPointerException {
		Friend friend = null;
		Cursor c = null;
		SQLiteDatabase db = this.getReadableDatabase();
		c = db.query(Names.F_TABLENAME, new String[]{Names.F_REPAYID, Names.F_LOOKUPURI, Names.F_NAME, Names.F_DEBT},
				Names.F_REPAYID+"=?", new String[]{repayID}, null, null, null);
		c.moveToFirst();
		try {
			friend = new Friend(repayID, c.getString(1), c.getString(2), new BigDecimal(c.getString(3)));
		} catch (NullPointerException e) {
			Log.i(TAG, "No ContactURI present, passing null");
			friend = new Friend(repayID, null, c.getString(2), new BigDecimal(c.getString(3)));
		}
		db.close();
		return friend;
	}

	/**
	 * Replace friend data in database with that passed in here
	 * @param friend
	 */
	public void updateFriendRecord(Friend friend) throws SQLException, NullPointerException {
		ContentValues values = new ContentValues();
		values.put(Names.F_REPAYID, friend.getRepayID());
		if(friend.getLookupURI()!=null){
			values.put(Names.F_LOOKUPURI, friend.getLookupURI());
		}
		else{
			values.putNull(Names.F_LOOKUPURI);
		}
		values.put(Names.F_NAME, friend.getName());
		values.put(Names.F_DEBT, friend.getDebt().toString());
		SQLiteDatabase db = this.getWritableDatabase();
		try {
			db.update(Names.F_TABLENAME, values, Names.F_REPAYID+"=?",
					new String[]{friend.getRepayID()});
		} catch (SQLException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			throw e;
		} finally {
			db.close();
		}
	}

	/**
	 * Remove a person from both tables of the database
	 * @param repayID
	 * @return Number of rows in database removed
	 * @throws android.database.SQLException
	 * @throws NullPointerException
	 */
	public int removeFriend(String repayID) throws SQLException, NullPointerException {
		SQLiteDatabase db = this.getWritableDatabase();
		int count;
		count = db.delete(Names.F_TABLENAME, Names.F_REPAYID+"=?", new String[]{repayID});
		count += db.delete(Names.D_TABLENAME, Names.F_REPAYID+"=?", new String[]{repayID});
		db.close();
		return count;
	}

	/**
	 * Remove a singular debt from the database
	 * @throws android.database.SQLException
	 * @throws IndexOutOfBoundsException
	 * @throws NullPointerException
	 */
	public void removeDebt(int debtID) throws SQLException, IndexOutOfBoundsException,
			NullPointerException {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(Names.D_TABLENAME, Names.D_DEBTID+"=?",
				new String[]{Integer.toString(debtID)});
		db.close();
	}

	/**
	 * Lookup the most recent entry in the database
	 * @return Debt representation of most recent debt entered into database
	 * @throws java.text.ParseException
	 * @throws NullPointerException
	 * @throws android.database.sqlite.SQLiteException
	 * @throws android.database.CursorIndexOutOfBoundsException
	 */
	public Debt getMostRecentDebt() throws ParseException, NullPointerException, SQLiteException,
			CursorIndexOutOfBoundsException {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c;
		c = db.query(Names.D_TABLENAME, null, null, null, null, null, null);
		c.moveToLast();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		db.close();
		return new Debt(c.getInt(0), c.getString(1), sdf.parse(c.getString(2)),
				new BigDecimal(c.getString(2)), c.getString(3));
	}

	/**
	 * @param debtID
	 * @return The debt stored against the two given attributes
	 * @throws android.database.SQLException
	 * @throws IndexOutOfBoundsException
	 * @throws java.text.ParseException
	 */
	public Debt getDebtByIDs(int debtID) throws SQLException, IndexOutOfBoundsException, ParseException {
		Cursor c = null;
		Debt debt = null;
		SQLiteDatabase db = this.getReadableDatabase();
		c = db.query(Names.D_TABLENAME, new String[]{Names.D_DEBTID, Names.D_REPAYID, Names.D_DATE, Names.D_AMOUNT, Names.D_DESCRIPTION},
				Names.D_DEBTID+"=?", new String[]{Integer.toString(debtID)}, null, null, null);
		if(c!=null){
			c.moveToFirst();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			Date date;
			date = sdf.parse(c.getString(2));
			debt = new Debt(c.getInt(0), c.getString(1), date, new BigDecimal(c.getString(3)), c.getString(4));
		}
		return debt;
	}
}
