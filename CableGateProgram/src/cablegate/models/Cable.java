package cablegate.models;

import java.sql.Clob;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Cable {
	private static final Logger log = LoggerFactory.getLogger(Cable.class);
	
	private int cableID;
	
	private String dateTime;
	
	private String cableNumber;
	
	private String sender;
	
	private String classification;
	
	private String referrals;
	
	private String mailingList;
	
	private String cableString;	// For runtime processing
	
	private Clob cableText;	// For database storage
	
	public Cable(){}
	
	public int getCableID() {
		return cableID;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public String getCableNumber() {
		return cableNumber;
	}

	public String getSender() {
		return sender;
	}

	public String getClassification() {
		return classification;
	}

	public String getReferrals() {
		return referrals;
	}

	public String getMailingList() {
		return mailingList;
	}
	
	public String getCableString() {
		return cableString;
	}

	public Clob getCableText() {
		return cableText;
	}
	
	public void setCableID(int cableID) {
		this.cableID = cableID;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setCableNumber(String cableNumber) {
		this.cableNumber = cableNumber;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setReferrals(String referrals) {
		this.referrals = referrals;
	}

	public void setMailingList(String mailingList) {
		this.mailingList = mailingList;
	}

	public void setCableString(String cableString) {
		this.cableString = cableString;
	}

	public void setCableText(Clob cableText) {
		this.cableText = cableText;
	}
	
	public Cable convertText(){
		String buffer = "";
		if(cableText != null){
			try {
				long length = cableText.length();
				buffer = cableText.getSubString(0, (int) length);				
			} catch (SQLException e) {
				log.error("Failed to convert clob!", e);
			}
		}
		this.setCableString(buffer);	
		return this;
	}

	@Override
	public String toString(){
		return getCableID() + ","
				+ getDateTime() + ","
				+ getCableNumber() + ","
				+ getSender() + ","
				+ getClassification();
	};
}
