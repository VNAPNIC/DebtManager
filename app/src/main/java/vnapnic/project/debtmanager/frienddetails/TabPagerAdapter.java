package vnapnic.project.debtmanager.frienddetails;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {

	private FriendFragment[] mFragments;

	public TabPagerAdapter(FragmentManager fm, FriendFragment[] fragments)
	{
    	super(fm);
		mFragments = fragments;
    }

    @Override
    public int getCount()
	{
        return mFragments.length;
    }

    @Override
    public FriendFragment getItem(int position)
	{
        return mFragments[position];
    }
}