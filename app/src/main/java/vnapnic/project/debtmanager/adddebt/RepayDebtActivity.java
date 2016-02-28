package vnapnic.project.debtmanager.adddebt;

import android.os.Bundle;
import android.view.View;

import vnapnic.project.debtmanager.R;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 * <p/>
 * This class will only show the EnterAmountFragment so that
 * I can get the partial amount the person has repaid. Calling
 * the other fragment is unnecessary.
 */

public class RepayDebtActivity extends DebtActivity
{
	private int mFrameId = R.id.activity_adddebt_framelayout;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_adddebt);

		mBuilder.setDescription(DEBT_REPAID_TEXT);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(true);

		getFragmentManager().beginTransaction().replace(mFrameId, new EnterAmountFragment()).commit();
	}

	@Override
	public void onNextButtonClick(View v)
	{
		switch (v.getId())
		{
			case R.id.fragment_enterdebtamount_donebtn:
				((DebtFragment)getFragmentManager().findFragmentById(mFrameId)).saveFields();
				save();
				break;

			default:
				break;
		}
	}

	@Override
	public void save()
	{
		if (mBuilder.getSelectedFriends().get(0).getDebt().compareTo(BigDecimal.ZERO) > 0)
		{
			mBuilder.setInDebtToMe(false); // Negate because I don't want add with a negative
		}
		super.save();
	}
}
