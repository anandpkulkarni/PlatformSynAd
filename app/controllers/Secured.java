/**
 * 
 */
package controllers;

import models.service.SigninService;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security.Authenticator;

/**
 * @author anandk
 *
 */
public class Secured extends Authenticator {
	
	@Override
	public String getUsername(Context ctx) {
		String token = ctx.request().getHeader("auth-token");
		return SigninService.validateToken(token);
	}
	
	@Override
	public Result onUnauthorized(Context ctx) {
		return unauthorized();
	}

}
