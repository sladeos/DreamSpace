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

		try {

			conn = DB.getConnection();

			String insertIntoDatabase = "UPDATE Packlist SET personalComputer=?, powerstrip=?, screenAndCables=?, keyboard=?, headset=?, networkCable=?, screwdriver=?, cableTies=?, ductTape=?, gameConsole=?, mobilePhone=?, camera=?, flashlight=?, chair=?, safe=?, installationMedia=?, drivers=?, games=?, antiVirus=?, toothbrush=?, soap=?, handCream=?, shampoo=?, deo=?, aspirin=?, earplugs=?, medication=?, clothes=?, swimwear=?, sheets=?, sleepingBag=?, pillow=?, mattress=? WHERE username = ?";
						
			preparedStatement = conn.prepareStatement(insertIntoDatabase);


			preparedStatement.setString(1, personalComputer);
			preparedStatement.setString(2, powerstrip);
			preparedStatement.setString(3, screenAndCables);
			preparedStatement.setString(4, keyboard);
			preparedStatement.setString(5, headset);
			preparedStatement.setString(6, networkCable);
			preparedStatement.setString(7, screwdriver);
			preparedStatement.setString(8, cableTies);
			preparedStatement.setString(9, ductTape);
			preparedStatement.setString(10, gameConsole);
			preparedStatement.setString(11, mobilePhone);
			preparedStatement.setString(12, camera);
			preparedStatement.setString(13, flashlight);
			preparedStatement.setString(14, chair);
			preparedStatement.setString(15, safe);
			preparedStatement.setString(16, installationMedia);
			preparedStatement.setString(17, drivers);
			preparedStatement.setString(18, games);
			preparedStatement.setString(19, antiVirus);
			preparedStatement.setString(20, toothbrush);
			preparedStatement.setString(21, soap);
			preparedStatement.setString(22, handCream);
			preparedStatement.setString(23, shampoo);
			preparedStatement.setString(24, deo);
			preparedStatement.setString(25, aspirin);
			preparedStatement.setString(26, earplugs);
			preparedStatement.setString(27, medication);
			preparedStatement.setString(28, clothes);
			preparedStatement.setString(29, swimwear);
			preparedStatement.setString(30, sheets);
			preparedStatement.setString(31, sleepingBag);
			preparedStatement.setString(32, pillow);
			preparedStatement.setString(33, mattress);
			preparedStatement.setString(34, username);
			
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
		String cableTies = json.findPath("cableTies").textValue();
		String ductTape = json.findPath("ductTape").textValue();
		String gameConsole = json.findPath("gameConsole").textValue();
		String mobilePhone = json.findPath("mobilePhone").textValue();
		String camera = json.findPath("camera").textValue();
		String flashlight = json.findPath("flashlight").textValue();
		String chair = json.findPath("chair").textValue();
		String safe = json.findPath("safe").textValue();
		String installationMedia = json.findPath("installationMedia").textValue();
		String drivers = json.findPath("drivers").textValue();
		String games = json.findPath("games").textValue();
		String antiVirus = json.findPath("antiVirus").textValue();
		String toothbrush = json.findPath("toothbrush").textValue();
		String soap = json.findPath("soap").textValue();
		String handCream = json.findPath("handCream").textValue();
		String shampoo = json.findPath("shampoo").textValue();
		String deo = json.findPath("deo").textValue();
		String aspirin = json.findPath("aspirin").textValue();
		String earplugs = json.findPath("earplugs").textValue();
		String medication = json.findPath("medication").textValue();
		String clothes = json.findPath("clothes").textValue();
		String swimwear = json.findPath("swimwear").textValue();
		String sheets = json.findPath("sheets").textValue();
		String sleepingBag = json.findPath("sleepingBag").textValue();
		String pillow = json.findPath("pillow").textValue();
		String mattress = json.findPath("mattress").textValue();

//Testutskrift i activators system bara för att se att 1:orna och 0:orna blir rätt.  
		System.out.println("ADD -> PC: " + personalComputer + " , " + "POWERSTRIP: " + powerstrip + " , " + "SCREENAND: " + screenAndCables);

		try {

			conn = DB.getConnection();

			String insertIntoDatabase = "INSERT INTO Packlist (username, personalComputer, powerstrip, screenAndCables, keyboard, headset, networkCable, screwdriver, cableTies, ductTape, gameConsole, mobilePhone, camera, flashlight, chair, safe, installationMedia, drivers, games, antiVirus, toothbrush, soap, handCream, shampoo, deo, aspirin, earplugs, medication, clothes, swimwear, sheets, sleepingBag, pillow, mattress) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
							
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, personalComputer);
			preparedStatement.setString(3, powerstrip);
			preparedStatement.setString(4, screenAndCables);
			preparedStatement.setString(5, keyboard);
			preparedStatement.setString(6, headset);
			preparedStatement.setString(7, networkCable);
			preparedStatement.setString(8, screwdriver);
			preparedStatement.setString(9, cableTies);
			preparedStatement.setString(10, ductTape);
			preparedStatement.setString(11, gameConsole);
			preparedStatement.setString(12, mobilePhone);
			preparedStatement.setString(13, camera);
			preparedStatement.setString(14, flashlight);
			preparedStatement.setString(15, chair);
			preparedStatement.setString(16, safe);
			preparedStatement.setString(17, installationMedia);
			preparedStatement.setString(18, drivers);
			preparedStatement.setString(19, games);
			preparedStatement.setString(20, antiVirus);
			preparedStatement.setString(21, toothbrush);
			preparedStatement.setString(22, soap);
			preparedStatement.setString(23, handCream);
			preparedStatement.setString(24, shampoo);
			preparedStatement.setString(25, deo);
			preparedStatement.setString(26, aspirin);
			preparedStatement.setString(27, earplugs);
			preparedStatement.setString(28, medication);
			preparedStatement.setString(29, clothes);
			preparedStatement.setString(30, swimwear);
			preparedStatement.setString(31, sheets);
			preparedStatement.setString(32, sleepingBag);
			preparedStatement.setString(33, pillow);
			preparedStatement.setString(34, mattress);
			
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

 public static Packlist getPacklist(String user) {
 		Connection conn = null;
 		PreparedStatement preparedStatement = null;
		
 		Packlist pL = new Packlist();
 		String username = session("connected");

 		try {
 			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM Packlist WHERE username=?;";
 			preparedStatement = conn.prepareStatement(insertIntoDatabase);
 			preparedStatement.setString(1, user);

 			ResultSet rs = preparedStatement.executeQuery();

 			if (rs.isBeforeFirst()) {
				rs.next();
 				pL.pc = rs.getString("personalComputer");
 				pL.powerstrip = rs.getString("powerstrip");
				pL.screenAndCables = rs.getString("screenAndCables");
 				pL.keyboard = rs.getString("keyboard");
 				pL.headset = rs.getString("headset");
 				pL.networkCable = rs.getString("networkCable");
				pL.screwdriver = rs.getString("screwdriver");
				pL.cableTies = rs.getString("cableTies");
				pL.ductTape = rs.getString("ductTape");
				pL.gameConsole = rs.getString("gameConsole");
				pL.mobilePhone = rs.getString("mobilePhone");
 				pL.camera = rs.getString("camera");
 				pL.flashlight = rs.getString("flashlight");
 				pL.chair = rs.getString("chair");
 				pL.safe = rs.getString("safe");
				pL.installationMedia = rs.getString("installationMedia");
 				pL.drivers = rs.getString("drivers");
 				pL.games = rs.getString("games");
 				pL.antiVirus = rs.getString("antiVirus");
 				pL.toothbrush = rs.getString("toothbrush");
 				pL.soap = rs.getString("soap");
 				pL.handCream = rs.getString("handCream");
 				pL.shampoo = rs.getString("shampoo");
 				pL.deo = rs.getString("deo");
 				pL.aspirin = rs.getString("aspirin");
 				pL.earplugs = rs.getString("earplugs");
 				pL.medication = rs.getString("medication");
 				pL.clothes = rs.getString("clothes");
 				pL.swimwear = rs.getString("swimwear");
 				pL.sheets = rs.getString("sheets");
 				pL.sleepingBag = rs.getString("sleepingBag");
 				pL.pillow = rs.getString("pillow");
 				pL.mattress = rs.getString("mattress");
 				pL.username = rs.getString("username");

 			}

 		return pL;
		
 		} catch (SQLException se) {
 			return null;
 		} catch (Exception e) {
 			// Handle errors for Class.forName
 			return null;
 		} finally {
 			try {
 				if (preparedStatement != null)
 					conn.close();
 			} catch (SQLException se) {
 			} // end finally try
 		} // end try
 	}

}