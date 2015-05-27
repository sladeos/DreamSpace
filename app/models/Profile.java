package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Profile extends Model {

	@Id
	@Constraints.Required
	public String username;

	public String userbio;

	public String skypeID;

	public String steamID;

	public String battlenetID;

	public String uplayID;

	public String twitchID;

	public String csgorank;
	
	public String wow2v2rating;
	
	public String wow3v3rating;
	
	public String wow5v5rating;
	
	public String wowrbgrating;
	
	public String lolrank;
	
	public String dota2rank;
	
	public String otherranks;

	public int userID;
	
	

}