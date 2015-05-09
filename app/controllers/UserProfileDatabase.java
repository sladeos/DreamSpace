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

	



	// public static Result getTournaments() {
	// 	String currentUser = session("connected");
	// 	if (currentUser == null) {
	// 		return unauthorized(LoginUserPage
	// 				.render("You have to login to access this page!"));
	// 	} else {
	// 		Connection conn = null;
	// 		PreparedStatement preparedStatement = null;
	// 		List<Tournament> tList = new ArrayList<Tournament>();
	// 		try {

	// 			conn = DB.getConnection();

	// 			String insertIntoDatabase = "SELECT * FROM ETournament et WHERE admin=?";
	// 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
	// 			preparedStatement.setString(1, currentUser);
	// 			ResultSet rs = preparedStatement.executeQuery();
	// 			// boolean next =

	// 			while (rs.next()) {
	// 				Tournament t = new Tournament();
	// 				t.tournamentname = rs.getString("tournamentName");
	// 				t.participant_count = rs.getInt("teamAmount");
	// 				t.tournamentcreator = rs.getString("admin");
	// 				t.tournamentdata = rs.getString("tournamentData");
	// 				t.tournamentID = rs.getInt("tournamentID");
	// 				tList.add(t);
	// 			}

	// 			rs.close();
	// 			return ok(MainTournamentPage.render(tList));
	// 		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
	// 			return badRequest(ice.toString());
	// 		} catch (NumberFormatException nfe) {
	// 			return badRequest(nfe.toString());
	// 		} catch (SQLException se) {
	// 			// Handle sql errors
	// 			return internalServerError(se.toString());
	// 		} catch (Exception e) {
	// 			// Handle errors for Class.forName
	// 			return internalServerError(e.toString());
	// 		} finally {
	// 			// finally block used to close resources
	// 			// try {
	// 			// if (preparedStatement != null)
	// 			// conn.close();
	// 			// } catch (SQLException se) {
	// 			// } //do nothing
	// 			try {
	// 				if (conn != null)
	// 					conn.close();
	// 			} catch (SQLException se) {
	// 				return internalServerError(se.toString());
	// 			} // end finally try
	// 		} // end try
	// 	}
	// }



	public static Result addUserProfile() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();



		//String avatarIDstring = json.findPath("avatarID").textValue();
		String skypeID = json.findPath("skypeID").textValue();
		String battlenetID = json.findPath("battlenetID").textValue();
		String steamID = json.findPath("steamID").textValue();
		String twitchID = json.findPath("twitchID").textValue();
		String uplayID = json.findPath("uplayID").textValue();
		String favouritegames = json.findPath("favouritegames").textValue();
		String userbio = json.findPath("about").textValue();


		String currentUser = session("connected");
		try {

			conn = DB.getConnection();

			//int avatarID = Integer.parseInt(avatarIDstring);
			String insertIntoDatabase = "INSERT INTO UserProfile (favouritegames, userbio, skypeID, steamID, battlenetID, uplayID, twitchID) VALUES(?,?,?,?,?,?,?)";
			
			preparedStatement = conn.prepareStatement(insertIntoDatabase);


			preparedStatement.setString(1, favouritegames);
			preparedStatement.setString(2, userbio);
			preparedStatement.setString(3, skypeID);
			preparedStatement.setString(4, steamID);
			preparedStatement.setString(5, battlenetID);
			preparedStatement.setString(6, uplayID);
			preparedStatement.setString(7, twitchID);
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

	// public static Result editUserProfile() {

	// 	Connection conn = null;
	// 	PreparedStatement preparedStatement = null;
	// 	JsonNode json = request().body().asJson();

	// 	String tournamentID = json.findPath("id").textValue();
	// 	String tournamentName = json.findPath("name").textValue();
	// 	String tournamentAmount = json.findPath("amount").textValue();
	// 	JsonNode tournamentTeams = json.findPath("teams");
	// 	JsonNode tournamentResults = json.findPath("results");

	// 	String tournamentData1 = tournamentTeams.toString();
	// 	String tournamentData2 = tournamentResults.toString();
	// 	String tournamentData = "{teams:" + tournamentData1 + ",results:"
	// 			+ tournamentData2 + "}";

	// 	String currentUser = session("connected");

	// 	try {
	// 		if (tournamentID == null || tournamentID.isEmpty()) {
	// 			throw new SQLException();
	// 		}
	// 		conn = DB.getConnection();

	// 		int parsedID = Integer.parseInt(tournamentID);
	// 		int teamAmount = Integer.parseInt(tournamentAmount);

	// 		String insertIntoDatabase = "UPDATE ETournament SET tournamentData=?, tournamentName=?, teamAmount=? WHERE tournamentID=?";
	// 		preparedStatement = conn.prepareStatement(insertIntoDatabase);

	// 		preparedStatement.setString(1, tournamentData);
	// 		preparedStatement.setString(2, tournamentName);
	// 		preparedStatement.setInt(3, teamAmount);
	// 		preparedStatement.setInt(4, parsedID);

	// 		preparedStatement.executeUpdate();
	// 		return ok();
	// 	} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
	// 		return badRequest(ice.toString());
	// 	} catch (NumberFormatException nfe) {
	// 		return badRequest(nfe.toString());
	// 	} catch (SQLException se) {
	// 		// Handle sql errors
	// 		return internalServerError(se.toString());
	// 	} catch (Exception e) {
	// 		// Handle errors for Class.forName
	// 		return internalServerError(e.toString());
	// 	} finally {
	// 		// finally block used to close resources
	// 		// try {
	// 		// if (preparedStatement != null)
	// 		// conn.close();
	// 		// } catch (SQLException se) {
	// 		// } //do nothing
	// 		try {
	// 			if (conn != null)
	// 				conn.close();
	// 		} catch (SQLException se) {
	// 			return internalServerError(se.toString());
	// 		} // end finally try
	// 	} // end try
// 	 }

 }
