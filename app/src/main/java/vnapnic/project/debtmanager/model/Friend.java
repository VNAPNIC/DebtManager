package vnapnic.project.debtmanager.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class Friend implements Comparable<Friend>, Serializable {
	
	public static final String REPAYID = "repayID";
	public static final String LOOKUPURI = "lookupUri";
	public static final String NAME = "name";
	public static final String AMOUNT = "amount";

	private String name, repayID;
	private BigDecimal debt;
	private String lookupURI;
	
	public Friend(String repayID, String lookupURI, String name, BigDecimal debt) {
		super();
		this.lookupURI = lookupURI;
		this.name = name;
		this.repayID = repayID;
		this.debt = debt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRepayID() {
		return repayID;
	}

	public String getLookupURI() {
		return lookupURI;
	}

	public BigDecimal getDebt() {
		return debt;
	}

	public void setDebt(BigDecimal debt) {
		this.debt = debt;
	}

	@Override
	public int compareTo(Friend another) {
		return another.getDebt().compareTo(debt);
	}

	@Override
	public boolean equals(Object o) {
		return (o.getClass() == Friend.class && ((Friend) o).getRepayID().equals(repayID));
	}
}
