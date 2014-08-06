package streaming.attempt1;

import java.util.Date;

import org.csveed.annotations.CsvCell;
import org.csveed.annotations.CsvDate;
import org.csveed.annotations.CsvFile;

@CsvFile(useHeader = false, quote='\"', escape='\\', separator=',')
public class CableBean {
	
	@CsvCell(required = true)
	private int cableNumber;
	
	@CsvDate(format = "M/d/yyyy H:mm")
	private Date dateTime;
	
	@CsvCell(required = true)
	private String cableID;
	
	@CsvCell(required = true)
	private String sender;
	
	@CsvCell(required = true)
	private String classification;
	
	@CsvCell(required = false)
	private String references;
	
	@CsvCell(required = true)
	private String mailingList;
	
	@CsvCell(required = true)
	private String cableText;

	public int getCableNumber() {
		return cableNumber;
	}
	
	public Date getDateTime() {
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

	public void setDateTime(Date dateTime) {
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
