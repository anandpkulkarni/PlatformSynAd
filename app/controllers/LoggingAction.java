package controllers;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import models.constants.ErrorCodes;
import models.exception.SynAdException;
import play.Play;
import play.libs.F;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.SimpleResult;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoggingAction extends Action.Simple {

	@Override
	public Promise<SimpleResult> call(Context ctx) throws Throwable {

		try {
			return delegate.call(ctx);
		}
		catch (Exception e) {
			
			int errorCode = ErrorCodes.getErrorCodeByService(ctx.request().method() + " " + ErrorCodes.getServicePath(ctx.request().path()));
			String errorMsg = "Error in " + ErrorCodes.getMessageByService(ctx.request().method() + " " + ErrorCodes.getServicePath(ctx.request().path()));
			
			// Find out if it is a business error
			boolean businessError = false;
			if (e instanceof SynAdException || (e.getCause() != null && e.getCause() instanceof SynAdException)) {
				businessError = true;
			}
			
			if (! businessError) {
				// Report it to admin
				Map<String,String[]> params = new HashMap<String,String[]>();
				String[] uri = new String[] {ctx._requestHeader().uri()};
				String[] userName = new String[] { ctx.request().username()};
				String[] excTrace = new String[] {exceptionStackTraceWithReason(e)};
				String[] platformBaseUrl = new String[] {Play.application().configuration().getString("application.baseUrl")};
				params.put("-platformBaseUrl-", platformBaseUrl);
				params.put("-username-", userName);
				params.put("-uri-", uri);
				params.put("-excTrace-", excTrace);
			}
			
			ctx.response().setContentType("application/json");
			return createErrorResult(e, true, errorCode, errorMsg);
		}
	}
	
	private Promise<SimpleResult> createErrorResult(Exception exc, final boolean isUserError, int errorCode, String errorMsg) {
		final ObjectNode data = Json.newObject();

		ObjectNode error = Json.newObject();
		if(exc != null && exc.getMessage() != null){
			error.put("error", exc.getMessage().replaceFirst("models.exception.SynAdException:", ""));
			error.put("errorCodeMsg", errorMsg);
			error.put("errorCode", errorCode);
		}
		data.put("data", error);

		Promise<SimpleResult> errPromise = Promise.promise(new F.Function0<SimpleResult>() {
		    public SimpleResult apply() throws Throwable {
		    	return isUserError? badRequest(data) : internalServerError(data);
		    }
		});
		return errPromise;
	}
	
	private String exceptionStackTraceWithReason(Throwable exc) {
		StringWriter str = new StringWriter();
		PrintWriter pw = new PrintWriter(str);
		do {
			exc.printStackTrace(pw);
			exc = exc.getCause();
		}
		while (exc != null);
		return str.toString();
	}
}