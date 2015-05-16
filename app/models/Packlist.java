package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Packlist extends Model{

@Id @Constraints.Required
public String userID;

@Constraints.Required
public String pc;

@Constraints.Required 
public String powerstrip;

@Constraints.Required
public String screenAnd;

@Constraints.Required 
public String keyboard;

@Constraints.Required
public String headset;

@Constraints.Required 
public String networkCable;

@Constraints.Required
public String screwdriver;

@Constraints.Required 
public String cable_Ties;

@Constraints.Required
public String ductTape;

@Constraints.Required 
public String gameConsole;

@Constraints.Required
public String mobilePhone;

@Constraints.Required 
public String camera;

@Constraints.Required
public String flashlight;

@Constraints.Required 
public String chair;

@Constraints.Required
public String safe;

@Constraints.Required 
public String installationMedia;

@Constraints.Required
public String drivers;

@Constraints.Required 
public String games;

@Constraints.Required
public String antiVirus;

@Constraints.Required 
public String toothbrush;

@Constraints.Required
public String soap;

@Constraints.Required 
public String handCream;

@Constraints.Required
public String shampoo;

@Constraints.Required
public String deo;

@Constraints.Required 
public String aspirin;

@Constraints.Required
public String earplugs;

@Constraints.Required 
public String medication;

@Constraints.Required
public String clothes;

@Constraints.Required 
public String swimwear;

@Constraints.Required
public String sheets;

@Constraints.Required
public String sleepingBag;

@Constraints.Required 
public String pillow;

@Constraints.Required
public String mattress;

public int userID;

}