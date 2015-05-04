package controllers;

import models.*;
import play.*;
import play.mvc.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;

public class EArenaDatabase extends Controller {

	public static Result addEArenaAd() {
		Connection conn = null;
		PreparedStatement preparedStatement;

		 if (Form.form(EArenaAd.class).bindFromRequest().hasErrors()) {
		 return badRequest(CreateArenaAd.render());
		 }

		EArenaAd ead = Form.form(EArenaAd.class).bindFromRequest().get();
		String arenaName = ead.arenaName;
		String information = ead.information;
		String gameName = ead.gameName;
		int playersRequired = ead.playersRequired;
		String admin = session("connected");
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "INSERT INTO EArena (arenaname, admin, arenainformation, playersrequired, gamename) VALUES(?,?,?,?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, arenaName);
			preparedStatement.setString(2, admin);
			preparedStatement.setString(3, information);
			preparedStatement.setInt(4, playersRequired);
			preparedStatement.setString(5, gameName);
			preparedStatement.executeUpdate();

			return redirect(routes.EArenaDatabase.getEArenaAds());

		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(NewUserPage
					.render("User with that name already exists"));
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
			// }// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			}// end finally try
		}// end try

	}

	public static Result getEArenaAds() {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<EArenaAd> adList = new ArrayList<EArenaAd>();
			try {

				conn = DB.getConnection();

				String selectAdminAds = "SELECT * FROM EArena";
				preparedStatement = conn.prepareStatement(selectAdminAds);
				// preparedStatement.setString(1, currentUser);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					EArenaAd a = new EArenaAd();
					a.arenaID = rs.getInt("arenaID");
					a.arenaName = rs.getString("arenaname");
					a.information = rs.getString("arenainformation");
					a.gameName = rs.getString("gamename");
					a.playersRequired = rs.getInt("playersrequired");
					a.admin = currentUser;
					adList.add(a);
				}

				rs.close();
				return ok(MainEArenaPage.render(adList));
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

	public static EArenaAd getIndividualEArena(Integer id) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;

		EArenaAd a = new EArenaAd();
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM EArena WHERE arenaID=?;";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();
			if (rs.isBeforeFirst()) {
				rs.next();
				a.arenaName = rs.getString("arenaname");
				a.information = rs.getString("arenainformation");
				a.gameName = rs.getString("gamename");
				a.playersRequired = rs.getInt("playersrequired");
				a.admin = rs.getString("admin");
			}

			if (a.admin == null) {
				return null;
			}

			return a;

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

}
