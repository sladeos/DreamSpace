package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class AdReply extends Model {


	@Id
	public int arenaID;

	@Constraints.Required
	public String user;

	@Constraints.Required
	public String content;
}
