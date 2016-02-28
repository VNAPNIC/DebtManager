package vnapnic.project.debtmanager;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class ContactsContractHelper
{
	private static final String TAG = ContactsContractHelper.class.getName();

	private static int[] typesPhone = new int[]{ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN,
		ContactsContract.CommonDataKinds.Phone.TYPE_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_WORK};
	private static int[] typesEmail = new int[]{ContactsContract.CommonDataKinds.Phone.TYPE_HOME, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
		ContactsContract.CommonDataKinds.Phone.TYPE_WORK};

	/**
	 * @param c
	 * @param contactID
	 * @return Phone number, if available. The search order is: mobile, main, home, work.
	 */
	public static HashMap<String,String> getContactPhoneNumber(Context c, String contactID){
		HashMap<String,String> phoneNums = new HashMap<String,String>();
		for(int i=0;i<=typesPhone.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesPhone[i])};
			String phoneNum = queryContactForPhoneNum(c, whereArgs);
			if(phoneNum!=null){
				if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME==typesPhone[i]){
					phoneNums.put("Home", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==typesPhone[i]){
					phoneNums.put("Mobile", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==typesPhone[i]){
					phoneNums.put("Work", phoneNum);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MAIN==typesPhone[i]){
					phoneNums.put("Main", phoneNum);
				}
			}
		}
		return phoneNums;
	}

	public static boolean hasContactData(Context c, String contactID){
		boolean hasData = false;
		for(int i=0;i<=typesPhone.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesPhone[i])};
			String phoneNum = queryContactForPhoneNum(c, whereArgs);
			if(phoneNum!=null){
				hasData = true;
				break;
			}
		}
		if(!hasData){
			for(int i=0;i<=typesEmail.length-1;i++){
				String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesEmail[i])};
				String email = queryContactForEmail(c, whereArgs);
				if(email!=null){
					hasData = true;
					break;
				}
			}
		}
		return hasData;
	}

	private static String queryContactForPhoneNum(Context c, String[] whereArgs){
		String phoneNumber = null;
		Cursor cursor = c.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? and " + ContactsContract.CommonDataKinds.Phone.TYPE + " = ?",
				whereArgs, null);

		int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			phoneNumber = cursor.getString(phoneNumberIndex);
			cursor.close();
		}
		Log.i(TAG, "Returning phone number: " + phoneNumber);
		return phoneNumber;
	}


	/**
	 * Get the main email address of the contact
	 * @param contactID The last known ContactID of the contact
	 * @param c The context to run in
	 * @return String representation of their email address
	 * @throws android.database.CursorIndexOutOfBoundsException
	 */
	public static HashMap<String,String> getContactsEmailAddress(String contactID, Context c) throws CursorIndexOutOfBoundsException
	{
		/*
		 * For some shitting reason, using ContactsContract.CommonDataKinds.Phone works instead of Email?
		 * Leaving it anyway, might just be some stupid HTC Sense 5 bug
		 */
		HashMap<String,String> emails = new HashMap<String,String>();
		for(int i=0;i<=typesEmail.length-1;i++){
			String[] whereArgs = new String[] {String.valueOf(contactID), String.valueOf(typesEmail[i])};
			String email = queryContactForEmail(c, whereArgs);
			if(email!=null){
				if(ContactsContract.CommonDataKinds.Phone.TYPE_HOME==typesEmail[i]){
					emails.put("Home", email);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE==typesEmail[i]){
					emails.put("Other", email);
				}
				else if(ContactsContract.CommonDataKinds.Phone.TYPE_WORK==typesEmail[i]){
					emails.put("Work", email);
				}
			}
		}
		return emails;
	}

	private static String queryContactForEmail(Context c, String[] whereArgs)
	{
		String phoneNumber = null;
		Cursor cursor = c.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ? and " + ContactsContract.CommonDataKinds.Email.TYPE + " = ?",
				whereArgs, null);

		int phoneNumberIndex = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			phoneNumber = cursor.getString(phoneNumberIndex);
			cursor.close();
		}
		Log.i(TAG, "Returning email address: " + phoneNumber);
		return phoneNumber;
	}

	/**
	 * Lookup the name of a contact in the system contacts app
	 * @param c For {@link android.content.ContentResolver}
	 * @param lookupURI The LOOKUP_URI for the contact
	 * @return String reresentation for the contacts display name as set in the Contacts app
	 */
	public static String getNameForContact(Context c, String lookupURI)
	{
		String[] cols = {ContactsContract.Contacts.DISPLAY_NAME};
		Cursor cursor = c.getContentResolver().query(Uri.parse(lookupURI), cols, null, null, null);
		cursor.moveToFirst();

		String result = cursor.getString(0).replaceAll("[-+.^:,']","");
		cursor.close();

		return result;
	}

	public static String getLookupForContact(Context c, String lookupURI)
	{
		String[] cols = {ContactsContract.Contacts.LOOKUP_KEY};
		Cursor cursor = c.getContentResolver().query(Uri.parse(lookupURI), cols, null, null, null);
		cursor.moveToFirst();

		String result = cursor.getString(0);
		cursor.close();

		return result;
	}
}
