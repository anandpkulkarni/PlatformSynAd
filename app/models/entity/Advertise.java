package models.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.ObjectId;

/**
 * @author anandk
 *
 */
public class Advertise {

	@Id
	@ObjectId
	public String id;
	
	public String userId;
	public String name;
	public String title;
	public String description;
	public String categoryId;
	public String category;
	public String status;
	public byte [] photo_1;
	public byte [] photo_2;
	public byte [] photo_3;
	
	public Date createdDate;
	public Date lastUpdatedDate;
	public List<MessageReply> replies;

	@Transient
	public boolean isOwnerOfAd;
	@Transient
	public int replyCount;
}
