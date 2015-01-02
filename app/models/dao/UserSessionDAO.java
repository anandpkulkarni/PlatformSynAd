package models.dao;

import java.util.Date;

import models.entity.UserSession;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import play.modules.mongodb.jackson.MongoDB;

public class UserSessionDAO {

	private static JacksonDBCollection<UserSession, String> coll = MongoDB
			.getCollection("userSession", UserSession.class, String.class);

	public static UserSession createUserSession(String userId) {
		UserSession session = new UserSession();
		session.userId = userId;
		session.lastUpdatedDate = new Date();
		WriteResult<UserSession, String> result = coll.save(session);
		session = result.getSavedObject();
		return session;
	}

	public static void delete(String token) {
		coll.removeById(token);
	}

	public static UserSession getSessionById(String id) {
		return coll.findOneById(id);
	}
}
