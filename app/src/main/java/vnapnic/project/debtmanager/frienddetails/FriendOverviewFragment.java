package vnapnic.project.debtmanager.frienddetails;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import vnapnic.project.debtmanager.Application;
import vnapnic.project.debtmanager.ContactsContractHelper;
import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.model.Friend;
import vnapnic.project.debtmanager.settings.SettingsFragment;
import vnapnic.project.debtmanager.view.RoundedImageView;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class FriendOverviewFragment extends FriendFragment implements OnClickListener
{
	private RoundedImageView mFriendPic;
	private TextView mTotalOwed, mTotalOwedPrefix;
	private Button mShareBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_frienddetails, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mShareBtn = (Button) getView().findViewById(R.id.share);
		mFriendPic = (RoundedImageView) getView().findViewById(R.id.friend_image);
		mTotalOwed = (TextView) getView().findViewById(R.id.amount);
		mTotalOwedPrefix = (TextView) getView().findViewById(R.id.owe_status);
		mShareBtn.setOnClickListener(this);

		// Animate the UI into view
		mFriendPic.setScaleX(0f);
		mFriendPic.setScaleY(0f);
		ViewPropertyAnimator animator = mFriendPic.animate();
		animator.scaleX(1f);
		animator.scaleY(1f);
		animator.setDuration(500);
		animator.start();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.share:
				if (((FriendActivity) getActivity()).getFriend().getDebt().compareTo(BigDecimal.ZERO) != 0)
				{
					AlertDialog.Builder shareDialog = new ShareDialog(getActivity(), ((FriendActivity) getActivity()).getFriend());
					shareDialog.show();
				}
				else
				{
					Toast.makeText(getActivity(), "There's no debt between you", Toast.LENGTH_SHORT).show(); // TODO Localise
				}
				break;
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		onFriendUpdated(((FriendActivity) getActivity()).getFriend());
	}

	@Override
	public void onFriendUpdated(Friend friend)
	{
		ImageLoader.getInstance().displayImage(friend.getLookupURI(), mFriendPic, Application.getImageOptions());

		if (friend.getDebt().compareTo(BigDecimal.ZERO) == 0)
		{
			mTotalOwedPrefix.setText(R.string.even_debt);
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + "0");
			mFriendPic.setOuterColor(mTheyOweMeColour);
		}
		else if (friend.getDebt().compareTo(BigDecimal.ZERO) < 0)
		{
			mTotalOwedPrefix.setText(R.string.i_owe);
			String amount = SettingsFragment.getFormattedAmount(friend.getDebt().negate());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + amount);
			mFriendPic.setOuterColor(mIOweThemColour);
		}
		else if (friend.getDebt().compareTo(BigDecimal.ZERO) > 0)
		{
			mTotalOwedPrefix.setText(R.string.they_owe);
			String amount = SettingsFragment.getFormattedAmount(friend.getDebt());
			mTotalOwed.setText(SettingsFragment.getCurrencySymbol(getActivity()) + amount);
			mFriendPic.setOuterColor(mTheyOweMeColour);
		}

		if (friend.getLookupURI() != null)
		{
			if (!ContactsContractHelper.hasContactData(getActivity(), Uri.parse(friend.getLookupURI()).getLastPathSegment()))
			{
				mShareBtn.setEnabled(false);
			}
		}
		else
		{
			mShareBtn.setEnabled(false);
		}
	}
}
