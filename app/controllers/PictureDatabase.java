package controllers;

import models.*;
import play.*;
import play.mvc.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;
//Imports for picture
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.Response;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConstants;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PictureDatabase extends Controller {

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
		String[] acceptedTypes = new String[] {"jpeg", "jpg", "jfif", "jpeg 2000", "tiff", "riff", "png", "gif","bmp", "png", "jpeg xr", "img", "bpg", "webp" };
		try {
			conn = DB.getConnection();

			String sql = "INSERT INTO Picture (creator, image, mimetype) VALUES(?,?,?)";
			preparedStatement = conn.prepareStatement(sql);

			MultipartFormData body = request().body().asMultipartFormData();
			FilePart picture = body.getFile("picture");

			if (picture != null) {
				File file = picture.getFile();
				img = ImageIO.read(file);
				String suffix = "image/";
				String type = picture.getContentType().substring(
						picture.getContentType().lastIndexOf("/") + 1);
				if(!Arrays.asList(acceptedTypes).contains(type)){
					return badRequest("That file format is not supported");
				}

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

				preparedStatement.setString(1, currentuser);
				preparedStatement.setBlob(2, inputStream);
				preparedStatement.setString(3, suffix);
				preparedStatement.executeUpdate();

				return redirect(routes.PictureDatabase.getPictures(1));

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

	public static Result getPictures(int page) {
		String currentUser = session("connected");
		if (currentUser == null) {
			return unauthorized(LoginUserPage
					.render("You have to login to access this page!"));
		} else {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Picture> pList = new ArrayList<Picture>();

			try {
				page = (page - 1) * 10;
				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM Picture ORDER BY created_date DESC LIMIT 10 OFFSET ?";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				preparedStatement.setInt(1, page);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					Picture p = new Picture();
					p.creator = rs.getString("creator");
					p.pictureID = rs.getInt("pictureID");
					pList.add(p);
				}

				rs.close();

				String rowCountStatement = "SELECT COUNT(*) FROM Picture";
				preparedStatement = conn.prepareStatement(rowCountStatement);

				ResultSet rc = preparedStatement.executeQuery();
				rc.next();
				int rowcount = rc.getInt(1);
				rc.close();
				return ok(PicturePage.render(pList, rowcount));

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

	public static Result render(int id) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		String mimetype = null;
		Blob image = null;
		try {
			conn = DB.getConnection();

			String insertIntoDatabase = "SELECT * FROM Picture WHERE pictureID=?";
			preparedStatement = conn.prepareStatement(insertIntoDatabase);
			preparedStatement.setInt(1, id);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				mimetype = rs.getString("mimetype");
				image = rs.getBlob("image");
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


public static List<Picture> getPicturesMainPage() {
			Connection conn = null;
			PreparedStatement preparedStatement = null;
			List<Picture> pList = new ArrayList<Picture>();

			try {
				conn = DB.getConnection();

				String insertIntoDatabase = "SELECT * FROM Picture ORDER BY created_date DESC LIMIT 3";
				preparedStatement = conn.prepareStatement(insertIntoDatabase);
				ResultSet rs = preparedStatement.executeQuery();

				while (rs.next()) {
					Picture p = new Picture();
					p.creator = rs.getString("creator");
					p.pictureID = rs.getInt("pictureID");
					pList.add(p);
				}

				rs.close();

				return pList;

		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ice) {
			return null;
		} catch (NumberFormatException nfe) {
	        return null;
		} catch (SQLException se) {
    		return null;
		} catch (Exception e) {
	    	return null;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				return null;
			} // end finally try
		} // end try
	 
}


}