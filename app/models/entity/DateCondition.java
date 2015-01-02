package models.entity;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

/**
 * @author anandk
 *
 */
public class DateCondition {

	@Id
	@ObjectId
	public String id;
	
	public String condition;
}
