package controllers;

import models.*;
import play.*;
import play.mvc.*;

import java.sql.*;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class NotificationDatabase extends Controller {
    
    public static Result invitePlayer() {
		Connection conn = null;
		PreparedStatement preparedStatement;
		
		JsonNode json = request().body().asJson();

		String participant = json.findPath("participant").textValue();
        int intTournamentID = json.findPath("tournamentID").asInt();
        
		try {
            String currentUser = session("connected");
			conn = DB.getConnection();
			
			String dbUser="";
			
			String sql = "SELECT * FROM User u cross join FacebookUser fu where u.username =?or fu.username =? LIMIT 1";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, participant);
			preparedStatement.setString(2, participant);

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.isBeforeFirst()) {
				rs.next();
				dbUser = rs.getString("username");
			}
			
			if (dbUser == ""){
			    return ok("non-existent");
			}

			
			String insertIntoDatabase = "INSERT INTO TournamentInvite (tournamentID, admin, participant) VALUES(?,?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, intTournamentID);
			preparedStatement.setString(2, currentUser);
			preparedStatement.setString(3, participant);
			preparedStatement.executeUpdate();

			return ok("success");

		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return ok("exists");
		} catch (SQLException se) {
			// Handle sql errors
			return ok("exists");
		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("An error occured");
		} finally {
		    // finally block used to close resources
			// try {
			// if (preparedStatement != null)
			// conn.close();
			// } catch (SQLException se) {
			// }// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			}// end finally try
		}// end try

	}
	
	
	public static Result handleInvite() {
		Connection conn = null;
		PreparedStatement preparedStatement;
		
		JsonNode json = request().body().asJson();
        int intTournamentID = json.findPath("tournamentID").asInt();
        String action = json.findPath("action").textValue();
        
		try {
		    
            String currentUser = session("connected");
			conn = DB.getConnection();
			
			if (action.equals("accept")){
			
    			String updateDatabase = "UPDATE TournamentInvite SET accepted = 1 WHERE tournamentID = ? AND participant = ?";
    			preparedStatement = conn.prepareStatement(updateDatabase);
    			preparedStatement.setInt(1, intTournamentID);
    			preparedStatement.setString(2, currentUser);
    			preparedStatement.executeUpdate();
    			return ok("accepted");
			
			} else if (action.equals("delete")){
			    
    			String deleteRowDatabase = "DELETE FROM TournamentInvite WHERE tournamentID = ? AND participant = ?";
    			preparedStatement = conn.prepareStatement(deleteRowDatabase);
    			preparedStatement.setInt(1, intTournamentID);
    			preparedStatement.setString(2, currentUser);
    			preparedStatement.executeUpdate();
    			return ok("deleted");
			    
			}
			
			return ok("failed");
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return ok("fail");
		} catch (SQLException se) {
			// Handle sql errors
			return ok("fail");
		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("fail");
		} finally {
		    // finally block used to close resources
			// try {
			// if (preparedStatement != null)
			// conn.close();
			// } catch (SQLException se) {
			// }// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			}// end finally try
		}// end try

	}
	
	
    public static Result getMyNotifications() {
    	String currentUser = session("connected");
    	if (currentUser == null) {
    		return unauthorized(LoginUserPage.render("You have to login to access this page!"));
    	} else {
    		Connection conn = null;
    		PreparedStatement preparedStatement = null;
    		List < Notification > notiList = new ArrayList < Notification > ();
    		try {
    
    			conn = DB.getConnection();
    
    			String selectNotifications = "SELECT * FROM TournamentInvite WHERE participant=?";
    			preparedStatement = conn.prepareStatement(selectNotifications);
    			preparedStatement.setString(1, currentUser);
    			ResultSet rs = preparedStatement.executeQuery();
    
    			while (rs.next()) {
    				Notification n = new Notification();
    				n.tournamentID = rs.getInt("tournamentID");
    				n.admin = rs.getString("admin");
    			    n.participant = rs.getString("participant");
    			    n.accepted = rs.getInt("accepted");
    			    n.viewed = rs.getInt("viewed");
    			    
    				notiList.add(n);
    			}
    
    			rs.close();
    			return ok(MyNotifications.render(notiList));
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
    				if (conn != null) conn.close();
    			} catch (SQLException se) {
    				return internalServerError(se.toString());
    			} // end finally try
    		} // end try
    	}
    }
    
}