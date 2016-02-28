package vnapnic.project.debtmanager.frienddetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.model.Debt;
import vnapnic.project.debtmanager.settings.SettingsFragment;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 * Adapter for displaying the history of debts
 */

public class FriendHistoryAdapter extends ArrayAdapter<Debt>
{
	private int layoutId;
	private ArrayList<Debt> debts;
	private Context context;

	public FriendHistoryAdapter(Context context, int layoutId, ArrayList<Debt> debts) {
		super(context, layoutId, debts);
		this.layoutId = layoutId;
		this.debts = debts;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View v = convertView;
		if(v == null)
		{
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutId, null);
		}
		Debt debt = debts.get(position);

		if(debt!=null)
		{
			v.setTag(debt);
			TextView description = (TextView)v.findViewById(R.id.fragment_debthistory_list_description);
			TextView amount = (TextView)v.findViewById(R.id.fragment_debthistory_list_amount);
			TextView dateTxt = (TextView)v.findViewById(R.id.fragment_debthistory_list_date);
			ImageView icon = (ImageView)v.findViewById(R.id.fragment_debthistory_list_icon);

			if (debt.getAmount().compareTo(BigDecimal.ZERO)<0)
			{
				if(SettingsFragment.getDebtHistoryColourPreference(context)==SettingsFragment.DEBTHISTORY_GREEN_BLUE)
				{
					icon.setImageResource(R.drawable.debt_ind_blue);
				}
				else
				{
					icon.setImageResource(R.drawable.debt_ind_red);
				}
				//debt.setAmount(debt.getAmount().negate()); // For it to show up without the minus in front of it **Makes Green on recycle. Substr instead**
				StringBuilder sb = new StringBuilder(SettingsFragment.getCurrencySymbol(context)+SettingsFragment.getFormattedAmount(debt.getAmount()));
				sb.deleteCharAt(1);
				amount.setText(sb.toString());
			}
			else if (debt.getAmount().compareTo(BigDecimal.ZERO)>=0)
			{
				amount.setText(SettingsFragment.getCurrencySymbol(context)+SettingsFragment.getFormattedAmount(debt.getAmount()));
				icon.setImageResource(R.drawable.debt_ind_green);
			}
			description.setText(debt.getDescription());
			if(debt.getDescription().equals(""))
			{
				description.setText("No Description");
			}
			dateTxt.setText(SettingsFragment.getFormattedDate(context, debt.getDate()));
		}
		return v;
	}
}
