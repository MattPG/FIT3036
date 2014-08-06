package stickj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.csveed.annotations.CsvCell;
import org.csveed.annotations.CsvFile;
import org.csveed.annotations.CsvIgnore;

@CsvFile(useHeader = false, quote='\"', escape='\\', separator=',')
public class CableBean {
	
	@CsvCell(required = true)
	private int orderNumber;
	
	@CsvCell(required = true)
	private String dateTime;
	
	@CsvCell(required = true)
	private String cableID;
	
	@CsvCell(required = true)
	private String sender;
	
	@CsvCell(required = true)
	private String classification;
	
	@CsvCell(required = false)
	private String references;
	
	@CsvCell(required = true)
	private String receivers;
	
	@CsvCell(required = true)
	private String cableText;
	
	@CsvIgnore
	private static final SimpleDateFormat dateFormat = 
						new SimpleDateFormat("MM/dd/yyy kk:mm");
	
	@CsvIgnore
	private Calendar cableDate = new GregorianCalendar();
	
	public CableBean(){}
	
	@Override
	public String toString(){
		return getOrderNumber() + ","
				+  getDateTime() + ","
				+  getCableID() + ","
				+  getSender() + ","
				+  getClassification() + ","
				+  getReferences() + ","
				+  getReceivers();
	}
	
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
		try {
			cableDate.setTime(dateFormat.parse(this.dateTime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	public String getCableID() {
		return cableID;
	}
	public void setCableID(String cableID) {
		this.cableID = cableID;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String from) {
		this.sender = from;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public String getReferences() {
		return references;
	}
	public void setReferences(String references) {
		this.references = references;
	}
	public String getReceivers() {
		return receivers;
	}
	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}
	public String getCableText() {
		return cableText;
	}
	public void setCableText(String cableText) {
		this.cableText = cableText;
	}	
	public SimpleDateFormat getDateFormat(){
		return dateFormat;
	}	
	public Calendar getCableDate(){
		return cableDate;
	}
}
