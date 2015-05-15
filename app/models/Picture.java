package models;

import java.awt.Image;
import java.io.InputStream;

import javax.persistence.*;
import javax.swing.ImageIcon;

import com.mysql.jdbc.Blob;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;
public class Picture extends Model	{
	

	@Id
	public int pictureID;
	
	public String creator;
	
	public String path;
	
	public java.sql.Blob image;
	
	public Blob blob;
	
	public InputStream strBlob;

	public byte[] bBlob;

}
