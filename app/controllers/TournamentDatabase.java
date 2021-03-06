package controllers;

import models.*;
import java.sql.*;

import com.fasterxml.jackson.databind.JsonNode;
import views.html.*;
import play.db.DB;
import play.mvc.*;
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
				t.information = rs.getString("information");
				t.logo = rs.getString("logo");
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

	public static Result getTournaments(int page) {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Tournament> tList = new ArrayList<Tournament>();
			try {

				page = (page - 1) * 10;

				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM ETournament ORDER BY created_date DESC LIMIT 10 OFFSET ? ";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				preparedStatement.setInt(1, page);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					Tournament t = new Tournament();
					t.tournamentname = rs.getString("tournamentName");
					t.participant_count = rs.getInt("teamAmount");
					t.tournamentcreator = rs.getString("admin");
					t.tournamentdata = rs.getString("tournamentData");
					t.tournamentID = rs.getInt("tournamentID");
					t.information =rs.getString("information");
					t.logo =rs.getString("logo");
					tList.add(t);
				}
				rs.close();

				String rowCountStatement = "SELECT COUNT(*) FROM ETournament";
				preparedStatement = conn.prepareStatement(rowCountStatement);

				ResultSet rc = preparedStatement.executeQuery();
				rc.next();
				int rowcount = rc.getInt(1);
				rc.close();
				return ok(MainTournamentPage.render(tList, rowcount));
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
		String information = json.findPath("info").textValue();
		String logo = json.findPath("logo").textValue();
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
			String insertIntoDatabase = "INSERT INTO ETournament (admin, tournamentData, tournamentName, teamAmount, information, logo) VALUES(?,?,?,?,?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, currentUser);
			preparedStatement.setString(2, tournamentData);
			preparedStatement.setString(3, tournamentName);
			preparedStatement.setInt(4, teamAmount);
			preparedStatement.setString(5, information);
			preparedStatement.setString(6, logo);

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
		String information = json.findPath("information").textValue();
		String logo = json.findPath("logo").textValue();
		JsonNode tournamentTeams = json.findPath("teams");
		JsonNode tournamentResults = json.findPath("results");

		String tournamentData1 = tournamentTeams.toString();
		String tournamentData2 = tournamentResults.toString();
		String tournamentData = "{teams:" + tournamentData1 + ",results:"
				+ tournamentData2 + "}";

		try {
			if (tournamentID == null || tournamentID.isEmpty()) {
				throw new SQLException();
			}
			conn = DB.getConnection();

			int parsedID = Integer.parseInt(tournamentID);
			int teamAmount = Integer.parseInt(tournamentAmount);

			String insertIntoDatabase = "UPDATE ETournament SET tournamentData=?, tournamentName=?, teamAmount=?, information=?, logo=? WHERE tournamentID=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, tournamentData);
			preparedStatement.setString(2, tournamentName);
			preparedStatement.setInt(3, teamAmount);
			preparedStatement.setString(4, information);
			preparedStatement.setString(5, logo);
			preparedStatement.setInt(6, parsedID);

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
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			} // end finally try
		} // end try
	}
	

	public static Result getMyTournaments(int page) {
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

				page = (page - 1) * 10;

				String insertIntoDatabase = "SELECT * FROM ETournament et WHERE admin=? ORDER BY created_date DESC LIMIT 10 OFFSET ?";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				preparedStatement.setString(1, currentUser);
				preparedStatement.setInt(2, page);
				ResultSet rs = preparedStatement.executeQuery();
				// boolean next =

				while (rs.next()) {
					Tournament t = new Tournament();
					t.tournamentname = rs.getString("tournamentName");
					t.participant_count = rs.getInt("teamAmount");
					t.tournamentcreator = rs.getString("admin");
					t.tournamentdata = rs.getString("tournamentData");
					t.tournamentID = rs.getInt("tournamentID");
					t.information =rs.getString("information");
					t.logo =rs.getString("logo");
					tList.add(t);
				}
				rs.close();
				
				String rowCountStatement = "SELECT COUNT(*) FROM ETournament WHERE admin=?";
				preparedStatement = conn.prepareStatement(rowCountStatement);
				preparedStatement.setString(1, currentUser);

				ResultSet rc = preparedStatement.executeQuery();
				rc.next();
				int rowcount = rc.getInt(1);
				rc.close();
				
				return ok(MyTournamentPage.render(tList, rowcount));
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
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					return internalServerError(se.toString());
				} // end finally try
			} // end try
		}
	}

	public static List<Tournament> getJoinedTournaments(String userUrl) {	
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		List<Tournament> tList = new ArrayList<Tournament>();
			
		try {
			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM ETournament JOIN TournamentInvite ON ETournament.tournamentID=TournamentInvite.tournamentID WHERE participant=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, userUrl);
			ResultSet rs = preparedStatement.executeQuery();


			while (rs.next()) {
				Tournament t = new Tournament();
				t.tournamentname = rs.getString("tournamentName");
				t.participant_count = rs.getInt("teamAmount");
				t.tournamentcreator = rs.getString("admin");
				t.tournamentdata = rs.getString("tournamentData");
				t.tournamentID = rs.getInt("tournamentID");
				t.information =rs.getString("information");
				t.logo =rs.getString("logo");
				tList.add(t);
			}
			



			rs.close();
			return tList;
			
			}catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
					return null;
			} catch (NumberFormatException nfe) {
		            return null;
			} catch (SQLException se) {
    		        return null;
			} catch (NullPointerException npe) {
	    	        return null;
			} catch (Exception e) {
				return null;
			}
			finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					return null;
				} // end finally try
			} // end try
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
				t.information =rs.getString("information");
				t.logo =rs.getString("logo");
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
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return null;
			} // end finally try
		} // end try
	 }



	public static List<Participant> getParticipants(int tID) {	
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		List<Participant> pList = new ArrayList<Participant>();
		int acceptedCheck = 1;

		 try {
		 	conn = DB.getConnection();
		 	String insertIntoDatabase = "SELECT username FROM TournamentInvite JOIN UserProfile ON TournamentInvite.participant = UserProfile.username WHERE TournamentInvite.tournamentID=? AND TournamentInvite.accepted=?";

		 	preparedStatement = conn.prepareStatement(insertIntoDatabase);
		 	preparedStatement.setInt(1, tID);
		 	preparedStatement.setInt(2, acceptedCheck);
		 	ResultSet rs = preparedStatement.executeQuery();


		 	while (rs.next()) {
		 	    Participant p = new Participant();
		 	    
		 	    p.username = rs.getString("username");
	            p.tournamentID = tID;
	            
		 		pList.add(p);
		 	}
		 	rs.close();
			return pList;
			
			}catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
					return null;
			} catch (NumberFormatException nfe) {
		            return null;
			} catch (SQLException se) {
    		        return null;
			} catch (NullPointerException npe) {
	    	        return null;
			} catch (Exception e) {
				return null;
			}
			finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					return null;
				} // end finally try
			} // end try
		}
		
		public static List<Participant> getPending(int tID) {	
		
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		List<Participant> pList = new ArrayList<Participant>();
		int acceptedCheck = 0;

		 try {
		 	conn = DB.getConnection();
		 	String insertIntoDatabase = "SELECT username FROM TournamentInvite JOIN UserProfile ON TournamentInvite.participant = UserProfile.username WHERE TournamentInvite.tournamentID=? AND TournamentInvite.accepted=?";

		 	preparedStatement = conn.prepareStatement(insertIntoDatabase);
		 	preparedStatement.setInt(1, tID);
		 	preparedStatement.setInt(2, acceptedCheck);
		 	ResultSet rs = preparedStatement.executeQuery();


		 	while (rs.next()) {
		 	    Participant p = new Participant();
		 	    
		 	    p.username = rs.getString("username");
	            p.tournamentID = tID;
	            
		 		pList.add(p);
		 	}
			



		 	rs.close();
			return pList;
			
			}catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
					return null;
			} catch (NumberFormatException nfe) {
		            return null;
			} catch (SQLException se) {
    		        return null;
			} catch (NullPointerException npe) {
	    	        return null;
			} catch (Exception e) {
				return null;
			}
			finally {
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