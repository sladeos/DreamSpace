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
	
	
	public static Result setViewed() {
		Connection conn = null;
		PreparedStatement preparedStatement;
	
		try {
		    
            String currentUser = session("connected");
			conn = DB.getConnection();
			
    			String updateDatabase = "UPDATE TournamentInvite SET viewed = 1 WHERE participant = ?";
    			preparedStatement = conn.prepareStatement(updateDatabase);
    			preparedStatement.setString(1, currentUser);
    			preparedStatement.executeUpdate();
    			
    			updateDatabase = "UPDATE EArenaReply SET viewed = 1 WHERE arenaadmin = ?";
    			preparedStatement = conn.prepareStatement(updateDatabase);
    			preparedStatement.setString(1, currentUser);
    			preparedStatement.executeUpdate();

    			return ok("success");

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
	
	public static Result hideReply() {
		Connection conn = null;
		PreparedStatement preparedStatement;
		
		JsonNode json = request().body().asJson();

		int replyID = json.findPath("replyID").asInt();
	
		try {
		    
            String currentUser = session("connected");
			conn = DB.getConnection();
			
    			String updateDatabase = "UPDATE EArenaReply SET hidden = 1 WHERE replyID = ?";
    			preparedStatement = conn.prepareStatement(updateDatabase);
    			preparedStatement.setInt(1, replyID);
    			preparedStatement.executeUpdate();
    			
    			return ok("success");

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
    		PreparedStatement preparedIStatement = null;
    		PreparedStatement preparedRStatement = null;
    		List < InviteNotification > notiList = new ArrayList < InviteNotification > ();
    		List < ReplyNotification > notrList = new ArrayList < ReplyNotification > ();
    		try {
    
    			conn = DB.getConnection();
    
    			String selectINotifications = "SELECT * FROM TournamentInvite WHERE participant=? AND accepted=0";
    			preparedIStatement = conn.prepareStatement(selectINotifications);
    			preparedIStatement.setString(1, currentUser);
    			ResultSet rsI = preparedIStatement.executeQuery();
    
    			while (rsI.next()) {
    				InviteNotification n = new InviteNotification();
    				n.tournamentID = rsI.getInt("tournamentID");
    				n.admin = rsI.getString("admin");
    			    n.participant = rsI.getString("participant");
    			    n.accepted = rsI.getInt("accepted");
    			    n.viewed = rsI.getInt("viewed");
    			    
    				notiList.add(n);
    			}
    			rsI.close();
    			
    			
    		    String selectRNotifications = "SELECT * FROM EArenaReply WHERE arenaadmin=? AND hidden=0";
    			preparedRStatement = conn.prepareStatement(selectRNotifications);
    			preparedRStatement.setString(1, currentUser);
    			ResultSet rsR = preparedRStatement.executeQuery();
    
    			while (rsR.next()) {
    				ReplyNotification r = new ReplyNotification();
    				r.replyID = rsR.getInt("replyID");
    				r.arenaID = rsR.getInt("arenaID");
    				r.arenaadmin = rsR.getString("arenaadmin");
    			    r.username = rsR.getString("username");
    			    r.created_date = rsR.getString("created_date");
    			    r.created_date = r.created_date.substring(0, r.created_date.lastIndexOf("."));
    			    r.viewed = rsR.getInt("viewed");
    			    
    				notrList.add(r);
    			}
    
    			rsR.close();
    			return ok(MyNotifications.render(notiList, notrList));
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
    
     public static Result getNotificationAmt() {
    	String currentUser = session("connected");
    
    		Connection conn = null;
    		PreparedStatement preparedStatement = null;
            int amt = 0;
            
            
    		try {
    
    			conn = DB.getConnection();
    
    			String selectNotifications = "SELECT (SELECT COUNT(*) FROM EArenaReply WHERE arenaadmin=? AND viewed=0) AS t1_amount, (SELECT COUNT(*) FROM TournamentInvite WHERE participant=? AND viewed=0) AS t2_amount";
    			preparedStatement = conn.prepareStatement(selectNotifications);
    			preparedStatement.setString(1, currentUser);
    			preparedStatement.setString(2, currentUser);
    			ResultSet rs = preparedStatement.executeQuery();
    
    			while (rs.next()) {
                amt += rs.getInt("t1_amount");
                amt += rs.getInt("t2_amount");
    			}
    			rs.close();
    			
    			return ok("" + amt);
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