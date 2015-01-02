package models.entity;

import java.util.Date;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

/**
 * @author anandk
 *
 */
public class MessageReply {

	@Id
	@ObjectId
	public String id = org.bson.types.ObjectId.get().toString();
	
	public String senderUserId;
	public String receiverUserId;
	
	public String message;
	public Date createdDate;
}
