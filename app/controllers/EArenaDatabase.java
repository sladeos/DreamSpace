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
import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EArenaDatabase extends Controller {
    
    private static boolean first = true;
    
    private static String wildcard(String str){
        
        if(!str.isEmpty()){
            return  "%" + str + "%";
            }
        
        return "";
    } 
    
    private static String generateSQL(String str, String str2){
       if(!str.isEmpty()){
           if (first == false){
               if (str2 == "arenainformation"){
                  return " OR " + str2 + " LIKE ?)";
               } else if (str2 == "created_date"){
                   return " AND " + str2 + " >= DATE_SUB(NOW(), INTERVAL ? MINUTE)";
               } else {
               return " AND " + str2 + " LIKE ?";
               }
            } else if (str2 == "arenaname"){
               first = false;
               return " (" + str2 + " LIKE ?";
            } else if (str2 == "created_date"){
                first = false;
                return " " + str2 + " >= DATE_SUB(NOW(), INTERVAL ? MINUTE)";
            } else {
                first = false;
               return " " + str2 + " LIKE ?";
           }
           
       }
       
       return "";
       
    }
    
    

	public static Result addEArenaAd() {
		Connection conn = null;
		PreparedStatement preparedStatement;
		

		 if (Form.form(EArenaAd.class).bindFromRequest().hasErrors()) {
		 List<String> games = new ArrayList<String>();
		 games.add("Autocomplete encountered an error");
		 return badRequest(CreateArenaAd.render(games));
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

			return redirect(routes.Application.mainearena(null, null, null, null, null));

		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest();
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

	public static List<EArenaAd> getEArenaAds(String search, String game, String username, String players, String minutes) {
	        
		    Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<EArenaAd> adList = new ArrayList<EArenaAd>();
			
			try {
			    
		    if((!search.isEmpty() && search != null) && (game.isEmpty() && game == null) && ( username.isEmpty() && username == null) && (players.isEmpty() && players == null) && ( minutes.isEmpty() && minutes == null )){
		        
		        conn = DB.getConnection();
                search = "%" + search + "%";
				String selectAdvSearch = "SELECT * FROM EArena WHERE arenaname LIKE ? OR admin LIKE ? OR arenainformation LIKE ? OR gamename LIKE ? OR playersrequired LIKE ?";
				
				preparedStatement = conn.prepareStatement(selectAdvSearch);
				preparedStatement.setString(1, search);
				preparedStatement.setString(2, search);
				preparedStatement.setString(3, search);
				preparedStatement.setString(4, search);
				preparedStatement.setString(5, search);
			    
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					EArenaAd a = new EArenaAd();
					a.arenaID = rs.getInt("arenaID");
					a.arenaName = rs.getString("arenaname");
					a.information = rs.getString("arenainformation");
					a.gameName = rs.getString("gamename");
					a.playersRequired = rs.getInt("playersrequired");
					a.admin = rs.getString("admin");
					adList.add(a);
				}
				rs.close();
				

				
		    } else if ((!search.isEmpty() && search != null) || (!game.isEmpty() && game != null) || (!username.isEmpty() && username != null) || (!players.isEmpty() && players != null) || (!minutes.isEmpty() && minutes != null)) {
		        
		        first = true;
		        conn = DB.getConnection();
		        
		        
                search = wildcard(search);
                game = wildcard(game);
                username = wildcard(username);
                
				String selectAdvSearch = "SELECT * FROM EArena WHERE";
				
				selectAdvSearch += generateSQL(search, "arenaname");
				selectAdvSearch += generateSQL(search, "arenainformation");
				selectAdvSearch += generateSQL(game, "gamename");
				selectAdvSearch += generateSQL(username, "admin");
				selectAdvSearch += generateSQL(players, "playersrequired");
				selectAdvSearch += generateSQL(minutes, "created_date");
				
				
				preparedStatement = conn.prepareStatement(selectAdvSearch);
					
				int counter = 1; 
				
				if (selectAdvSearch.contains("arenaname")){
				preparedStatement.setString(counter, search);
				counter++;
				} 
				
				if (selectAdvSearch.contains("arenainformation")){
				preparedStatement.setString(counter, search);
				counter++;
				} 
				
				if (selectAdvSearch.contains("gamename")){
				preparedStatement.setString(counter, game);
				counter++;
				} 
				
				if (selectAdvSearch.contains("admin")){
				preparedStatement.setString(counter, username);
				counter++;
				} 
				
				if (selectAdvSearch.contains("playersrequired")){
				preparedStatement.setString(counter, players);
				counter++;
				} 
				
				if (selectAdvSearch.contains("created_date")){
				preparedStatement.setString(counter, minutes);
				counter++;
				} 
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					EArenaAd a = new EArenaAd();
					a.arenaID = rs.getInt("arenaID");
					a.arenaName = rs.getString("arenaname");
					a.information = rs.getString("arenainformation");
					a.gameName = rs.getString("gamename");
					a.playersRequired = rs.getInt("playersrequired");
					a.admin = rs.getString("admin");
					adList.add(a);
				}
				
				first = true;
				rs.close();
		    
		    } else {
                
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
					a.admin = rs.getString("admin");
					adList.add(a);
				}
				
			
				
				rs.close();
		    }

				
				return adList;
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
	
	
	public static List<String> getEArenaGames() {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<String> games = new ArrayList<String>();
			try {

				conn = DB.getConnection();
				String selectAdminAds = "SELECT gamename FROM Games";
				preparedStatement = conn.prepareStatement(selectAdminAds);

				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					games.add(rs.getString("gamename"));
				}

				rs.close();
				return games;
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
				return null;
			} catch (NumberFormatException nfe) {
				return null;
			} catch (SQLException se) {
				// Handle sql errors
				return null;
			} catch (Exception e) {
				// Handle errors for Class.forName
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
				a.arenaID = id;
			}

			if (a.admin == null ) {
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
	
	public static Result addReply() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();

		String adID = json.findPath("adID").textValue();
		String contents = json.findPath("contents").textValue();
		
		String currentUser = session("connected");
		try {
			int adIDint = Integer.parseInt(adID);
			conn = DB.getConnection();
			String insertIntoDatabase = "INSERT INTO EArenaReply (arenaID, username,replycontent) VALUES(?,?,?)";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setInt(1, adIDint);
			preparedStatement.setString(2, currentUser);
			preparedStatement.setString(3, contents);


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
	
	
	public static List<AdReply> getEArenaReplies(Integer id) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		List<AdReply> adReplyList = new ArrayList<AdReply>();
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM EArenaReply WHERE arenaID=?;";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, id);

			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				AdReply a = new AdReply();
				a.content = rs.getString("replycontent");
				a.user = rs.getString("username");
				adReplyList.add(a);
			}

			if (adReplyList == null ) {
				return null;
			}

			return adReplyList;

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
	
	public static Result updateEArenaAd() {
		Connection conn = null;
		PreparedStatement preparedStatement;
		JsonNode json = request().body().asJson();

		String arenaName = json.findPath("arenaName").textValue();
		String information = json.findPath("information").textValue();
		String gameName = json.findPath("gameName").textValue();
		String strplayersRequired = json.findPath("playersRequired").textValue();
		String strid = json.findPath("id").textValue();
		
		try {
			int playersRequired = Integer.parseInt(strplayersRequired);
			int id = Integer.parseInt(strid);
			conn = DB.getConnection();
			String insertIntoDatabase = "UPDATE EArena SET arenaname=?, arenainformation=?, playersrequired=?, gamename=? WHERE arenaID=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			
			preparedStatement.setString(1, arenaName);
			preparedStatement.setString(2, information);
			preparedStatement.setInt(3, playersRequired);
			preparedStatement.setString(4, gameName);
			preparedStatement.setInt(5, id);
			
			preparedStatement.executeUpdate();

			return ok("yeees");

		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString() + "WOW");
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
	
	public static Result getMyEArenaAds() {
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

    String selectAdminAds = "SELECT * FROM EArena WHERE admin=?";
    preparedStatement = conn.prepareStatement(selectAdminAds);
    preparedStatement.setString(1, currentUser);
    ResultSet rs = preparedStatement.executeQuery();

    while (rs.next()) {
     EArenaAd a = new EArenaAd();
     a.arenaID = rs.getInt("arenaID");
     a.arenaName = rs.getString("arenaname");
     a.information = rs.getString("arenainformation");
     a.gameName = rs.getString("gamename");
     a.playersRequired = rs.getInt("playersrequired");
     a.admin = rs.getString("admin");
     adList.add(a);
    }

    rs.close();
    return ok(myearenapage.render(adList));
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
	

}
