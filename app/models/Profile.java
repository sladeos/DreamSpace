package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Profile extends Model{

@Id @Constraints.Required
public String username;

@Constraints.Required 
public String favouritegames;

@Constraints.Required
public String userbio;

@Constraints.Required
public String skypeID;

@Constraints.Required 
public String steamID;

@Constraints.Required
public String battlenetID;

@Constraints.Required
public String uplayID;

@Constraints.Required 
public String twitchID;

@Constraints.Required
public int userID;
}