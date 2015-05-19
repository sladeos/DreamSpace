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

public class InviteDatabase extends Controller {
    
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
    
}