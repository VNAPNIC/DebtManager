package vnapnic.project.debtmanager.frienddetails;

import android.app.Fragment;
import android.os.Bundle;

import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.model.Friend;
import vnapnic.project.debtmanager.settings.SettingsFragment;

/**
 * Created by Matt Allen
 * http://mattallensoftware.co.uk
 * mattallen092@gmail.co.uk
 * <p/>
 * 10/08/2014.
 */
public abstract class FriendFragment extends Fragment implements OnFriendUpdatedListener
{
	protected int		mTheyOweMeColour, mIOweThemColour;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		if(SettingsFragment.getDebtHistoryColourPreference(getActivity())==SettingsFragment.DEBTHISTORY_GREEN_RED){
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.red_debt);
		} else {
			mTheyOweMeColour = getActivity().getResources().getColor(R.color.green_debt);
			mIOweThemColour = getActivity().getResources().getColor(R.color.blue_debt);
		}
	}

	public abstract void onFriendUpdated(Friend friend);
}
