package cablegate.models;

import java.sql.Clob;
import java.sql.SQLException;

import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.apache.solr.analysis.StopFilterFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TermVector;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Indexed
@AnalyzerDef(name = "CableAnalyser",
tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
filters = {
	@TokenFilterDef(factory = LowerCaseFilterFactory.class),
//	@TokenFilterDef(factory = SynonymFilterFactory.class),
//	@TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
//		@Parameter(name = "language", value = "English")
//	}),
	@TokenFilterDef(factory = StopFilterFactory.class, params = {
		@Parameter(name="words", value= "stopwords.txt"),
		@Parameter(name="ignoreCase", value="true")
	})
})
public class Cable {
	private static final Logger log = LoggerFactory.getLogger(Cable.class);
	
//	@Boost(2f)
	@DocumentId
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private int cableID;

//	@Boost(2f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String dateTime;

//	@Boost(3f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String cableNumber;

//	@Boost(2f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String sender;

//	@Boost(3f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String classification;

//	@Boost(1.5f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String referrals;

//	@Boost(1.5f)
	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	private String mailingList;

	@Field(termVector = TermVector.WITH_POSITION_OFFSETS)
	@FieldBridge(impl = ClobToStringBridge.class)
	private Clob cableText;	// For database storage

	private String cableString;	// For runtime processing
	
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
	
	/*
	 * Converts the Clob cableText into the String cableString object.
	 * Note: requires the Connection the retrieved this object to still be open.
	 */
	public void convertText(){
		String buffer = "";
		if(cableText != null){
			try {
				long length = cableText.length();
				buffer = cableText.getSubString(1, (int) length);	
			} catch (SQLException e) {
				log.error("Failed to convert clob!", e);
			}
		}else {
			log.error("Null clob detected!");
		}
		this.setCableString(buffer);
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
