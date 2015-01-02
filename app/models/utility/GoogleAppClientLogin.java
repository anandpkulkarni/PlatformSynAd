package models.utility;
import java.io.File;
import java.util.Collection;
import java.util.Vector;

import models.exception.SynAdException;

import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;

public class GoogleAppClientLogin {

	public static boolean login(String userName, String password) throws Exception {

		// HttpTransport used to send login request.
	    HttpTransport transport = new NetHttpTransport();
	    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
	    try {
	      // authenticate with ClientLogin
	      ClientLogin authenticator = new ClientLogin();
	      authenticator.transport = transport;
	      
	      // Google service trying to access, e.g., "cl" for calendar.
	      authenticator.authTokenType = "apps";
	      authenticator.username = userName;
	      authenticator.password = password;
	      Response response = authenticator.authenticate();
	      String authKey = response.auth;
//	      String authHeader = response.getAuthorizationHeaderValue();
	      
	      Collection<String> accountScopes = new Vector<String>();
	      //accountScopes.add(PlusScopes.PLUS_LOGIN.toString());
	      //accountScopes.add(PlusScopes.PLUS_ME.toString());
	      //accountScopes.add("https://www.googleapis.com/auth/userinfo.profile?alt=json&access_token="+authKey);
	      
	      accountScopes.add("https://www.googleapis.com/auth/userinfo.email");
	      accountScopes.add("https://www.googleapis.com/auth/userinfo.profile");
	      accountScopes.add("https://www.googleapis.com/auth/plus.login");
	      accountScopes.add("https://www.googleapis.com/auth/plus.profile.emails.read");
	      //accountScopes.add("https://www.googleapis.com/auth/plus.email");
	      
	      //GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new FileReader(""));
	    		  
	      GoogleCredential.Builder googleBuilder = new GoogleCredential.Builder()
	      .setTransport(transport)
	      .setJsonFactory(jsonFactory)
	      .setServiceAccountId("83426981317-osakedopsh5er8npqbs26gsfbbhjrlqd@developer.gserviceaccount.com")
	      //.setServiceAccountUser("83426981317-osakedopsh5er8npqbs26gsfbbhjrlqd.apps.googleusercontent.com")
	      .setServiceAccountScopes(accountScopes)
	      .setClientSecrets("83426981317-osakedopsh5er8npqbs26gsfbbhjrlqd.apps.googleusercontent.com", "25c3487faee1064deca6564726ccd8cbadfae4ac")
	      .setClientAuthentication(response)
	      .setServiceAccountPrivateKeyFromP12File(new File("D:/Anand/Advertisement/Advertisement-25c3487faee1.p12"));
	      GoogleCredential credential = googleBuilder.build();
	      credential = credential.setAccessToken(authKey);
	      credential.refreshToken();
//	      String token = credential.getAccessToken();
//	      

Oauth2 oauth2 = new com.google.api.services.oauth2.Oauth2.Builder(new NetHttpTransport(), jsonFactory, credential).setApplicationName(
        "").build();
Userinfoplus userinfo = oauth2.userinfo().get().execute();
String strProfile = userinfo.toPrettyString();

com.google.api.services.oauth2.Oauth2.Userinfo.V2.Me.Get get = oauth2.userinfo().v2().me().get();
userinfo = get.execute();
strProfile = userinfo.toPrettyString();
	      
	      return true;
	    } catch (Exception e) {
	      // Likely a "403 Forbidden" error.
	    	e.printStackTrace();
	      throw new SynAdException("Invalid Login!!");
	      //return false;
	    }
	  }}
