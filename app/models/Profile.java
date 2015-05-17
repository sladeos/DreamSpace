package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Profile extends Model{

@Id @Constraints.Required
public String username;


public String favouritegames;


public String userbio;


public String skypeID;


public String steamID;


public String battlenetID;


public String uplayID;


public String twitchID;


public int userID;
}