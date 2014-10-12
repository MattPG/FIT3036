package cablegate.models;

import java.sql.Clob;
import java.sql.SQLException;

import org.hibernate.search.bridge.StringBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clob-String Bridge.
 * Converts clob type to a string
 *
 */
public class ClobToStringBridge implements StringBridge {
	private static final Logger log = LoggerFactory.getLogger(ClobToStringBridge.class);

	@Override
	public String objectToString(Object object) {
		Clob cableText = (Clob) object;
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
		return buffer;
	}
}   