package models.dao;

import java.util.List;

import models.entity.Item;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class ItemsDAO {

	private static JacksonDBCollection<Item, String> coll = MongoDB
			.getCollection("Items", Item.class, String.class);
	
	public static List<Item> getAllItems() {
		return coll.find().toArray();
	}
	
	public static String getCategoryIdByName(String category) {
		List<Item> itemList = coll.find(DBQuery.is("name", category)).toArray();
		Item item = itemList.get(0);
		return item.id;
	}

	public static String getCategoryById(String categoryId) {
		return coll.findOneById(categoryId).name;
	}
}
