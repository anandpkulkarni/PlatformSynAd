package models.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ErrorCodes {

	private static Map<String, Object[]> hmapServiceErrorDetails = new HashMap<String, Object[]>();

	public static int getErrorCodeByService(String service) {
		if (hmapServiceErrorDetails.containsKey(service)) {
			return (Integer)hmapServiceErrorDetails.get(service)[0];
		}
		else {
			return -1;
		}
	}

	public static String getMessageByService(String service) {
		if (hmapServiceErrorDetails.containsKey(service)) {
			return (String)hmapServiceErrorDetails.get(service)[1];
		}
		else {
			return "Invalid ErrorCode";
		}
	}

	public static String getServicePath(String service) {
		if (service.matches("(.)*(\\d)(.)*")) {
			String serviceTemplate = "";
			
			StringTokenizer tokens = new StringTokenizer(service, "/");
			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (token.matches("(.)*(\\d)(.)*") || token.equals(token.toUpperCase())) {
					token = "VAL";
				}
				serviceTemplate += "/" + token;
			}
			if (serviceTemplate.charAt(serviceTemplate.length() - 1) == '/') {
				serviceTemplate.substring(0, serviceTemplate.length());
			}
			return serviceTemplate;
		}
		else {
			return service;
		}
	}
	
	
	static {

		hmapServiceErrorDetails.put("GET /assets/*file", new Object[]{2001, "finding resource file"});

		hmapServiceErrorDetails.put("POST /login", new Object[]{2002, "login"});
		hmapServiceErrorDetails.put("DELETE /logout", new Object[]{2003, "logout"});
	}
}
