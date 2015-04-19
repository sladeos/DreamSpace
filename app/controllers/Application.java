package controllers;

import models.*; 
import play.*;
import play.data.*;
import play.mvc.*;
import views.html.*;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class Application extends Controller {
    
  
    
    public static Result index() {
         return ok(newpage.render(Form.form(User.class)));
    }
	
	public static Result newPage() {
       // return ok(newpage.render());
       return ok();
    }
    
    public static Result authenticate() {
    Form<User> userForm = Form.form(User.class).bindFromRequest();
    return ok();
    }
    
}