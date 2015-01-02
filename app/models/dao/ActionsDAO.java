package models.dao;

import java.util.List;

import models.entity.Action;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class ActionsDAO {

	private static JacksonDBCollection<Action, String> coll = MongoDB
			.getCollection("Actions", Action.class, String.class);
	
	public static List<Action> getAllActions() {
		return coll.find().toArray();
	}
	
	public static String getActionIdByName(String actionName) {
		List<Action> actionList = coll.find(DBQuery.is("name", actionName)).toArray();
		Action action = actionList.get(0);
		return action.id;
	}

	public static String getActionById(String actionId) {
		return coll.findOneById(actionId).name;
	}
}
