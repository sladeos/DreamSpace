package controllers;

import models.*;
import play.*;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import java.awt.Choice;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PictureDatabase extends Controller{
	
	
	public static Result savePicture(){
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String currentuser = session("connected");
		try {
			conn = DB.getConnection();
			
			MultipartFormData body = request().body().asMultipartFormData();
			  FilePart picture = body.getFile("picture");
			  if (picture != null) {
			    
			    File file = picture.getFile();
			    InputStream inputStream = new FileInputStream(file);
			    String sql = "INSERT INTO Picture (creator, image) VALUES(?,?)";
			    preparedStatement = conn.prepareStatement(sql);
			    preparedStatement.setString(1, currentuser);
			    preparedStatement.setBlob(2, inputStream);
			    preparedStatement.executeUpdate();
			    
			    return ok();
			    
			  }else{
				  
				 return ok("IMAGE WAS EMPTY");
			  }

		} catch (SQLException se) {
			return ok("null" + se.toString());
		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("null" + e.toString());
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothin
		}

	}

	
	public static Result getPictures() {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Picture> pList = new ArrayList<Picture>();

			try {

				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM Picture";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				ResultSet rs = preparedStatement.executeQuery();
				//ByteArrayInputStream input = new ByteArrayInputStream(pic);
				
				byte[] img = null;
				while (rs.next()) {
					Picture p = new Picture();
					p.creator = rs.getString("creator");
					p.image = rs.getBlob("image");
					p.pic = rs.getBytes("image");
					
					pList.add(p);
				}

				rs.close();
				return ok(PicturePage.render(pList));
				
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