/**
 * 
 */
package models.service;

import java.util.HashMap;
import java.util.Map;

import models.dao.DateConditionsDAO;
import models.dao.ItemsDAO;
import models.dao.UserSessionDAO;
import models.entity.UserSession;
import models.exception.SynAdException;
import models.utility.GoogleAppClientLogin;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author anandk
 *
 */
public class SigninService {
	
	/**
	 * Performs Login
	 * 
	 * @param userJSON Received JSON data
	 * @return Valid auth-token that needs to be passed for future accesses
	 * @throws Exception
	 */
	public static Map<String, Object> login(JsonNode userJSON, String userAgent) throws Exception
	{
		String userName = null;
		String password = null;
		if(userJSON.has("password")){
			password = userJSON.get("password").asText();
		}else{
			throw new SynAdException("Password is required");
		}

		if(userJSON.has("userName")){
			userName = userJSON.get("userName").asText();
		}else{
			throw new SynAdException("Username is required");
		}

		Map<String, Object> hmapUserDetails = new HashMap<String, Object>();
		boolean isAuthSuccessful = GoogleAppClientLogin.login(userName, password);
		if(isAuthSuccessful) { //Login successful
			UserSession userSession = UserSessionDAO.createUserSession(userName);
			hmapUserDetails.put("auth-token", userSession.id);
			hmapUserDetails.put("userId", userSession.userId);
			hmapUserDetails.put("itemList", ItemsDAO.getAllItems());
			hmapUserDetails.put("dateConditionList", DateConditionsDAO.getAllConditions());
		}
		return hmapUserDetails;
	}
	
	public static void logout(String token) {
		UserSessionDAO.delete(token);
	}
	
	public static String validateToken(String token) {
		UserSession session = UserSessionDAO.getSessionById(token);
		
		if (session == null) {
			return null;
		}
		return session.userId;
	}
}
