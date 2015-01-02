package models.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.constants.PlatformConstants;
import models.entity.Advertise;
import models.entity.MessageReply;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import play.modules.mongodb.jackson.MongoDB;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class AdvertiseDAO {

	private static JacksonDBCollection<Advertise, String> coll = MongoDB
			.getCollection("Advertise", Advertise.class, String.class);

	public static Advertise create(Advertise advertise) {
		WriteResult<Advertise, String> result = coll.insert(advertise);
		return result.getSavedObject();
	}
	
	public static Advertise update(Advertise advertise) {
		WriteResult<Advertise, String> result = coll.save(advertise);
		return result.getSavedObject();
	}
	
	public static List<Advertise> findByUserId(String userId) {
		List<Advertise> myposts = coll.find(DBQuery.is("userId", userId)).sort(new BasicDBObject("lastUpdatedDate", -1)).toArray();
		return myposts;
	}
	
	public static Advertise findById(String postId) {
		return coll.findOneById(postId);
	}

	public static void delete(String postId) {
		coll.removeById(postId);
	}

	public static List<MessageReply> getAdvertiseReplies(String postId) {
		Advertise advertise = findById(postId);
		return advertise.replies;
	}
	
	public static List<Advertise> searchAdvertisements(String loggedInUserId, String category, String postedBy,
			String dateCondition, Date onDate, Date fromDate, Date toDate, String sortBy, int startIndex, int records) throws Exception {
		BasicDBList and = new BasicDBList();
		if (category != null && !"".equals(category)) {
			and.add(new BasicDBObject("category", category));
		}
		
		if (postedBy != null) {
			Pattern ptr = Pattern.compile("^"+postedBy,Pattern.CASE_INSENSITIVE);
			and.add(new BasicDBObject("name", ptr));
		}
		
		if (dateCondition != null && !"".equals(dateCondition)) {
			if (PlatformConstants.DATE_CONDITION_EQUALS.equalsIgnoreCase(dateCondition)) {
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$gt", onDate)));
				Date endOfDay = new Date(onDate.getTime() + (24 * 60 * 60 * 1000));
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$lt", endOfDay)));
			}
			else if(PlatformConstants.DATE_CONDITION_AFTER.equalsIgnoreCase(dateCondition)) {
				Date endOfDay = new Date(onDate.getTime() + (24 * 60 * 60 * 1000));
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$gt", endOfDay)));
			}
			else if(PlatformConstants.DATE_CONDITION_BEFORE.equalsIgnoreCase(dateCondition)) {
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$lt", onDate)));
			}
			else if(PlatformConstants.DATE_CONDITION_ON_AFTER.equalsIgnoreCase(dateCondition)) {
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$gte", onDate)));
			}
			else if(PlatformConstants.DATE_CONDITION_ON_OR_BEFORE.equalsIgnoreCase(dateCondition)) {
				Date endOfDay = new Date(onDate.getTime() + (24 * 60 * 60 * 1000));
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$lte", endOfDay)));
			}
			else if(PlatformConstants.DATE_CONDITION_BETWEEN.equalsIgnoreCase(dateCondition)) {
				Date endOfDay = new Date(toDate.getTime() + (24 * 60 * 60 * 1000));
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$gt", fromDate)));
				and.add(new BasicDBObject("createdDate", new BasicDBObject("$lt", endOfDay)));
			}
		}
		
		BasicDBObject match = new BasicDBObject("$match", new BasicDBObject("$and", and));
		BasicDBObject limit = new BasicDBObject("$limit",records);
		String sortColumn = "createdDate";
		if (PlatformConstants.SORT_BY_POSTED_DATE.equalsIgnoreCase(sortBy)) {
			sortColumn = "createdDate";
		}
		else if (PlatformConstants.SORT_BY_POSTED_BY.equalsIgnoreCase(sortBy)) {
			sortColumn = "name";
		}
		else if (PlatformConstants.SORT_BY_TITLE.equalsIgnoreCase(sortBy)) {
			sortColumn = "title";
		}
		
		BasicDBObject sort = new BasicDBObject("$sort",new BasicDBObject(sortColumn,-1));
		
		BasicDBObject skip = new BasicDBObject("$skip",startIndex);
		DBCollection collection = coll.getDbCollection();
		AggregationOutput output = collection.aggregate(match, sort, skip, limit);
		return getAdvertiseListUsingOutput(output, loggedInUserId);
		
	}
	
	public static List<Advertise> getAdvertiseListUsingOutput(AggregationOutput output, String loggedInUserId) throws Exception {
		List<Advertise> listAdvertise = new ArrayList<Advertise>();
		for (DBObject obj : output.results()) {
			Advertise advertise = new Advertise();
			advertise.title = (String)obj.get("title");
			advertise.id = obj.get("_id").toString();
			advertise.userId = (String)obj.get("userId");
			advertise.name = (String)obj.get("name");
			advertise.description = (String)obj.get("description");
			advertise.categoryId = (String)obj.get("categoryId");
			advertise.category = (String)obj.get("category");
			advertise.status = (String)obj.get("status");
			advertise.createdDate = (Date)obj.get("createdDate");
			advertise.lastUpdatedDate = (Date)obj.get("lastUpdatedDate");
			if (loggedInUserId.equals(advertise.userId)) {
				advertise.isOwnerOfAd = true;
			}
			advertise.replies = (List)obj.get("replies");
			int replyCount = 0;
			if (advertise.replies != null) {
				replyCount = advertise.replies.size();
			}
			advertise.replyCount = replyCount;
			
			listAdvertise.add(advertise);
		}
		return listAdvertise;
	}
}
