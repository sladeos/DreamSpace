package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Notification extends Model{

@Id @Constraints.Required 
public int tournamentID;

@Constraints.Required
public String admin;

@Id @Constraints.Required 
public String participant;

@Constraints.Required 
public int accepted;

@Constraints.Required 
public int viewed;
}