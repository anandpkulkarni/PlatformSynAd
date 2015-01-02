package models.entity;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

/**
 * @author anandk
 *
 */
public class Item {

	@Id
	@ObjectId
	public String id;
	
	public String name;
}
