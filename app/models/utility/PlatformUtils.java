package models.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.exception.SynAdException;

public class PlatformUtils {

	public static Date getDateFromString(String date) throws SynAdException {
		try {
			Date updatedDate = new SimpleDateFormat("MM/dd/yyyy")
					.parse(date);
			return updatedDate;
		} catch (ParseException e) {
			throw new SynAdException("Invalid Date Format: " + date
					+ " It should be : MM/dd/yyyy");
		}
	}
	
}
