package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class EArenaAd extends Model {

	@Constraints.Required
	public String arenaName;

	@Id
	public int arenaID;

	public String admin;

	@Constraints.Required
	public String information;

	@Constraints.Required
	public String gameName;

	@Constraints.Required
	public int playersRequired;

}
