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
import play.mvc.*;
import play.db.*;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;

public class TournamentDatabase extends Controller {

	public static Tournament getTournamentAdmin(Integer id) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;

		Tournament t = new Tournament();

		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM ETournament WHERE tournamentID=?;";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.isBeforeFirst()) {
				rs.next();
				t.tournamentcreator = rs.getString("admin");
			}

			if (t.tournamentcreator == null) {
				return null;
			}

			return t;

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

	public static Tournament getTournament(Integer id) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;

		Tournament t = new Tournament();

		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM ETournament WHERE tournamentID=? ORDER BY created_date DESC";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.isBeforeFirst()) {
				rs.next();
				t.tournamentID = rs.getInt("tournamentID");
				t.tournamentname = rs.getString("tournamentName");
				t.participant_count = rs.getInt("teamAmount");
				t.tournamentcreator = rs.getString("admin");
				t.tournamentdata = rs.getString("tournamentData");

			}

			if (t.tournamentdata == null) {
				return null;
			}

			return t;

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

	public static Result getTournaments() {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Tournament> tList = new ArrayList<Tournament>();
			try {

				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM ETournament WHERE 1 ORDER BY created_date DESC";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				ResultSet rs = preparedStatement.executeQuery();
				// boolean next =

				while (rs.next()) {
					Tournament t = new Tournament();
					t.tournamentname = rs.getString("tournamentName");
					t.participant_count = rs.getInt("teamAmount");
					t.tournamentcreator = rs.getString("admin");
					t.tournamentdata = rs.getString("tournamentData");
					t.tournamentID = rs.getInt("tournamentID");
					tList.add(t);
				}

				rs.close();
				return ok(MainTournamentPage.render(tList));
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
				return badRequest(ice.toString());
			} catch (NumberFormatException nfe) {
				return badRequest(nfe.toString());
			} catch (SQLException se) {
				// Handle sql errors
				return internalServerError(se.toString());
			} catch (Exception e) {
				// Handle errors for Class.forName
				return internalServerError(e.toString());
			} finally {
				// finally block used to close resources
				// try {
				// if (preparedStatement != null)
				// conn.close();
				// } catch (SQLException se) {
				// } //do nothing
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					return internalServerError(se.toString());
				} // end finally try
			} // end try
		}
	}

	public static Result addTournament() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();

		// JsonNode jsonparent = json.findParent("tournamentInfo");
		// String tournamentData = jsonparent.toString();
		// String dataOnly =
		// tournamentData.substring(tournamentData.lastIndexOf(":"));

		String tournamentName = json.findPath("name").textValue();
		String tournamentAmount = json.findPath("amount").textValue();
		JsonNode tournamentTeams = json.findPath("teams");
		JsonNode tournamentResults = json.findPath("results");

		String tournamentData1 = tournamentTeams.toString();
		String tournamentData2 = tournamentResults.toString();
		String tournamentData = "{\"teams\":" + tournamentData1
				+ ",\"results\":" + tournamentData2 + "}";

		String currentUser = session("connected");
		try {

			conn = DB.getConnection();

			int teamAmount = Integer.parseInt(tournamentAmount);
			String insertIntoDatabase = "INSERT INTO ETournament (admin, tournamentData, tournamentName, teamAmount) VALUES(?,?,?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, currentUser);
			preparedStatement.setString(2, tournamentData);
			preparedStatement.setString(3, tournamentName);
			preparedStatement.setInt(4, teamAmount);

			preparedStatement.executeUpdate();
			return ok();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString());
		} catch (NumberFormatException nfe) {
			return badRequest(nfe.toString());
		} catch (SQLException se) {
			// Handle sql errors
			return internalServerError(se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			// finally block used to close resources
			// try {
			// if (preparedStatement != null)
			// conn.close();
			// } catch (SQLException se) {
			// } //do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}

	public static Result editTournament() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();

		String tournamentID = json.findPath("id").textValue();
		String tournamentName = json.findPath("name").textValue();
		String tournamentAmount = json.findPath("amount").textValue();
		JsonNode tournamentTeams = json.findPath("teams");
		JsonNode tournamentResults = json.findPath("results");

		String tournamentData1 = tournamentTeams.toString();
		String tournamentData2 = tournamentResults.toString();
		String tournamentData = "{teams:" + tournamentData1 + ",results:"
				+ tournamentData2 + "}";

		String currentUser = session("connected");

		try {
			if (tournamentID == null || tournamentID.isEmpty()) {
				throw new SQLException();
			}
			conn = DB.getConnection();

			int parsedID = Integer.parseInt(tournamentID);
			int teamAmount = Integer.parseInt(tournamentAmount);

			String insertIntoDatabase = "UPDATE ETournament SET tournamentData=?, tournamentName=?, teamAmount=? WHERE tournamentID=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, tournamentData);
			preparedStatement.setString(2, tournamentName);
			preparedStatement.setInt(3, teamAmount);
			preparedStatement.setInt(4, parsedID);

			preparedStatement.executeUpdate();
			return ok();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString());
		} catch (NumberFormatException nfe) {
			return badRequest(nfe.toString());
		} catch (SQLException se) {
			// Handle sql errors
			return internalServerError(se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			// finally block used to close resources
			// try {
			// if (preparedStatement != null)
			// conn.close();
			// } catch (SQLException se) {
			// } //do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}

	public static Result deleteTournament() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();

		String tournamentID = json.findPath("id").textValue();

		String currentUser = session("connected");

		try {
			if (tournamentID == null || tournamentID.isEmpty()) {
				throw new SQLException();
			}
			conn = DB.getConnection();

			int parsedID = Integer.parseInt(tournamentID);

			String insertIntoDatabase = "DELETE FROM ETournament WHERE tournamentID=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setInt(1, parsedID);

			preparedStatement.executeUpdate();
			return ok();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString());
		} catch (NumberFormatException nfe) {
			return badRequest(nfe.toString());
		} catch (SQLException se) {
			// Handle sql errors
			return internalServerError(se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			// finally block used to close resources
			// try {
			// if (preparedStatement != null)
			// conn.close();
			// } catch (SQLException se) {
			// } //do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}

	public static Result getMyTournaments() {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Tournament> tList = new ArrayList<Tournament>();
			try {
				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM ETournament et WHERE admin=? ORDER BY created_date DESC";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				preparedStatement.setString(1, currentUser);
				ResultSet rs = preparedStatement.executeQuery();
				// boolean next =

				while (rs.next()) {
					Tournament t = new Tournament();
					t.tournamentname = rs.getString("tournamentName");
					t.participant_count = rs.getInt("teamAmount");
					t.tournamentcreator = rs.getString("admin");
					t.tournamentdata = rs.getString("tournamentData");
					t.tournamentID = rs.getInt("tournamentID");
					tList.add(t);
				}

				rs.close();
				return ok(MyTournamentPage.render(tList));
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
				return badRequest(ice.toString());
			} catch (NumberFormatException nfe) {
				return badRequest(nfe.toString());
			} catch (SQLException se) {
				// Handle sql errors
				return internalServerError(se.toString());
			} catch (Exception e) {
				// Handle errors for Class.forName
				return internalServerError(e.toString());
			} finally {
				// finally block used to close resources
				// try {
				// if (preparedStatement != null)
				// conn.close();
				// } catch (SQLException se) {
				// } //do nothing
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					return internalServerError(se.toString());
				} // end finally try
			} // end try
		}
	}

	public static List<Tournament> getTournamentsMainPage() {
		  	
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		List<Tournament> tList = new ArrayList<Tournament>();
			
			
		try {
	
			conn = DB.getConnection();
           
			String selectTournaments = "SELECT * FROM ETournament ORDER BY created_date DESC LIMIT 5";

			preparedStatement = conn.prepareStatement(selectTournaments);
			ResultSet rs = preparedStatement.executeQuery();
				
			while (rs.next()) {
				Tournament t = new Tournament();
				t.tournamentname = rs.getString("tournamentName");
				t.participant_count = rs.getInt("teamAmount");
				t.tournamentcreator = rs.getString("admin");
				t.tournamentdata = rs.getString("tournamentData");
				t.tournamentID = rs.getInt("tournamentID");
				tList.add(t);
			}

			rs.close();
		
			return tList;
			
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return null;
		} catch (NumberFormatException nfe) {
	        return null;
		} catch (SQLException se) {
    		return null;
		} catch (Exception e) {
	    	return null;
		} finally {
				// finally block used to close resources
				// try {
				// if (preparedStatement != null)
				// conn.close();
				// } catch (SQLException se) {
				// } //do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return null;
			} // end finally try
		} // end try
	 }		
}