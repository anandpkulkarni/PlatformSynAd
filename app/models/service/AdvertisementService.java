package models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.constants.PlatformConstants;
import models.dao.ActionsDAO;
import models.dao.AdvertiseDAO;
import models.dao.DateConditionsDAO;
import models.dao.ItemsDAO;
import models.dao.UserSessionDAO;
import models.entity.Action;
import models.entity.Advertise;
import models.entity.DateCondition;
import models.entity.Item;
import models.entity.MessageReply;
import models.entity.UserSession;
import models.exception.SynAdException;
import models.utility.PlatformUtils;
import play.mvc.Http.RequestBody;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author anandk
 * 
 */
public class AdvertisementService {

	public static List<Item> getCategories() {
		return ItemsDAO.getAllItems();
	}

	public static List<DateCondition> getDateConditions() {
		return DateConditionsDAO.getAllConditions();
	}

	public static List<Action> getActions() {
		return ActionsDAO.getAllActions();
	}

	public static Advertise getPost(String postId) throws Exception {
		return AdvertiseDAO.findById(postId);
	}
	
	public static Advertise getDetailedPostView(String authToken, String postId) throws Exception {
		UserSession userSession = UserSessionDAO.getSessionById(authToken);
		if (userSession == null) {
			throw new SynAdException("Unauthorized service access");
		}
		String loggedInUserId = userSession.userId;
		Advertise advertise = AdvertiseDAO.findById(postId);
		if(advertise.userId.equals(loggedInUserId)) {
			//List<MessageReply> replies = AdvertiseDAO.getAdvertiseReplies(postId);
			advertise.isOwnerOfAd = true;
		}
		else {
			advertise.replies = new ArrayList<MessageReply>();
		}
		return advertise;
	}
	
	public static boolean deletePost(String postId) throws Exception {
		AdvertiseDAO.delete(postId);
		return true;
	}
	
	public static List<Advertise> getPosts(String token) throws Exception {
		
		UserSession userSession = UserSessionDAO.getSessionById(token);
		if (userSession == null) {
			throw new SynAdException("Unauthorized service access");
		}
		List<Advertise> myposts = AdvertiseDAO.findByUserId(userSession.userId);
		for(Advertise advertise: myposts) {
			advertise.photo_1 = null;
			advertise.photo_2 = null;
			advertise.photo_3 = null;
		}
		return myposts;
	}
	
	public static byte[] getPhoto(String postId, String photoId) throws Exception {
		Advertise advertise = AdvertiseDAO.findById(postId);
		if ("1".equals(photoId)) {
			return advertise.photo_1;
		}
		else if ("2".equals(photoId)) {
			return advertise.photo_2;
		}
		else if ("3".equals(photoId)) {
			return advertise.photo_3;
		}
		return null;
	}
	
	public static Advertise sendMessage(JsonNode sendMessageJSON, String token) throws Exception {
		String message = sendMessageJSON.get("message").asText();
		String postId = sendMessageJSON.get("postId").asText();
		Advertise advertise = AdvertiseDAO.findById(postId);
		UserSession session = UserSessionDAO.getSessionById(token);
		MessageReply messageReply = new MessageReply();
		messageReply.createdDate = new Date();
		messageReply.message = message;
		messageReply.receiverUserId = advertise.userId;
		messageReply.senderUserId = session.userId;
		advertise.replies.add(messageReply);
		return AdvertiseDAO.update(advertise);
	}
	
	public static Advertise postAd(RequestBody requestBody, String token) throws Exception {
		return createOrUpdateAd(requestBody, token, "CREATE");
	}
	
	public static Advertise updateAd(RequestBody requestBody, String token) throws Exception {
		return createOrUpdateAd(requestBody, token, "UPDATE");
	}
	
	private static Advertise createOrUpdateAd(RequestBody requestBody, String token, String action) throws Exception {
		
		UserSession userSession = UserSessionDAO.getSessionById(token);
		if (userSession == null) {
			throw new SynAdException("Unauthorized service access");
		}
		JsonNode postAdJSON = requestBody.asJson();

		byte photo_1[] = getPhoto(postAdJSON, action, 1);
		byte photo_2[] = getPhoto(postAdJSON, action, 2);
		byte photo_3[] = getPhoto(postAdJSON, action, 3);
		
		String title = postAdJSON.get("title").asText();
		String name = postAdJSON.get("name").asText();
		String description = "";
		if (postAdJSON.get("description") != null) {
			description = postAdJSON.get("description").asText();
		}
		String categoryName = postAdJSON.get("category").asText();
		
		Advertise adv = new Advertise();
		Date now = new Date();
		adv.userId = userSession.userId;
		adv.title = title;
		adv.name = name;
		adv.description = description;
		adv.category = categoryName;
		adv.categoryId = ItemsDAO.getCategoryIdByName(categoryName);
		adv.photo_1 = photo_1;
		adv.photo_2 = photo_2;
		adv.photo_3 = photo_3;
		adv.lastUpdatedDate = now;
		if ("CREATE".equalsIgnoreCase(action)) {
			adv.createdDate = now;
			adv.replies = new ArrayList<MessageReply>();
			adv.status = PlatformConstants.ADVERTISE_STATUS_OPEN;
			return AdvertiseDAO.create(adv);
		}
		else if ("UPDATE".equalsIgnoreCase(action)) {
			String postId = postAdJSON.get("postId").asText();
			adv = AdvertiseDAO.findById(postId);
			adv.status = postAdJSON.get("status").asText();
			if (PlatformConstants.ADVERTISE_STATUS_DELETE.equalsIgnoreCase(adv.status)) {
				AdvertiseDAO.delete(postId);
			}
			else {
				return AdvertiseDAO.update(adv);
			}
		}
		return null;
	}

	private static byte [] getPhoto(JsonNode postAdJSON, String action, int imgId) throws Exception {
		byte photo[] = null;
		if (postAdJSON.get("photo" + imgId) != null) {
			photo = postAdJSON.get("photo" + imgId).binaryValue();
			if(photo == null && "UPDATE".equalsIgnoreCase(action)) 
			{
				if (postAdJSON.get("is_img_thumbnil_" +  + imgId + "_changed").asText().equalsIgnoreCase("true")) {
					photo = null;
				}
				else {
					String id = postAdJSON.get("postId").asText();
					if (imgId == 1)
						photo = AdvertiseDAO.findById(id).photo_1;
					else if (imgId == 2)
						photo = AdvertiseDAO.findById(id).photo_2;
					if (imgId == 3)
						photo = AdvertiseDAO.findById(id).photo_3;
				}
			}
		}
		return photo;
	}
	
	public static Map<String, Object> searchAdvertisements(String authToken, String category, String postedBy,
			String dateCondition, String onDate, String fromDate, String toDate, String sortBy, int startIndex, int records) throws Exception {
		UserSession session = UserSessionDAO.getSessionById(authToken);
		if (session == null) {
			throw new SynAdException("Unauthorized service access");
		}
		String userId = session.userId;
		Date dtToDate = null;
		Date dtFromDate = null;
		Date dtOnDate = null;
		
		if (onDate != null && !"".equals(onDate)) {
			dtOnDate = PlatformUtils.getDateFromString(onDate);
		}
		if (fromDate != null && !"".equals(fromDate)) {
			dtFromDate = PlatformUtils.getDateFromString(fromDate);
		}
		if (toDate != null && !"".equals(toDate)) {
			dtToDate = PlatformUtils.getDateFromString(toDate);
		}
		
		Map<String, Object> hmapAdvDetails = new HashMap<String, Object>();
		hmapAdvDetails.put("itemList", ItemsDAO.getAllItems());
		hmapAdvDetails.put("dateConditionList", DateConditionsDAO.getAllConditions());
		List<Advertise> advertiseList = AdvertiseDAO.searchAdvertisements(userId, category, postedBy, dateCondition, dtOnDate, dtFromDate, dtToDate, sortBy, startIndex, records);
		hmapAdvDetails.put("advertiseList", advertiseList);
		return hmapAdvDetails;
	}
}
