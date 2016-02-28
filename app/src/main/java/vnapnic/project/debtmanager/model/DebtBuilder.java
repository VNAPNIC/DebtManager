package vnapnic.project.debtmanager.model;

import android.text.TextUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Matt Allen
 * http://mattallensoftware.co.uk
 * mattallen092@gmail.co.uk
 * <p/>
 * 07/08/2014.
 */
public class DebtBuilder implements Serializable
{
	private String mDescription;
	private boolean owesMe = true, distributeEvenly = false, includingMe = false;
	private ArrayList<Friend> mSelectedFriends;
	private BigDecimal mAmount;
	private Date mDate;

	public DebtBuilder()
	{
		mSelectedFriends = new ArrayList<Friend>();
	}

	public String getDescription()
	{
		return mDescription;
	}

	public void setDescription(String description)
	{
		mDescription = description;
	}

	public boolean isInDebtToMe() {
		return owesMe;
	}

	public void setInDebtToMe(boolean owesMe) {
		this.owesMe = owesMe;
	}

	public boolean isDistributedEvenly() {
		return distributeEvenly;
	}

	public void setDistributedEvenly(boolean distributeEvenly) {
		this.distributeEvenly = distributeEvenly;
	}

	public Date getDate()
	{
		if (mDate == null) mDate = new Date();
		return mDate;
	}

	public void setDate(Date date)
	{
		mDate = date;
	}

	public boolean isIncludingMe() {
		return includingMe;
	}

	public void setIncludingMe(boolean includingMe) {
		this.includingMe = includingMe;
	}

	public ArrayList<Friend> getSelectedFriends() {
		return mSelectedFriends;
	}

	public void setSelectedFriends(ArrayList<Friend> selectedFriends) {
		this.mSelectedFriends = selectedFriends;
	}

	public void addSelectedFriend(Friend friend)
	{
		if (!mSelectedFriends.contains(friend)) mSelectedFriends.add(friend);
	}

	public void removeSelectedFriend(Friend friend)
	{
		mSelectedFriends.remove(friend);
	}

	public BigDecimal getAmount() {
		return mAmount;
	}

	public void setAmount(BigDecimal amount) {
		this.mAmount = amount;
	}

	public ArrayList<String> getAvailableImageUris()
	{
		if (mSelectedFriends != null && mSelectedFriends.size() > 0)
		{
			ArrayList<String> uris = new ArrayList<String>();
			for (Friend friend : mSelectedFriends)
			{
				if (!TextUtils.isEmpty(friend.getLookupURI()))
				{
					uris.add(friend.getLookupURI());
				}
			}
			return uris;
		}
		return null;
	}

	public String getNamesList(boolean shortened)
	{
		String name = "";
		if (mSelectedFriends.size() < 3)
		{
			for (Friend friend : mSelectedFriends)
			{
				name += friend.getName() + "\n";
			}
		}
		else if (mSelectedFriends.size() > 2)
		{
			if (shortened)
			{
				for (int i=0; i <= 2; i++)
				{
					name += mSelectedFriends.get(i).getName() + "\n";
				}
				name += "and more...";
			}
			else
			{
				for (int i=0; i <= 5; i++)
				{
					name += mSelectedFriends.get(i).getName() + "\n";
				}
				if (mSelectedFriends.size() > 5)
				{
					name += "and more...";
				}
			}
		}
		return name;
	}

	/**
	 * Create a list of all new debts to add into the database
	 * @return A list of debts that can be iterated through
	 */
	public List<Debt> getNewDebts()
	{
		BigDecimal debtAmount = getAmountToApply();

		List<Debt> debts = new ArrayList<Debt>();
		for (Friend friend : mSelectedFriends)
		{
			debts.add(new Debt(0, friend.getRepayID(), getDate(), debtAmount, mDescription));
		}
		return debts;
	}

	/**
	 * Quickly get the amount that will be applied to each person
	 * @return {@link java.math.BigDecimal} representation of the amount
	 */
	public BigDecimal getAmountToApply()
	{
		BigDecimal debtAmount;
		if (distributeEvenly)
		{
			if (includingMe)
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedFriends.size() + 1), RoundingMode.CEILING);
			}
			else
			{
				debtAmount = mAmount.divide(new BigDecimal(mSelectedFriends.size()), BigDecimal.ROUND_CEILING);
			}
		}
		else
		{
			debtAmount = mAmount;
		}
		if (!owesMe) debtAmount = debtAmount.negate();
		return debtAmount;
	}

	public List<Friend> getUpdatedFriends()
	{
		BigDecimal debtAmount = getAmountToApply();
		ArrayList<Friend> updated = mSelectedFriends;
		for (Friend friend : updated)
		{
			friend.setDebt(friend.getDebt().add(debtAmount));
		}
		return updated;
	}
}
