package vnapnic.project.debtmanager.adddebt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import vnapnic.project.debtmanager.database.DatabaseHandler;
import vnapnic.project.debtmanager.model.Debt;
import vnapnic.project.debtmanager.model.DebtBuilder;
import vnapnic.project.debtmanager.model.Friend;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public abstract class DebtActivity extends Activity
{
	public static final String FRIEND = "friend";
	public static final String DEBT = "debt";
	public static final String DEBT_REPAID_TEXT = "Repaid";
	private static final String DEBT_BUILDER = "builder";

	protected DatabaseHandler mDB;

	protected Friend mFriend;
	protected Debt mDebt;

	protected DebtBuilder mBuilder;

	protected boolean isEditing = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Do some instantiation here

		if (savedInstanceState != null && savedInstanceState.get(DEBT_BUILDER) != null)
		{
			mBuilder = (DebtBuilder)savedInstanceState.get(DEBT_BUILDER);
		}
		else
		{
			mBuilder = new DebtBuilder();

			if (getIntent().getExtras() != null && getIntent().getExtras().get(FRIEND) != null)
			{
				mFriend = (Friend)getIntent().getExtras().get(FRIEND);
				mBuilder.addSelectedFriend(mFriend);
			}

			if (getIntent().getExtras() != null && getIntent().getExtras().get(DEBT) != null)
			{
				mDebt = (Debt)getIntent().getExtras().get(DEBT);
				mBuilder.setAmount(mDebt.getAmount());
				mBuilder.setDescription(mDebt.getDescription());
				mBuilder.setDate(mDebt.getDate());
			}
		}

		mDB = new DatabaseHandler(this);
	}

	public DebtBuilder getDebtBuilder()
	{
		return mBuilder;
	}

	public DatabaseHandler getDBHandler()
	{
		return mDB;
	}

	public void save()
	{
		if (isEditing)
		{
			// Subtract the old amount
			mFriend.setDebt(mFriend.getDebt().subtract(mDebt.getAmount()));
			// Get the newly entered data
			mDebt.setAmount(mBuilder.getAmountToApply());
			mDebt.setDescription(mBuilder.getDescription());
			mDB.updateDebt(mDebt);
			// Add the new amount
			mFriend.setDebt(mFriend.getDebt().add(mDebt.getAmount()));
			mDB.updateFriendRecord(mFriend);
			finish();
		}
		else
		{
			// Add the debts into the DB
			for (Debt debt : mBuilder.getNewDebts())
			{
				mDB.addDebt(debt.getRepayID(), debt.getAmount(), debt.getDescription());
			}
			// Then update the friend objects
			for (Friend friend : mBuilder.getUpdatedFriends())
			{
				mDB.updateFriendRecord(friend);
			}
		}

		finish(); // Return to friend overview
	}

	public abstract void onNextButtonClick(View v);

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putSerializable(DEBT_BUILDER, mBuilder);
	}
}
