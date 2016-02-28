package vnapnic.project.debtmanager.adddebt;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import vnapnic.project.debtmanager.Application;
import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.settings.SettingsFragment;
import vnapnic.project.debtmanager.view.RoundedImageView;

import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class DebtSummaryFragment extends DebtFragment implements OnClickListener, OnCheckedChangeListener
{
	private RoundedImageView mHeaderPic, mHeaderPic2, mHeaderPic3;
	private TextView mNamesTxt, mAmountTxt, mTheyOweMe, mIOweThem, mOverflowText;
	private EditText mDescription;
	private CheckBox mSplitEvenly, mInclMe;
	private int mTheyOweMeColour, mIOweThemColour, mNeutralColor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		return inflater.inflate(R.layout.fragment_debtsummary, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		getActivity().getActionBar().setTitle(R.string.summary);
		getActivity().getActionBar().setSubtitle(null);

		mDescription = (EditText)getView().findViewById(R.id.description);
		mSplitEvenly = (CheckBox)getView().findViewById(R.id.split_amount);
		mIOweThem = (TextView)getView().findViewById(R.id.i_owe_them);
		mTheyOweMe = (TextView)getView().findViewById(R.id.they_owe_me);
		mAmountTxt = (TextView)getView().findViewById(R.id.amount);
		mHeaderPic = (RoundedImageView)getView().findViewById(R.id.header_pic);
		mHeaderPic2 = (RoundedImageView)getView().findViewById(R.id.header_pic2);
		mHeaderPic3 = (RoundedImageView)getView().findViewById(R.id.header_pic3);
		mNamesTxt = (TextView)getView().findViewById(R.id.header_names);
		mInclMe = (CheckBox)getView().findViewById(R.id.incl_me);
		mOverflowText = (TextView)getView().findViewById(R.id.overflow_text);

		mHeaderPic.setOuterColor(Color.parseColor("#DDDDDD"));
		mHeaderPic2.setOuterColor(Color.parseColor("#DDDDDD"));
		mHeaderPic3.setOuterColor(Color.parseColor("#DDDDDD"));
		mInclMe.setVisibility(CheckBox.INVISIBLE);
		mSplitEvenly.setOnCheckedChangeListener(this);
		mInclMe.setOnCheckedChangeListener(this);
		mTheyOweMe.setOnClickListener(this);
		mIOweThem.setOnClickListener(this);

		if (SettingsFragment.getDebtHistoryColourPreference(getActivity()) == SettingsFragment.DEBTHISTORY_GREEN_RED)
		{
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.darkred_debt);
		}
		else
		{
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.blue_debt);
		}
		mNeutralColor = getActivity().getResources().getColor(R.color.main_background_slightlyDarker);

		int numberOfPeople = ((DebtActivity)getActivity()).getDebtBuilder().getSelectedFriends().size();
		if (numberOfPeople < 2)
		{
			mSplitEvenly.setVisibility(CheckBox.INVISIBLE);
		}
		if (mNamesTxt != null)
		{
			mNamesTxt.setText(((DebtActivity)getActivity()).getDebtBuilder().getNamesList(false).trim());
		}

		// Set images for the contact photos
		ArrayList<String> availableImages = ((DebtActivity)getActivity()).getDebtBuilder().getAvailableImageUris();

		try
		{
			ImageLoader.getInstance().displayImage(availableImages.get(0), mHeaderPic, Application.getImageOptions());
		}
		catch (IndexOutOfBoundsException e)
		{
			mHeaderPic.setImageResource(R.drawable.friend_image_light);
		}
		if (numberOfPeople >= 2)
		{
			mHeaderPic2.setVisibility(View.VISIBLE);
			try
			{
				ImageLoader.getInstance().displayImage(availableImages.get(1), mHeaderPic2, Application.getImageOptions());
			}
			catch (IndexOutOfBoundsException e)
			{
				mHeaderPic2.setImageResource(R.drawable.friend_image_light);
			}
		}
		if (numberOfPeople >= 3)
		{
			mHeaderPic3.setVisibility(View.VISIBLE);
			try
			{
				ImageLoader.getInstance().displayImage(availableImages.get(2), mHeaderPic3, Application.getImageOptions());
			}
			catch (IndexOutOfBoundsException e)
			{
				mHeaderPic3.setImageResource(R.drawable.friend_image_light);
			}
		}
		if (numberOfPeople > 3)
		{
			mOverflowText.setVisibility(View.VISIBLE);
			mOverflowText.setText("+" + Integer.toString(numberOfPeople - 3) + "\nmore");
		}

		mAmountTxt.setText(SettingsFragment.getCurrencySymbol(getActivity()) + ((DebtActivity)getActivity()).getDebtBuilder().getAmount().toString());
		setOweStatusColour(((DebtActivity)getActivity()).getDebtBuilder().isInDebtToMe());
	}

	private void setOweStatusColour(boolean isInDebtToMe)
	{
		if (isInDebtToMe)
		{
			mTheyOweMe.setBackgroundColor(mTheyOweMeColour);
			mIOweThem.setBackgroundColor(mNeutralColor);
		}
		else
		{
			mTheyOweMe.setBackgroundColor(mNeutralColor);
			mIOweThem.setBackgroundColor(mIOweThemColour);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.i_owe_them:
				((DebtActivity)getActivity()).getDebtBuilder().setInDebtToMe(false);
				setOweStatusColour(false);
				break;

			case R.id.they_owe_me:
				((DebtActivity)getActivity()).getDebtBuilder().setInDebtToMe(true);
				setOweStatusColour(true);
				break;

			default:
				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.getId() == R.id.split_amount)
		{
			((DebtActivity)getActivity()).getDebtBuilder().setDistributedEvenly(isChecked);
			if (isChecked)
			{
				mInclMe.setVisibility(CheckBox.VISIBLE);
			}
			else
			{
				mInclMe.setVisibility(CheckBox.INVISIBLE);
			}
		}
		else if (buttonView.getId() == R.id.incl_me)
		{
			((DebtActivity)getActivity()).getDebtBuilder().setIncludingMe(isChecked);
		}
	}

	@Override
	public void saveFields()
	{
		((DebtActivity)getActivity()).getDebtBuilder().setDescription(mDescription.getText().toString());
	}
}
