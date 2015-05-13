package controllers;

import models.*;

import java.security.SecureRandom;
import java.sql.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import views.html.*;
import models.FacebookUser;
import play.data.Form;
import play.db.DB;
import play.libs.Json;
import play.mvc.*;
import play.db.*;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDatabase extends Controller {

	

public static Profile getProfile() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		
		Profile p = new Profile();
		String username = session("connected");
		
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM UserProfile WHERE username=?;";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, username);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.isBeforeFirst()) {
				rs.next();
				p.username = rs.getString("username");
				p.favouritegames = rs.getString("favouritegames");
				p.userbio = rs.getString("userbio");
				p.skypeID = rs.getString("skypeID");
				p.steamID = rs.getString("steamID");
				p.battlenetID = rs.getString("battlenetID");
				p.uplayID = rs.getString("uplayID");
				p.twitchID = rs.getString("twitchID");
				p.userID = rs.getInt("userID");
			}


			return p;

		} catch (SQLException se) {
			return null;
		} catch (Exception e) {
			// Handle errors for Class.forName
			return null;
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
		}

	}





	public static Result addUserProfile() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();


		String username = session("connected");
		//String avatarIDstring = json.findPath("avatarID").textValue();
		String skypeID = json.findPath("skypeID").textValue();
		String battlenetID = json.findPath("battlenetID").textValue();
		String steamID = json.findPath("steamID").textValue();
		String twitchID = json.findPath("twitchID").textValue();
		String uplayID = json.findPath("uplayID").textValue();
		String favouritegames = json.findPath("favouritegames").textValue();
		String userbio = json.findPath("about").textValue();

		
		try {

			conn = DB.getConnection();

			//int avatarID = Integer.parseInt(avatarIDstring);
			String insertIntoDatabase = "INSERT INTO UserProfile (username, favouritegames, userbio, skypeID, steamID, battlenetID, uplayID, twitchID) VALUES(?,?,?,?,?,?,?,?)";
			
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, favouritegames);
			preparedStatement.setString(3, userbio);
			preparedStatement.setString(4, skypeID);
			preparedStatement.setString(5, steamID);
			preparedStatement.setString(6, battlenetID);
			preparedStatement.setString(7, uplayID);
			preparedStatement.setString(8, twitchID);
		//	preparedStatement.setInt(13, avatarID);

			preparedStatement.executeUpdate();
			return ok("Succesful Reply!");
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString() + "HEYEYEY");
		} 
		catch (NumberFormatException nfe) {
			return badRequest(nfe.toString() + "WOW");
		} catch (SQLException se) {
			// Handle sql errors
			return internalServerError(se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}

	






	 public static Result editUserProfile() {
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();


		String username = session("connected");
		//String avatarIDstring = json.findPath("avatarID").textValue();
		String skypeID = json.findPath("skypeID").textValue();
		String battlenetID = json.findPath("battlenetID").textValue();
		String steamID = json.findPath("steamID").textValue();
		String twitchID = json.findPath("twitchID").textValue();
		String uplayID = json.findPath("uplayID").textValue();
		String favouritegames = json.findPath("favouritegames").textValue();
		String userbio = json.findPath("about").textValue();

		
		try {

			conn = DB.getConnection();

			//int avatarID = Integer.parseInt(avatarIDstring);
			String insertIntoDatabase = "UPDATE UserProfile SET favouritegames=?, userbio=?, skypeID=?, steamID=?, battlenetID=?, uplayID=?, twitchID=? WHERE username=?";
			
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			
			preparedStatement.setString(1, favouritegames);
			preparedStatement.setString(2, userbio);
			preparedStatement.setString(3, skypeID);
			preparedStatement.setString(4, steamID);
			preparedStatement.setString(5, battlenetID);
			preparedStatement.setString(6, uplayID);
			preparedStatement.setString(7, twitchID);
		//	preparedStatement.setInt(13, avatarID);
			preparedStatement.setString(8, username);

			preparedStatement.executeUpdate();
			return ok("Succesful Reply!");
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString() + "HEYEYEY");
		} 
		catch (NumberFormatException nfe) {
			return badRequest(nfe.toString() + "WOW");
		} catch (SQLException se) {
			// Handle sql errors
			return internalServerError(se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}
	




 }
