package controllers;

import models.*;
import play.*;
import play.mvc.*;

import java.io.UnsupportedEncodingException;
import java.sql.*;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.SecureRandom;

import play.libs.Json;

public class Application extends Controller {

	public static Result mainMethod() {
		String user = session("connected");
		if (user != null) {
			return ok(main.render("You are logged in as " + user));
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}
	
	public static Result facebookExistLogin(){
	
		return mainMethod();
	}
	

	public static Result newUserPage() {
		String currentUser = session("connected");
		if (currentUser != null) {
			return ok(main.render("You are already logged in as " + currentUser
					+ " Please log out if you wish to create another account"));
		}

		return ok(NewUserPage.render(""));
	}

	public static Result loginUserPage() {

		String currentUser = session("connected");
		if (currentUser != null) {
			return ok(main
					.render("You are already logged in as " + currentUser));
		}

		return ok(LoginUserPage.render(""));
	}

	public static Result logout() {
		session().clear();
		return redirect(routes.Application.loginUserPage());
	}

}
