package models.dao;

import java.util.List;

import models.entity.DateCondition;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class DateConditionsDAO {

	private static JacksonDBCollection<DateCondition, String> coll = MongoDB
			.getCollection("DateConditions", DateCondition.class, String.class);
	
	public static List<DateCondition> getAllConditions() {
		return coll.find().toArray();
	}
}
