package vnapnic.project.debtmanager.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 *
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 *
 */

public class Debt implements Comparable<Debt>, Serializable {
	
	private Date date;
	private BigDecimal amount;
	private String repayID, description;
	private int debtID;
	
	public Debt(int debtID, String repayID, Date date, BigDecimal amount, String description) {
		super();
		this.date = date;
		this.debtID = debtID;
		this.amount = amount;
		this.repayID = repayID;
		this.description = description;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRepayID() {
		return repayID;
	}

	public void setRepayID(String repayID) {
		this.repayID = repayID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getDebtID(){
		return debtID;
	}
	
	@Override
	public int compareTo(Debt another) {
		return another.getDate().compareTo(date);
	}
}
