package vnapnic.project.debtmanager.frienddetails;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import vnapnic.project.debtmanager.ContactsContractHelper;
import vnapnic.project.debtmanager.SendMail;
import vnapnic.project.debtmanager.model.Friend;

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

public class ShareDialog extends Builder implements OnClickListener {

	private static final String TAG = ShareDialog.class.getName();

	private HashMap<String, String> mEmails, mPhoneNums;
	private Friend								mFriend;
	private Context mContext;

	public ShareDialog(Context context, Friend friend) {
		super(context);
		mContext = context;
		mFriend = friend;
		Log.d(TAG, "Getting emails from contacts");
		mEmails = ContactsContractHelper.getContactsEmailAddress(Uri.parse(mFriend.getLookupURI()).getLastPathSegment(), context);
		Log.d(TAG, "Getting phone numbers from contacts");
		mPhoneNums = ContactsContractHelper.getContactPhoneNumber(context, Uri.parse(mFriend.getLookupURI()).getLastPathSegment());
		String[] items = new String[]{"SMS", "Email"};
		setTitle("Select Share Method");
		setItems(items, this);
	}

	/* (non-Javadoc)
	 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
	 * It seems to work well with just presuming you want to use the first number;
	 * Android does a reverse lookup in the contacts book automatically
	 */
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(which==0){
			Log.i(TAG, "SMS Selected");
			if(mPhoneNums.size()>=1){
				SendMail.smsFriend(mContext, mPhoneNums.get(mPhoneNums.keySet().iterator().next()), mFriend.getDebt());
			} else {
				Toast.makeText(mContext, "No Numbers Found For " + mFriend.getName(), Toast.LENGTH_SHORT).show();
			}
		}
		else if(which==1){
			Log.i(TAG, "Email Selected");
			if(mEmails.size()>=1){
				SendMail.emailFriend(mContext, mFriend.getDebt(), mEmails.get(mEmails.keySet().iterator().next()));
			} else {
				Toast.makeText(mContext, "No Email Addresses Found For " + mFriend.getName(), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
