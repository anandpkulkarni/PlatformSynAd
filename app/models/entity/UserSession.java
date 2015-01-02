/**
 * 
 */
package models.entity;

import java.util.Date;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

/**
 * @author anandk
 *
 */
public class UserSession {
	
	@Id
	@ObjectId
	public String id;
	
	public String userId;
	
	public Date lastUpdatedDate;
}
