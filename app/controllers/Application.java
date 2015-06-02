package controllers;

import models.*;
import play.*;
import play.mvc.*;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;

import views.html.*;
import play.data.Form;
import play.db.*;
import views.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.persistence.Entity;

import play.libs.Json;

public class Application extends Controller {

	public static Result mainMethod() {
		String[] acceptedIps = new String[] {"facebookexternalhit/1.1 (http://www.facebook.com/externalhit_uatext.php)","facebookexternalhit/1.1", "Facebot"};
		String userAgent = request().getHeader("User-Agent");
		String user = session("connected");
		if (user != null) {
			return ok(main.render( user, PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}else if(Arrays.asList(acceptedIps).contains(userAgent)){
			session("connected", "facebookCrawler");
			return ok(main.render( "facebookCrawler", PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}
		else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}

	public static Result facebookExistLogin() {

		return mainMethod();
	}

	public static Result newUserPage() {
		String currentUser = session("connected");
		if (currentUser != null) {
			return ok(main.render("You are already logged in as " + currentUser
					+ " Please log out if you wish to create another account", PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}

		return ok(NewUserPage.render(""));
	}

	public static Result chooseUsername() {
		
		String currentUser = session("connected");
		if (currentUser != null) {
			return ok(main.render("You are already logged in as " + currentUser
					+ " Please log out if you wish to create another account", PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}

		return ok(ChooseUsername.render(""));
	}

	public static Result loginUserPage() {
		String[] acceptedIps = new String[] {"facebookexternalhit/1.1 (http://www.facebook.com/externalhit_uatext.php)","facebookexternalhit/1.1", "Facebot"};
		String userAgent = request().getHeader("User-Agent");
		
		String currentUser = session("connected");
		if (currentUser != null) {
			return ok(main
					.render("You are already logged in as " + currentUser, PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}else if(Arrays.asList(acceptedIps).contains(userAgent)){
			session("connected", "facebookCrawler");
			return ok(main.render( "facebookCrawler", PictureDatabase.getPicturesMainPage(), EArenaDatabase.getEArenaAdsMainPage(), TournamentDatabase.getTournamentsMainPage()));
		}
		return ok(LoginUserPage.render(""));
	}

	public static Result tournament() {
		String user = session("connected");
		if (user != null) {
			return ok(CreateTournamentPage.render("You are logged in as "
					+ user));
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}

	public static Result logout() {
		session().clear();
		return redirect(routes.Application.loginUserPage());
	}

	public static Result showTournament(Integer id) {
		String user = session("connected");
		if (user != null) {
			if (user.equals(TournamentDatabase.getTournamentAdmin(id).tournamentcreator)) {

				return ok(EditTournament.render(TournamentDatabase
						.getTournament(id), TournamentDatabase.getParticipants(id), TournamentDatabase.getPending(id)));
			} else {
				return ok(ShowTournament.render(TournamentDatabase.getTournament(id), TournamentDatabase.getParticipants(id), user));
			}
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}



	public static Result showIndividualAd(Integer id) {
		String user = session("connected");
		if (user != null) {
			if (user.equals(EArenaDatabase.getIndividualEArena(id).admin)) {
				return ok(EditEArena.render(
						EArenaDatabase.getIndividualEArena(id),
						EArenaDatabase.getEArenaReplies(id)));
			} else {
				return ok(ShowIndividualEArena.render(
						EArenaDatabase.getIndividualEArena(id),
						EArenaDatabase.getEArenaReplies(id)));
			}
		} else {
			return unauthorized(LoginUserPage
					.render("Please login to access this feature!"));

		}
	}



	public static Result createArenaAd() {
		String user = session("connected");
		if (user != null) {
			return ok(CreateArenaAd.render(EArenaDatabase.getEArenaGames()));
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}
	
    public static Result newPicture(){
    	return ok(NewPicturePage.render());
    }

	public static Result showProfile(String userUrl) {
		String user = session("connected");
		if (user != null) {
			if (user.equals(userUrl)) {
				return ok(MyProfile.render(UserProfileDatabase
						.getProfile(userUrl), TournamentDatabase.getJoinedTournaments(userUrl)));
			} else {
				return ok(OtherUserProfile.render(UserProfileDatabase
						.getProfile(userUrl), TournamentDatabase.getJoinedTournaments(userUrl)));
			}
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}

	public static Result getMyProfile() {
		String user = session("connected");
		if (user != null) {
			return ok(MyProfile.render(UserProfileDatabase.getProfile(user), TournamentDatabase.getJoinedTournaments(user)));
		} else {
			return unauthorized(LoginUserPage
					.render("Welcome, login to explore the website"));
		}
	}

			 public static Result showPacklist() {
				String user = session("connected");
			 		if (user != null){
			 			return ok(MyPacklist.render(PacklistDatabase.getPacklist(user))); 
			 	 } else {
			 	 	return unauthorized(LoginUserPage
			 	 			.render("Welcome, login to explore the website"));
			 	 }
			 }




}
