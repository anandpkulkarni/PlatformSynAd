package controllers;

import java.util.List;
import java.util.Map;

import models.entity.Action;
import models.entity.Advertise;
import models.entity.DateCondition;
import models.entity.Item;
import models.service.AdvertisementService;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBody;
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
public class Advertisement extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result sendMessage() throws Exception {
		Request req = request();
		RequestBody requestBody = req.body();
		JsonNode sendMessageJSON = requestBody.asJson();
		String authToken = req.getHeader("auth-token");
		Advertise advertise = AdvertisementService.sendMessage(sendMessageJSON, authToken);
		response().setContentType("application/json");
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("userId", advertise.userId);
		token.put("title", advertise.title);
		token.put("name", advertise.name);
		token.put("description", advertise.description);
		token.put("category", advertise.category);
		token.put("categoryId", advertise.categoryId);
		data.put("data", token);
		return ok(data);
	}

	@Security.Authenticated(Secured.class)
	public static Result getMessageThread(String postId) throws Exception {
		return ok();
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getCategories() throws Exception {
		response().setContentType("application/json");
		List<Item> itemList = AdvertisementService.getCategories();
		JsonNode itemListJson = Json.toJson(itemList);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("itemList", itemListJson);
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getDateConditions() throws Exception {
		response().setContentType("application/json");
		List<DateCondition> dateConditionList = AdvertisementService.getDateConditions();
		JsonNode dateConditionListJson = Json.toJson(dateConditionList);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("dateConditionList", dateConditionListJson);
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getActions() throws Exception {
		response().setContentType("application/json");
		List<Action> actionList = AdvertisementService.getActions();
		JsonNode actionListJson = Json.toJson(actionList);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("actionList", actionListJson);
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result postAd() throws Exception {
		Request req = request();
		RequestBody requestBody = req.body();
		String authToken = req.getHeader("auth-token");
		Advertise advertise = AdvertisementService.postAd(requestBody, authToken);
		response().setContentType("application/json");
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("userId", advertise.userId);
		token.put("title", advertise.title);
		token.put("name", advertise.name);
		token.put("description", advertise.description);
		token.put("category", advertise.category);
		token.put("categoryId", advertise.categoryId);
		data.put("data", token);
		return ok(data);
	}

	@Security.Authenticated(Secured.class)
	public static Result updateAd() throws Exception {
		Request req = request();
		RequestBody requestBody = req.body();
		String authToken = req.getHeader("auth-token");
		Advertise advertise = AdvertisementService.updateAd(requestBody, authToken);
		response().setContentType("application/json");
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		if(advertise == null) { //Advertisement got deleted
			token.put("action", "Deleted");
		}
		else {
			token.put("action", "Updated");
			token.put("userId", advertise.userId);
			token.put("title", advertise.title);
			token.put("name", advertise.name);
			token.put("description", advertise.description);
			token.put("category", advertise.category);
			token.put("categoryId", advertise.categoryId);
		}
		data.put("data", token);
		return ok(data);
	}

	@Security.Authenticated(Secured.class)
	public static Result getPosts() throws Exception {
		Request req = request();
		String authToken = req.getHeader("auth-token");
		List<Advertise> myposts = AdvertisementService.getPosts(authToken);
		
		JsonNode mypostListJson = Json.toJson(myposts);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("mypostList", mypostListJson);
		data.put("data", token);
		return ok(data);
	}

	@Security.Authenticated(Secured.class)
	public static Result deletePost(String postId) throws Exception {
		boolean result = AdvertisementService.deletePost(postId);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("result", Json.toJson(result));
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getPost(String postId) throws Exception {
		Advertise advertise = AdvertisementService.getPost(postId);
		
		JsonNode mypostJson = Json.toJson(advertise);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("mypost", mypostJson);
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getDetailedPostView(String postId) throws Exception {
		String authToken = request().getHeader("auth-token");
		Advertise advertise = AdvertisementService.getDetailedPostView(authToken, postId);
		
		JsonNode mypostJson = Json.toJson(advertise);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("mypost", mypostJson);
		data.put("data", token);
		return ok(data);
	}
	
	@Security.Authenticated(Secured.class)
	public static Result getPhoto(String postId, String photoId) throws Exception {
		byte photoArray[] = AdvertisementService.getPhoto(postId, photoId);

		JsonNode photoJson = Json.toJson(photoArray);
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("photo", photoJson);
		data.put("data", token);
		return ok(data);
	}

	@Security.Authenticated(Secured.class)
	public static Result searchAdvertisements(String category, String postedBy,
			String dateCondition, String onDate, String fromDate, String toDate, String sortBy, int startIndex, int records) throws Exception {
		String authToken = request().getHeader("auth-token");
		response().setContentType("application/json");
		Map<String, Object> hmapAdvDetails = AdvertisementService.searchAdvertisements(authToken, 
				category, postedBy, dateCondition, onDate, fromDate, toDate, sortBy, startIndex, records);

		JsonNode itemListJson = Json.toJson(hmapAdvDetails.get("itemList"));
		JsonNode advListJson = Json.toJson(hmapAdvDetails.get("advertiseList"));
		ObjectNode data = Json.newObject();
		ObjectNode token = Json.newObject();
		token.put("advertiseList", advListJson);
		token.put("itemList", itemListJson);
		data.put("data", token);
		return ok(data);
	}
}
