package controllers;

import models.*;

import java.sql.*;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.fasterxml.jackson.databind.JsonNode;

import javax.imageio.ImageIO;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.*;
import play.db.DB;
import play.mvc.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UserProfileDatabase extends Controller {

	public static Profile getProfile(String user) {

		Connection conn = null;
		PreparedStatement preparedStatement = null;

		Profile p = new Profile();
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "SELECT * FROM UserProfile WHERE username=?;";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, user);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.isBeforeFirst()) {
				rs.next();
				p.username = rs.getString("username");
				p.userbio = rs.getString("userbio");
				p.skypeID = rs.getString("skypeID");
				p.steamID = rs.getString("steamID");
				p.battlenetID = rs.getString("battlenetID");
				p.uplayID = rs.getString("uplayID");
				p.twitchID = rs.getString("twitchID");
				p.userID = rs.getInt("userID");
				p.csgorank = rs.getString("csgorank");
				p.wow2v2rating = rs.getString("wow2v2");
				p.wow3v3rating = rs.getString("wow3v3");
				p.wow5v5rating = rs.getString("wow5v5");
				p.wowrbgrating = rs.getString("wowrbg");
				p.lolrank = rs.getString("lolrank");
				p.dota2rank = rs.getString("dota2mmr");
				p.otherranks = rs.getString("otherranks");
			}
			
			return p;

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

	public static Result addUserProfile() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String username = session("connected");
		try {

			conn = DB.getConnection();
			String insertIntoDatabase = "INSERT INTO UserProfile (username) VALUES(?)";

			preparedStatement = conn.prepareStatement(insertIntoDatabase);

			preparedStatement.setString(1, username);

			preparedStatement.executeUpdate();
			return ok();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString() + "HEYEYEY");
		} catch (NumberFormatException nfe) {
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

	public static Result editUserProfile() {

		Connection conn = null;
		PreparedStatement preparedStatement = null;
		JsonNode json = request().body().asJson();

		String username = session("connected");
		// String avatarIDstring = json.findPath("avatarID").textValue();
		String skypeID = json.findPath("skypeID").textValue();
		String battlenetID = json.findPath("battlenetID").textValue();
		String steamID = json.findPath("steamID").textValue();
		String twitchID = json.findPath("twitchID").textValue();
		String uplayID = json.findPath("uplayID").textValue();
		String userbio = json.findPath("about").textValue();

		String csgorank = json.findPath("csgorank").textValue();
		String wow2v2 = json.findPath("wow2v2").textValue();
		String wow3v3 = json.findPath("wow3v3").textValue();
		String wow5v5 = json.findPath("wow5v5").textValue();
		String wowrbg = json.findPath("wowrbg").textValue();
		String lolrank = json.findPath("lolrank").textValue();
		String dota2mmr = json.findPath("dota2mmr").textValue();
		String otherranks = json.findPath("otherranks").textValue();

		try {
			conn = DB.getConnection();
			// int avatarID = Integer.parseInt(avatarIDstring);
			String insertIntoDatabase = "UPDATE UserProfile SET userbio=?, skypeID=?, steamID=?, battlenetID=?, uplayID=?, twitchID=?,csgorank=?,wow2v2=?,wow3v3=?,wow5v5=?,wowrbg=?,lolrank=?,dota2mmr=?,otherranks=? WHERE username=?";

			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, userbio);
			preparedStatement.setString(2, skypeID);
			preparedStatement.setString(3, steamID);
			preparedStatement.setString(4, battlenetID);
			preparedStatement.setString(5, uplayID);
			preparedStatement.setString(6, twitchID);
			preparedStatement.setString(7, csgorank);
			preparedStatement.setString(8, wow2v2);
			preparedStatement.setString(9, wow3v3);
			preparedStatement.setString(10, wow5v5);
			preparedStatement.setString(11, wowrbg);
			preparedStatement.setString(12, lolrank);
			preparedStatement.setString(13, dota2mmr);
			preparedStatement.setString(14, otherranks);
			preparedStatement.setString(15, username);
			preparedStatement.executeUpdate();
			return ok("Edited!");
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return badRequest(ice.toString() + "HEYEYEY");
		} catch (NumberFormatException nfe) {
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

	public static Result getProfiles(int page) {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Profile> proList = new ArrayList<Profile>();
			try {
				page = (page - 1) * 10;
				conn = DB.getConnection();

				String selectProfiles = "SELECT * FROM UserProfile ORDER BY userID DESC LIMIT 10 OFFSET ? ";
				preparedStatement = conn.prepareStatement(selectProfiles);
				preparedStatement.setInt(1, page);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					Profile p = new Profile();

					p.username = rs.getString("username");
					p.userbio = rs.getString("userbio");
					proList.add(p);
				}
				rs.close();
				String rowCountStatement = "SELECT COUNT(*) FROM UserProfile";
				preparedStatement = conn.prepareStatement(rowCountStatement);

				ResultSet rc = preparedStatement.executeQuery();
				rc.next();
				int rowcount = rc.getInt(1);
				rc.close();
				return ok(MainProfilePage.render(proList, rowcount));
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

	// Inner class containing image information
	public static class ImageInformation {
		public final int orientation;
		public final int width;
		public final int height;

		public ImageInformation(int orientation, int width, int height) {
			this.orientation = orientation;
			this.width = width;
			this.height = height;
		}

		public String toString() {
			return String.format("%dx%d,%d", this.width, this.height,
					this.orientation);
		}
	}

	public static ImageInformation readImageInformation(File imageFile)
			throws IOException, MetadataException, ImageProcessingException {
		Metadata metadata = ImageMetadataReader.readMetadata(imageFile);

		Directory directory = metadata
				.getFirstDirectoryOfType(ExifIFD0Directory.class);
		JpegDirectory jpegDirectory = (JpegDirectory) metadata
				.getFirstDirectoryOfType(JpegDirectory.class);

		int orientation = 1;
		try {
			orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		} catch (MetadataException me) {

		} catch (NullPointerException npe) {
			return null;
		}
		int width = jpegDirectory.getImageWidth();
		int height = jpegDirectory.getImageHeight();

		return new ImageInformation(orientation, width, height);
	}

	public static AffineTransform getExifTransformation(ImageInformation info) {

		AffineTransform t = new AffineTransform();

		switch (info.orientation) {
		case 1:
			break;
		case 2: // Flip X
			t.scale(-1.0, 1.0);
			t.translate(-info.width, 0);
			break;
		case 3: // PI rotation
			t.translate(info.width, info.height);
			t.rotate(Math.PI);
			break;
		case 4: // Flip Y
			t.scale(1.0, -1.0);
			t.translate(0, -info.height);
			break;
		case 5: // - PI/2 and Flip X
			t.rotate(-Math.PI / 2);
			t.scale(-1.0, 1.0);
			break;
		case 6: // -PI/2 and -width
			t.translate(info.height, 0);
			t.rotate(Math.PI / 2);
			break;
		case 7: // PI/2 and Flip
			t.scale(-1.0, 1.0);
			t.translate(-info.height, 0);
			t.translate(0, info.width);
			t.rotate(3 * Math.PI / 2);
			break;
		case 8: // PI / 2
			t.translate(0, info.width);
			t.rotate(3 * Math.PI / 2);
			break;
		}

		return t;
	}

	public static BufferedImage transformImage(BufferedImage originalImage,
			AffineTransform transform) throws Exception {
		AffineTransformOp affineTransformOp = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_BILINEAR);
		Rectangle2D rec = affineTransformOp.getBounds2D(originalImage);

		int height = (int) rec.getHeight();

		int width = (int) rec.getWidth();

		BufferedImage destinationImage = new BufferedImage(width, height,
				originalImage.getType());

		destinationImage = affineTransformOp.filter(originalImage,
				destinationImage);

		return destinationImage;
	}

	public static Result savePicture() {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String currentuser = session("connected");
		BufferedImage img = null;
		BufferedImage finalImg = null;
		InputStream inputStream;
		try {
			conn = DB.getConnection();

			String sql = "UPDATE UserProfile SET image=?, mimetype=? WHERE username = ?";
			preparedStatement = conn.prepareStatement(sql);

			MultipartFormData body = request().body().asMultipartFormData();

			FilePart picture = body.getFile("picture");

			if (picture != null) {
				File file = picture.getFile();
				img = ImageIO.read(file);
				String suffix = "image/";
				String type = picture.getContentType().substring(
						picture.getContentType().lastIndexOf("/") + 1);

				suffix += type;

				if (readImageInformation(file) != null) {
					ImageInformation imageF = readImageInformation(file);

					AffineTransform info = getExifTransformation(imageF);

					finalImg = transformImage(img, info);

					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(finalImg, type, os);
					inputStream = new ByteArrayInputStream(os.toByteArray());

				} else {
					inputStream = new FileInputStream(file);
				}

				preparedStatement.setBlob(1, inputStream);
				preparedStatement.setString(2, suffix);
				preparedStatement.setString(3, currentuser);
				preparedStatement.executeUpdate();

				return redirect("myprofile");

			} else {

				return ok("IMAGE WAS EMPTY");
			}

		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("null " + e.toString());
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothin
		}

	}

	public static Result deletePicture() {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String currentuser = session("connected");
		try {
			conn = DB.getConnection();

			String sql = "UPDATE UserProfile SET mimetype=? WHERE username = ?";
			preparedStatement = conn.prepareStatement(sql);

			preparedStatement.setString(1, null);
			preparedStatement.setString(2, currentuser);
			preparedStatement.executeUpdate();

			return ok(MyProfile.render(
					UserProfileDatabase.getProfile(currentuser),
					TournamentDatabase.getJoinedTournaments(currentuser)));

		} catch (Exception e) {
			// Handle errors for Class.forName
			return ok("null " + e.toString());
		} finally {
			// finally block used to close resources
			try {
				if (preparedStatement != null)
					conn.close();
			} catch (SQLException se) {
			}// do nothin
		}

	}

	public static Result render(String username) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String mimetype = null;
		Blob image = null;
		try {
			conn = DB.getConnection();

			String insertIntoDatabase = "SELECT image, mimetype FROM UserProfile WHERE username=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				mimetype = rs.getString("mimetype");
				image = rs.getBlob("image");
			}

			if (mimetype == null) {

				String mimetypeD = null;
				Blob imageD = null;
				PreparedStatement preparedStatementD = null;

				String getDefaultPicDatabase = "SELECT image, mimetype FROM UserProfile WHERE username=?";
				preparedStatementD = conn
						.prepareStatement(getDefaultPicDatabase);
				preparedStatementD.setString(1, "defaultpic");
				ResultSet rsD = preparedStatementD.executeQuery();

				while (rsD.next()) {
					mimetypeD = rsD.getString("mimetype");
					imageD = rsD.getBlob("image");
				}

				int blobLengthD = (int) imageD.length();
				byte[] bytesD = imageD.getBytes(1, blobLengthD);
				rsD.close();

				return ok(bytesD).as(mimetypeD);

			}

			rs.close();

			int blobLength = (int) image.length();
			byte[] bytes = image.getBytes(1, blobLength);

			return ok(bytes).as(mimetype);

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
