package cablegate.importer;


public class CableBean {
	private static final String[] HEADER_ARRAY ={	"cableID",
													"dateTime", 
													"cableNumber",
													"sender",
													"classification",
													"referrals",
													"mailingList",
													"cableText" 
												};

	private int cableID;
	
	private String dateTime;
	
	private String cableNumber;
	
	private String sender;
	
	private String classification;
	
	private String referrals;
	
	private String mailingList;
	
	private String cableText;

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

	public String getCableText() {
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

	public void setCableText(String cableText) {
		this.cableText = cableText;
	}
	
	public static String[] getHeaderArray() {
		return HEADER_ARRAY;
	}

	@Override
	public String toString(){
		return getCableID() + ","
				+ getDateTime() + ","
				+ getCableNumber() + ","
				+ getSender() + ","
				+ getClassification() + ","
				+ getReferrals() + ","
				+ getMailingList();
	}
	
}
