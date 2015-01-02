/**
 * 
 */
package controllers;

import java.util.Map;

import models.service.SigninService;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author anandk
 * 
 */
@With (LoggingAction.class)
public class Signin extends Controller {

	public static Result login() throws Exception {
		Request req = request();
		String userAgent = req.getHeader("client-user-agent");
		if(userAgent == null){
			userAgent = req.getHeader("user-agent");
		}
		JsonNode loginJSON = req.body().asJson();
		response().setContentType("application/json");
		ObjectNode data = Json.newObject();

		Map<String, Object> hmapUserDetails = SigninService.login(loginJSON, userAgent);
		
		Logger.info("Logged in user#" + hmapUserDetails.get("userId"));
		
		ObjectNode token = Json.newObject();
		token.put("auth-token", (String) hmapUserDetails.get("auth-token"));
		token.put("userId", (String) hmapUserDetails.get("userId"));
		JsonNode itemListJson = Json.toJson(hmapUserDetails.get("itemList"));
		JsonNode dateConditionListJson = Json.toJson(hmapUserDetails.get("dateConditionList"));
		token.put("itemList", itemListJson);
		token.put("dateConditionList", dateConditionListJson);
		data.put("data", token);			
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result logout() throws Exception{
		Request req = request();
		String token = req.getHeader("auth-token");
		Logger.info("Logged out user#" + req.username());
		
		SigninService.logout(token);
		return ok();
	}
}
