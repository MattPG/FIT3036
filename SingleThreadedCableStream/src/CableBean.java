


public class CableBean {
	
	private int cableNumber;
	
	private String dateTime;
	
	private String cableID;
	
	private String sender;
	
	private String classification;
	
	private String references;
	
	private String mailingList;
	
	private String cableText;

	public int getCableNumber() {
		return cableNumber;
	}
	
	public String getDateTime() {
		return dateTime;
	}

	public String getCableID() {
		return cableID;
	}

	public String getSender() {
		return sender;
	}

	public String getClassification() {
		return classification;
	}

	public String getReferences() {
		return references;
	}

	public String getMailingList() {
		return mailingList;
	}

	public String getCableText() {
		return cableText;
	}

	public void setCableNumber(int cableNumber) {
		this.cableNumber = cableNumber;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public void setCableID(String cableID) {
		this.cableID = cableID;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setReferences(String references) {
		this.references = references;
	}

	public void setMailingList(String mailingList) {
		this.mailingList = mailingList;
	}

	public void setCableText(String cableText) {
		this.cableText = cableText;
	}
	
	public String toString(){
		return getCableNumber() + ","
				+ getDateTime() + ","
				+ getCableID() + ","
				+ getSender() + ","
				+ getClassification() + ","
				+ getReferences() + ","
				+ getMailingList();
	}
	
}