package controllers;

import models.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;

import java.sql.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Arrays;

import play.libs.Json;
import play.mvc.*;

public class UserDatabase extends Controller {
	private final static int ITERATION_NUMBER = 1000;
    
   public static Result checkName() {

		JsonNode json = request().body().asJson();

		String nameDirty = json.findPath("username").textValue();
	
		String nameOnly = nameDirty.substring(nameDirty.lastIndexOf("=") + 1);
		
		Connection conn = null;
		Statement stmt = null;
		String dbUser = null;

		String sql = "SELECT * FROM `User` WHERE `username` = " + "'"
				+ nameOnly + "'";


		try {

			conn = DB.getConnection();
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			if (rs.isBeforeFirst()) {
				rs.next();

				dbUser = rs.getString("username");
			

		}
			return ok(dbUser);
		} catch (SQLException se) {
			return ok("null");
		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("null");
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothin
		}

	}

   
	// Create user and send to database
	public static Result addUser() {

		if (Form.form(User.class).bindFromRequest().hasErrors()) {
			return badRequest(NewUserPage.render("Leave no form empty"));
		}

		User user = Form.form(User.class).bindFromRequest().get();
		ObjectNode result = Json.newObject();
		Connection conn = null;
		Statement stmt = null;
		String userUsername = user.username;
		String userPassword = user.password;
		String userEmail = user.email;
		String userConfirmPass = user.confirmPass;


        if (!userPassword.equals(userConfirmPass)){
        return badRequest(NewUserPage
					.render("Passwords didn't match"));
	    }
	    
		if (userUsername.matches("^.*[^a-zA-Z0-9].*$")) {
			return badRequest(NewUserPage
					.render("Please only use letters and numbers for the username"));
		}

		try {
			// Secure randm generation
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// 64bit salt generation
			byte[] bSalt = new byte[8];
			random.nextBytes(bSalt);
			// Hash method called
			byte[] bDigest = getHash(ITERATION_NUMBER, userPassword, bSalt);
			String sDigest = byteToBase64(bDigest);
			String sSalt = byteToBase64(bSalt);
			conn = DB.getConnection();
			stmt = conn.createStatement();

			String insertIntoDatabase = "INSERT INTO User"
					+ "(USERNAME, EMAIL, PASSWORD, SALT) " + "VALUES" + "(" + "'"
					+ userUsername + "'" + "," + "'" + userEmail + "'" + "," + "'" + sDigest + "'" + ","
					+ "'" + sSalt + "'" + ")";

			// execute insert SQL stetement
			stmt.executeUpdate(insertIntoDatabase);

			// user.save();
			session("connected", userUsername);
			return redirect(routes.Application.mainMethod());
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice){
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
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			}// end finally try
		}// end try

	}

	// Method for user login. Requests database reply for entered username,
	// reacts accordingly.
	public static Result getUser() {

		Connection conn = null;
		Statement stmt = null;

		if (Form.form(User.class).bindFromRequest().hasErrors()) {
			return badRequest(LoginUserPage.render("WERFSDFSD"));
		}

		User user = Form.form(User.class).bindFromRequest().get();

		String userUsername = user.username;
		String userPassword = user.password;

		try {

			conn = DB.getConnection();
			stmt = conn.createStatement();

			String sql = "SELECT * FROM `User` WHERE `username` = " + "'"
					+ userUsername + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.isBeforeFirst()) {
				rs.next();

				String username = rs.getString("username");
				String pwDigest = rs.getString("password");
				String salt = rs.getString("salt");

				if (pwDigest == null || salt == null) {
					throw new SQLException(
							"Database inconsistant Salt or Digested Password altered");
				}

				byte[] bDigest = base64ToByte(pwDigest);
				byte[] bSalt = base64ToByte(salt);

				byte[] proposedDigest = getHash(ITERATION_NUMBER, userPassword,
						bSalt);
				if (Arrays.equals(proposedDigest, bDigest)) {
					rs.close();
					session("connected", userUsername);
					return redirect(routes.Application.mainMethod());
				}
			}

			rs.close();
			return ok(LoginUserPage.render("Wrong user/pass"));
			
		} catch (SQLException se) {
			return badRequest(NewUserPage
					.render("Error: " + se.toString()));
		} catch (Exception e) {
			// Handle errors for Class.forName
			return internalServerError(e.toString());
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return internalServerError(se.toString());
			}// end finally try
		}// end try

	}


	private static byte[] getHash(int iterationNumber, String password,
			byte[] salt) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNumber; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	// From a base 64 representation, returns the corresponding byte[]
	public static byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	// From a byte[] returns a base 64 representation
	public static String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}

}