package vnapnic.project.debtmanager.adddebt;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import vnapnic.project.debtmanager.R;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class EnterAmountFragment extends DebtFragment implements OnClickListener
{

	private static final String TAG = EnterAmountFragment.class.getName();
	private static final String NUMBER_DEC = "numDec", NUMBER_INT = "numInt"; // For Saved State
	private TextView mAmountDisplay;
	private String mAmountInt = "0", mAmountDec = "00"; //Instantiate it as this
	private Button mDoneBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_enterdebtamount, container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().getActionBar().setSubtitle(R.string.whats_debt_amount);
		getActivity().getActionBar().setTitle(R.string.enter_amount);

		if (savedInstanceState != null && savedInstanceState.getString(NUMBER_DEC) != null)
		{
			mAmountDec = savedInstanceState.getString(NUMBER_DEC);
		}
		else if (((DebtActivity)getActivity()).getDebtBuilder().getAmount() != null)
		{
			String[] string = ((DebtActivity)getActivity()).getDebtBuilder().getAmount().toString().split(".");
			if (string.length > 1)
			{
				mAmountDec = string[1];
			}
		}

		if (savedInstanceState != null && savedInstanceState.getString(NUMBER_INT) != null)
		{
			mAmountInt = savedInstanceState.getString(NUMBER_INT);
		}
		else if (((DebtActivity)getActivity()).getDebtBuilder().getAmount() != null)
		{
			String[] string = ((DebtActivity)getActivity()).getDebtBuilder().getAmount().toString().split(".");
			if (string.length > 0)
			{
				mAmountInt = string[0];
			}
		}

		mAmountDisplay = (TextView)getView().findViewById(R.id.fragment_enterdebtamount_amount);
		mDoneBtn = (Button)getView().findViewById(R.id.fragment_enterdebtamount_donebtn);
		mAmountDisplay.setText(mAmountInt + "." + mAmountDec);
		// Set OnClickListeners for all buttons on the view (Other than done btn)
		getView().findViewById(R.id.fragment_enterdebtamount_backspace).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_one).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_two).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_three).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_four).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_five).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_six).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_seven).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_eight).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_nine).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_zero).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_doublezero).setOnClickListener(this);
		getView().findViewById(R.id.fragment_enterdebtamount_clear).setOnClickListener(this);
		if (getActivity().getClass() == RepayDebtActivity.class)
		{
			mDoneBtn.setText(getActivity().getResources().getString(R.string.done)); // Give it a more appropriate message
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString(NUMBER_INT, mAmountInt);
		outState.putString(NUMBER_DEC, mAmountDec);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.fragment_enterdebtamount_backspace:
				if (mAmountInt.length() <= 1)
				{
					mAmountDec = mAmountInt.substring(mAmountInt.length() - 1, mAmountInt.length()) + mAmountDec.substring(0, 1);
					mAmountInt = "0";
				}
				else
				{
					mAmountDec = mAmountInt.substring(mAmountInt.length() - 1) + mAmountDec.substring(0, 1);
					mAmountInt = mAmountInt.substring(0, mAmountInt.length() - 1);
				}
				mAmountDisplay.setText(mAmountInt + "." + mAmountDec);
				break;

			case R.id.fragment_enterdebtamount_one:
				insertNumber("1");
				break;

			case R.id.fragment_enterdebtamount_two:
				insertNumber("2");
				break;

			case R.id.fragment_enterdebtamount_three:
				insertNumber("3");
				break;

			case R.id.fragment_enterdebtamount_four:
				insertNumber("4");
				break;

			case R.id.fragment_enterdebtamount_five:
				insertNumber("5");
				break;

			case R.id.fragment_enterdebtamount_six:
				insertNumber("6");
				break;

			case R.id.fragment_enterdebtamount_seven:
				insertNumber("7");
				break;

			case R.id.fragment_enterdebtamount_eight:
				insertNumber("8");
				break;

			case R.id.fragment_enterdebtamount_nine:
				insertNumber("9");
				break;

			case R.id.fragment_enterdebtamount_clear:
				mAmountDec = "00";
				mAmountInt = "0";
				mAmountDisplay.setText(mAmountInt + "." + mAmountDec);
				break;

			case R.id.fragment_enterdebtamount_doublezero:
				insertNumber("0");
				insertNumber("0");
				break;

			case R.id.fragment_enterdebtamount_zero:
				insertNumber("0");
				break;
		}
	}

	private void insertNumber(String number)
	{
		if (mAmountInt.length() < 9)
		{
			String numberToCarryOver = mAmountDec.substring(0, 1);
			mAmountDec = mAmountDec.substring(1, 2) + number;
			if (numberToCarryOver.equals("0") && mAmountInt.equals("0"))
			{
				// Do nothing
			}
			else
			{
				mAmountInt += numberToCarryOver;
			}

			// Get rid of heading 0's
			if (mAmountInt.length() > 1 && mAmountInt.substring(0, 1).equals("0"))
			{
				mAmountInt = mAmountInt.substring(1);
			}
		}
		else
		{
			Toast.makeText(getActivity(), "Too many digits", Toast.LENGTH_SHORT).show();
		}
		mAmountDisplay.setText(mAmountInt + "." + mAmountDec);
	}

	/**
	 * @return BigDecimal representation of current number entered by user
	 */
	public BigDecimal getAmount()
	{
		return new BigDecimal(mAmountInt + "." + mAmountDec);
	}

	public void setAmount(String amount)
	{
		try
		{
			String[] splitAmount = amount.split("\\.");
			mAmountInt = splitAmount[0];
			mAmountDec = splitAmount[1];
			Log.i(TAG, "Amount set to " + amount);
			mAmountDisplay.setText(mAmountInt + "." + mAmountDec);
		}
		catch (Exception e)
		{
			Log.i(TAG, "No data. Using 0.00");
		}
	}

	@Override
	public void saveFields()
	{
		((DebtActivity)getActivity()).getDebtBuilder().setAmount(getAmount());
	}
}
