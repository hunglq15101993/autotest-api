package com.msb.stp.utils;

import java.util.Map;
import java.util.stream.Collectors;

public class CurlUtil {

	public static String buildCurl(
	        String method,
	        String url,
	        Map<String, String> headers,
	        Map<String, String> params,
	        Object body) {

	    StringBuilder curl = new StringBuilder("curl -X ").append(method.toUpperCase());

	    // headers
	    headers.forEach((k, v) -> curl.append(" -H '").append(k).append(": ").append(v).append("'"));

	    // body
	    if (body != null) {
	        String bodyString = (body instanceof String) ? (String) body : body.toString();
	        curl.append(" -d '").append(bodyString.replace("'", "\\'")).append("'");
	    }

	    // query params
	    if (!params.isEmpty()) {
	        url += "?" + params.entrySet()
	                           .stream()
	                           .map(e -> e.getKey() + "=" + e.getValue())
	                           .collect(Collectors.joining("&"));
	    }

	    curl.append(" '").append(url).append("'");

	    return curl.toString();
	}
	
}
