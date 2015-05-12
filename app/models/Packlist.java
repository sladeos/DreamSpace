package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.*;
import play.db.ebean.Model;

@Entity
public class Packlist extends Model{

@Id @Constraints.Required
public String PersonalComputer;

@Constraints.Required 
public String Powerstrip;

;
}