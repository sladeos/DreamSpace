package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Tournament extends Model{

@Id @Constraints.Required
public String tournamentname;
 
@Constraints.Required
public int participant_count;

@Constraints.Required 
public String tournamentdata;

@Constraints.Required
public String tournamentcreator;

public int tournamentID;

public String information;

public String logo;
}