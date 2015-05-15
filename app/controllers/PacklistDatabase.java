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

public class PacklistDatabase extends Controller {

	public static Result editPacklist() {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();
		
		String username = session("connected");
		String personalComputer = json.findPath("pc").textValue();
		String powerstrip = json.findPath("powerstrip").textValue();
		String screenAndCables = json.findPath("screenAnd").textValue();
		String keyboard = json.findPath("keyboard").textValue();

//Testutskrift i activators system bara för att se att 1:orna och 0:orna blir rätt.  
		System.out.println("EDIT -> PC: " + personalComputer + " , " + "POWERSTRIP: " + powerstrip + " , " + "SCREENAND: " + screenAndCables);

		try {

			conn = DB.getConnection();

			String insertIntoDatabase = "UPDATE Packlist SET personalComputer = ?, powerstrip = ?, screenAndCables =? WHERE username = ?";
						
			preparedStatement = conn.prepareStatement(insertIntoDatabase);


			preparedStatement.setString(1, personalComputer);
			preparedStatement.setString(2, powerstrip);
			preparedStatement.setString(3, screenAndCables);
			preparedStatement.setString(4, username);
			
			
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


	











public static Result addPacklist() {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();
		
		String username = session("connected");
		String personalComputer = json.findPath("pc").textValue();
		String powerstrip = json.findPath("powerstrip").textValue();
		String screenAndCables = json.findPath("screenAnd").textValue();
		String keyboard = json.findPath("keyboard").textValue();
		String headset = json.findPath("headset").textValue();
		String networkCable = json.findPath("networkCable").textValue();
		String screwdriver = json.findPath("screwdriver").textValue();
		String cable_Ties = json.findPath("cable_Ties").textValue();
		String ductTape = json.findPath("ductTape").textValue();
		String gameConsole = json.findPath("gameConsole").textValue();
		String mobilePhone = json.findPath("mobilePhone").textValue();
		String camera = json.findPath("camera").textValue();
		String flashlight = json.findPath("flashlight").textValue();
//Testutskrift i activators system bara för att se att 1:orna och 0:orna blir rätt.  
		System.out.println("ADD -> PC: " + personalComputer + " , " + "POWERSTRIP: " + powerstrip + " , " + "SCREENAND: " + screenAndCables);

		try {

			conn = DB.getConnection();

			String insertIntoDatabase = "INSERT INTO Packlist (username, personalComputer, powerstrip, screenAndCables, keyboard, headset, networkCable, screwdriver, cable_Ties, ductTape, gameConsole, mobilePhone, camera, flashlight) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
						
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, personalComputer);
			preparedStatement.setString(3, powerstrip);
			preparedStatement.setString(4, screenAndCables);
			preparedStatement.setString(5, keyboard);
			preparedStatement.setString(6, headset);
			preparedStatement.setString(7, networkCable);
			preparedStatement.setString(8, screwdriver);
			preparedStatement.setString(9, cable_Ties);
			preparedStatement.setString(10, ductTape);
			preparedStatement.setString(11, gameConsole);
			preparedStatement.setString(12, mobilePhone);
			preparedStatement.setString(13, camera);
			preparedStatement.setString(14, flashlight);
			
			
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