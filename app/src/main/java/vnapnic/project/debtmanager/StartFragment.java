package vnapnic.project.debtmanager;

import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vnapnic.project.debtmanager.database.DatabaseHandler;
import vnapnic.project.debtmanager.frienddetails.FriendActivity;
import vnapnic.project.debtmanager.model.Debt;
import vnapnic.project.debtmanager.model.Friend;
import vnapnic.project.debtmanager.settings.SettingsFragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class StartFragment extends Fragment implements OnItemClickListener
{

	public static final String TAG = StartFragment.class.getName();

	private GridView mGrid;
	private TextView mEmptyState;
	private StartFragmentAdapter mAdapter;
	private ProgressBar mProgressBar;
	private int mListItem = R.layout.fragment_start_friendslist_item, mSortOrder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_start, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		mGrid = (GridView)getView().findViewById(R.id.fragment_start_friendsgrid);
		mEmptyState = (TextView)getView().findViewById(R.id.fragment_start_nofriendsadded);
		mProgressBar = (ProgressBar)getView().findViewById(R.id.fragment_start_progress);
		mProgressBar.setVisibility(ProgressBar.GONE);
		mGrid.setOnItemClickListener(this); // THE GRID. A DIGITAL FRONTIER.
		mSortOrder = SettingsFragment.getSortOrder(getActivity());
	}

	@Override
	public void onResume()
	{
		super.onResume();
		updateList();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case R.id.action_total:
				showTotalDialog();
				return true;

			case R.id.action_recalculateTotals:
				new RecalculateTotalDebts().execute(((MainActivity) getActivity()).getDB());

			default:
				return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inf)
	{
		super.onCreateOptionsMenu(menu, inf);
		inf.inflate(R.menu.friendslist, menu);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// Retrieve Friend object and continue with navigation
		Friend selectedFriend = (Friend)arg1.getTag();
		Intent overview = new Intent(getActivity(), FriendActivity.class);
		overview.putExtra(FriendActivity.FRIEND, selectedFriend);
		startActivity(overview);
	}

	public void updateList()
	{
		if (((MainActivity)getActivity()).getFriends() == null || ((MainActivity)getActivity()).getFriends().size() == 0)
		{
			mGrid.setVisibility(View.GONE);
			mEmptyState.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(ProgressBar.GONE);
		}
		else
		{
			mGrid.setVisibility(View.VISIBLE);
			mEmptyState.setVisibility(View.GONE);
			mProgressBar.setVisibility(ProgressBar.GONE);

			mAdapter = new StartFragmentAdapter(getActivity(), mListItem, ((MainActivity)getActivity()).getFriends());
			mGrid.setAdapter(mAdapter);
		}
	}

	private BigDecimal calculateTotalDebt()
	{
		if (((MainActivity)getActivity()).getFriends() != null)
		{
			BigDecimal total = new BigDecimal("0");
			for (Friend friend : ((MainActivity)getActivity()).getFriends())
			{
				total = total.add(friend.getDebt());
			}
			return total;
		}
		else
		{
			return new BigDecimal("0");
		}
	}

	public void showTotalDialog()
	{
		View v = LayoutInflater.from(getActivity()).inflate(R.layout.total_dialog, null, false);
		BigDecimal totalAmount = calculateTotalDebt();

		if (totalAmount.compareTo(BigDecimal.ZERO) == 1)
		{
			((TextView)v.findViewById(R.id.title)).setText(R.string.youre_owed);
			((TextView)v.findViewById(R.id.amount)).setText(SettingsFragment.getCurrencySymbol(getActivity()) + calculateTotalDebt().toString());
		}
		else if (totalAmount.compareTo(BigDecimal.ZERO) == 0)
		{
			((TextView)v.findViewById(R.id.title)).setText(R.string.even_debt);
			((TextView)v.findViewById(R.id.amount)).setText(SettingsFragment.getCurrencySymbol(getActivity()) + calculateTotalDebt().toString());
		}
		else if (totalAmount.compareTo(BigDecimal.ZERO) == -1)
		{
			((TextView)v.findViewById(R.id.title)).setText(R.string.i_owe);
			((TextView)v.findViewById(R.id.amount)).setText(SettingsFragment.getCurrencySymbol(getActivity()) + calculateTotalDebt().negate().toString());
		}

		new Builder(getActivity()).setView(v).setPositiveButton(R.string.close, null).show();
	}

	private class RecalculateTotalDebts extends AsyncTask<DatabaseHandler, Integer, ArrayList<Friend>>
	{

		@Override
		protected void onPreExecute()
		{
			mGrid.setAdapter(null);
			mGrid.setVisibility(ListView.GONE);
			mEmptyState.setVisibility(RelativeLayout.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);
		}

		private BigDecimal totalAllDebts(ArrayList<Debt> debts)
		{
			BigDecimal amount = new BigDecimal("0");
			if (debts != null && debts.size() > 0)
			{
				for (int i = 0; i <= debts.size() - 1; i++)
				{
					amount = amount.add(debts.get(i).getAmount());
				}
			}
			return amount;
		}

		@Override
		protected ArrayList<Friend> doInBackground(DatabaseHandler... params)
		{
			try
			{
				ArrayList<Friend> friends = params[0].getAllFriends();
				if (friends != null)
				{
					for (int i = 0; i <= friends.size() - 1; i++)
					{
						BigDecimal newAmount;
						try
						{
							newAmount = totalAllDebts(params[0].getDebtsByRepayID(friends.get(i).getRepayID()));
						}
						catch (Exception e)
						{
							e.printStackTrace();
							newAmount = new BigDecimal("0");
						}
						try
						{
							friends.get(i).setDebt(newAmount);
							params[0].updateFriendRecord(friends.get(i));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}

					Collections.sort(friends);
					if (mSortOrder == SettingsFragment.SORTORDER_OWETHEM)
					{
						Collections.reverse(friends);
					}
					return friends;
				}
			}
			catch (CursorIndexOutOfBoundsException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<Friend> result)
		{
			if (result != null && result.size() > 0)
			{
				mAdapter = new StartFragmentAdapter(getActivity(), mListItem, result);
				mGrid.setVisibility(ListView.VISIBLE);
				mGrid.setAdapter(mAdapter);
			}
			else
			{
				mEmptyState.setVisibility(RelativeLayout.VISIBLE);
			}

			mProgressBar.setVisibility(ProgressBar.GONE);
		}
	}
}
