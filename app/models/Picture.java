package models;

import java.awt.Image;

import javax.persistence.*;
import javax.swing.ImageIcon;

import com.mysql.jdbc.Blob;

import play.db.ebean.*;
import play.data.validation.*;
import play.db.ebean.Model;
public class Picture extends Model	{
	

	public java.sql.Blob image;
	@Id
	public int pictureID;
	
	public String creator;
	
	public byte[] pic;
	
	

}
