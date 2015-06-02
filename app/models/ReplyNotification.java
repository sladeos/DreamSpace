package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class ReplyNotification extends Model{

@Id @Constraints.Required 
public int replyID;

@Constraints.Required 
public int arenaID;

@Constraints.Required
public String arenaadmin;

@Constraints.Required 
public String username;

@Constraints.Required
public String created_date;

@Constraints.Required 
public int viewed;

public String adName;
}