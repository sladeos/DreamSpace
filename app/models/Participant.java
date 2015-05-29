package models;

import java.util.*;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Participant extends Model {


	@Id @Constraints.Required
	public int tournamentID;

	@Id @Constraints.Required
	public String username;

}
